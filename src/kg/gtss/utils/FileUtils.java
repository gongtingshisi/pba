package kg.gtss.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * A class to handle File.
 * */
public class FileUtils {
	private static String TAG = "FileUtils";
	private static String SDPATH;

	static {
		Log.v(TAG, "static");
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	public static String getSDPATH() {
		return SDPATH;
	}

	public static File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	public static File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdirs();//NOT mkdir()
		return dir;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	public static Uri getPicturesUri(String path, String fileName) {
		File f = new File(SDPATH + Common.SdCardCacheDirName + path + fileName);
		if (f.exists()) {
			return Uri.fromFile(f);
		} else {
			return null;
		}
	}

	public static String getPicturespath(String path, String fileName) {
		File f = new File(SDPATH + Common.SdCardCacheDirName + path + fileName);
		if (f.exists()) {
			return f.getAbsolutePath();
		} else {
			return null;
		}
	}

	/**
	 * write file to dir on sdcard from inputstream.param1:douban or
	 * google,param2:filename
	 * */
	public static File write2SDFromInput(String path, String fileName,
			InputStream input) {

		Log.v(TAG, "write2SDFromInput " + SDPATH + Common.SdCardCacheDirName
				+ path + fileName);
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(Common.SdCardCacheDirName + path);
			file = createSDFile(Common.SdCardCacheDirName + path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return file;
	}

	/**
	 * to copy inputstream to dest.Caution:copying maybe take a while.
	 * */
	public static void copyBigDataTo(InputStream inStream, String strOutFileName)
			throws IOException {
		if (null == inStream)
			return;
		OutputStream myOutput = new FileOutputStream(strOutFileName);
		// myInput = this.getAssets().open(file);
		byte[] buffer = new byte[1024];
		int length = inStream.read(buffer);
		while (length > 0) {
			myOutput.write(buffer, 0, length);
			length = inStream.read(buffer);
		}
		myOutput.flush();// !!!!!!!!!VERY IMPORTANT ,cping is a long time,or
							// Text file busy.
		inStream.close();
		myOutput.close();
	}
}
