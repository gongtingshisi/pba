package kg.gtss.alarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ReadingAlarmSQLiteOpenHelper extends SQLiteOpenHelper {

	public abstract interface Columns extends BaseColumns {
		public static final String READING_ALARM_ON = "on";
		public static final String READING_ALARM_TIME = "time";
		public static final String READING_ALARM_VIBRATE = "vibrate";
		public static final String READING_ALARM_COMMENT = "comment";
	}

	public static final String READING_ALARM_DB_NAME = "reading_alarm.db";
	public static final String READING_ALARM_TABLE = "reading_alarm";
	private int DB_VERSION = 1;
	private String CREATE = "create table " + READING_ALARM_TABLE + "(" + ")";

	public ReadingAlarmSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
