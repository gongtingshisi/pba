package kg.gtss.personalbooksassitant;

import java.io.ByteArrayOutputStream;

import kg.gtss.utils.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * a helper to pass parcelable data from zxing to pbamain.Strong & Wonderful
 * ,haha:) author:fengzhang. date:2015.12.3
 * */
public class ZxingDataParcelable implements Parcelable {
	// Bitmap bitmap;
	public String format;
	public String type;
	public String time;
	public String meta;
	public String isbn;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = "format:" + format + ",type:" + type + ",time:" + time
				+ ",meta:" + meta + ",isbn:" + isbn;

		return s;
	}

	/**
	 * 图片序列化.bitmap->bytes
	 * */
	public static byte[] getBytes(Bitmap bitmap) {
		// 实例化字节数组输出流
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);// 压缩位图
		return baos.toByteArray();// 创建分配字节数组
	}

	/**
	 * 图片反序列化.bytes->bitmap
	 * */
	public static Bitmap getBitmap(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);// 从字节数组解码位图
	}

	public ZxingDataParcelable(/* Bitmap b, */String f, String t, String time,
			String m, String i) {

		// this.bitmap = b;
		this.format = f;
		this.type = t;
		this.time = time;
		this.meta = m;
		this.isbn = i;

	}

	public ZxingDataParcelable(Parcel p) {
		// TODO Auto-generated constructor stub
		/* Log.v(this, "contruct ZxingDataParcelable using Parcel"); */
		format = p.readString();
		type = p.readString();
		time = p.readString();
		meta = p.readString();
		isbn = p.readString();
		// 反序列化图片
		// p.readByteArray(byteDraw);
		// bitmap=getBitmap(byteDraw);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		// TODO Auto-generated method stub
		/* Log.v(this, "writeToParcel"); */
		dest.writeString(format);
		dest.writeString(type);
		dest.writeString(time);
		dest.writeString(meta);
		dest.writeString(isbn);
		// 序列化图片
		// byteDraw=getBytes(bitmap);
		// dest.writeByteArray(byteDraw);
	}

	public static final Parcelable.Creator<ZxingDataParcelable> CREATOR = new Parcelable.Creator<ZxingDataParcelable>() {
		/*
		 * @Override public Param createFromParcel(Parcel source) { return new
		 * ZxingDataParcelable(source); }
		 */
		@Override
		public ZxingDataParcelable[] newArray(int size) {
			/*
			 * Log.v(this,
			 * "Parcelable.Creator<ZxingDataParcelable> CREATOR newArray");
			 */
			return new ZxingDataParcelable[size];
		}

		@Override
		public ZxingDataParcelable createFromParcel(Parcel arg0) {
			// TODO Auto-generated method stub
			/*
			 * Log.v(this,
			 * "Parcelable.Creator<ZxingDataParcelable> CREATOR createFromParcel"
			 * );
			 */
			return new ZxingDataParcelable(arg0);
		}
	};

}
