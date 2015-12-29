package kg.gtss.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

//http://keegan-lee.diandian.com/post/2012-12-06/40047548955
/**
 * �ļ������ࣺImageFileCache.pictures are cached in local files.
 * */
public class ImageFileCache {
	private static String TAG = "ImageFileCache";

	private static final String WHOLESALE_CONV = ".cach";

	private Activity mContext;

	private static final int MB = 1024 * 1024;
	private static final int CACHE_SIZE = 10;
	private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

	private boolean useInternalCache = true;

	public ImageFileCache(Activity con) {
		mContext = con;
		// �����ļ�����
		removeCache(getDirectory());
	}

	/** �ӻ����л�ȡͼƬ **/
	public Bitmap getImage(final String url) {
		final String path = getDirectory() + "/" + convertUrlToFileName(url);
		File file = new File(path);
		if (file.exists()) {
			Bitmap bmp = BitmapFactory.decodeFile(path);
			if (bmp == null) {
				file.delete();
			} else {
				updateFileTime(path);
				return bmp;
			}
		}
		return null;
	}

	/** ��ͼƬ�����ļ����� **/
	public void saveBitmap(Bitmap bm, String url) {
		if (bm == null) {
			return;
		}
		// �ж�sdcard�ϵĿռ�
		if (!useInternalCache
				&& FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			// SD�ռ䲻��
			return;
		}
		String filename = convertUrlToFileName(url);
		String dir = getDirectory();
		Log.v(TAG, "dir:" + dir);
		File dirFile = new File(dir);
		if (!dirFile.exists())
			dirFile.mkdirs();
		File file = new File(dir + "/" + filename);
		try {
			file.createNewFile();
			OutputStream outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			Log.w("ImageFileCache", "FileNotFoundException");
		} catch (IOException e) {
			Log.w("ImageFileCache", "IOException");
		}
		if (dirFile.isDirectory() && dirFile.list() != null)
			for (String f : dirFile.list()) {
				Log.v(TAG, "cache file img:" + dir + "/" + f);
			}
	}

	/**
	 * ����洢Ŀ¼�µ��ļ���С��
	 * ���ļ��ܴ�С���ڹ涨��CACHE_SIZE����sdcardʣ��ռ�С��FREE_SD_SPACE_NEEDED_TO_CACHE�Ĺ涨
	 * ��ôɾ��40%���û�б�ʹ�õ��ļ�
	 */
	private boolean removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null) {
			return true;
		}
		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return false;
		}

		int dirSize = 0;
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().contains(WHOLESALE_CONV)) {
				dirSize += files[i].length();
			}
		}

		if (dirSize > CACHE_SIZE * MB
				|| FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifSort());
			for (int i = 0; i < removeFactor; i++) {
				if (files[i].getName().contains(WHOLESALE_CONV)) {
					files[i].delete();
				}
			}
		}

		if (freeSpaceOnSd() <= CACHE_SIZE) {
			return false;
		}

		return true;
	}

	/** �޸��ļ�������޸�ʱ�� **/
	public static void updateFileTime(String path) {
		File file = new File(path);
		long newModifiedTime = System.currentTimeMillis();
		file.setLastModified(newModifiedTime);
	}

	/** ����sdcard�ϵ�ʣ��ռ� **/
	private static int freeSpaceOnSd() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

	/** ��urlת���ļ��� **/
	private static String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		return strs[strs.length - 1] + WHOLESALE_CONV;
	}

	private String getInternalDir() {
		return mContext.getCacheDir().getAbsolutePath();
	}

	/** ��û���Ŀ¼ **/
	private String getDirectory() {
		String dir = useInternalCache ? getInternalDir() : getSDPath() + "/"
				+ Common.SdCardCacheDirName;
		return dir;
	}

	/** ȡSD��·�� **/
	private String getSDPath() {
		String sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory().getAbsolutePath(); // ��ȡ��Ŀ¼
		}
		if (sdDir != null) {
			Log.v(TAG, "------->" + sdDir.toString());
			return sdDir.toString();
		} else {
			return "";
		}
	}

	/**
	 * �����ļ�������޸�ʱ���������
	 */
	private class FileLastModifSort implements Comparator<File> {
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}

}
