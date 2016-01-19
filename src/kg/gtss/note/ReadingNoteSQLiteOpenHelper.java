package kg.gtss.note;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ReadingNoteSQLiteOpenHelper extends SQLiteOpenHelper {

	public abstract interface Columns extends BaseColumns {
		public final static String TIME = "time";

		public final static String CONTENT = "content";
	}

	static String DB_NAME = "note.db";
	static int DB_VERSION = 1;
	public static String TABLE_NAME = "note";
	String CREATE = "create table " + TABLE_NAME + "(" + Columns._ID
			+ " integer default '1' not null primary key autoincrement,"
			+ Columns.TIME + " text not null," + Columns.CONTENT
			+ " text not null" + ")";
	String DROP = "drop table if exists " + TABLE_NAME;

	public ReadingNoteSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DB_NAME, factory, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	public ReadingNoteSQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL(DROP);
		this.onCreate(db);
	}

}
