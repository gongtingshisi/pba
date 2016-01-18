package kg.gtss.alarm;

import kg.gtss.utils.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ReadingAlarmSQLiteOpenHelper extends SQLiteOpenHelper {

	public abstract interface Columns extends BaseColumns {
		public static final String READING_ALARM_ON = "_on";// NOT allowed on

		public static final String READING_ALARM_TIME = "_time";// NOT allowed
																// time
		public static final String READING_ALARM_VIBRATE = "vibrate";
		public static final String READING_ALARM_COMMENT = "comment";
		public static final String READING_ALARM_MUTE = "mute";
		// 用来操作pending intent的句柄
		public static final String READING_ALARM_PENDING_INTENT = "pending_id";
	}

	public static final String READING_ALARM_DB_NAME = "reading_alarm.db";
	public static final String READING_ALARM_TABLE = "reading_alarm";
	private static int DB_VERSION = 1;
	private String DROP = "drop it if exists " + READING_ALARM_TABLE;
	private String CREATE = "create table " + READING_ALARM_TABLE + "("
			+ Columns._ID
			+ " integer default '1' not null primary key autoincrement,"
			+ Columns.READING_ALARM_ON + " integer not null,"
			+ Columns.READING_ALARM_TIME + " long not null,"
			+ Columns.READING_ALARM_VIBRATE + " integer not null,"
			+ Columns.READING_ALARM_COMMENT + " text not null,"
			+ Columns.READING_ALARM_PENDING_INTENT + " integer not null,"
			+ Columns.READING_ALARM_MUTE + " integer not null" + ")";

	public ReadingAlarmSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, READING_ALARM_DB_NAME, factory, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	public ReadingAlarmSQLiteOpenHelper(Context context) {
		super(context, READING_ALARM_DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v(this, CREATE);
		db.execSQL(CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL(DROP);
		onCreate(db);
	}

}
