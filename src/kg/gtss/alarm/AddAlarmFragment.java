package kg.gtss.alarm;

import java.util.Calendar;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class AddAlarmFragment extends PreferenceFragment implements
		OnPreferenceChangeListener {

	Uri URI = ReadingAlarmContentProvider.CONTENT_URI;
	String COLUMN_ID = ReadingAlarmSQLiteOpenHelper.Columns._ID;
	String COLUMN_ON = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_ON;
	String COLUMN_VIBRATE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_VIBRATE;
	String COLUMN_COMMENT = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_COMMENT;
	String COLUMN_TIME = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_TIME;
	String COLUMN_MUTE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_MUTE;
	String[] PROJECTION = { COLUMN_ID, COLUMN_ON, COLUMN_VIBRATE,
			COLUMN_COMMENT, COLUMN_TIME, COLUMN_MUTE };

	SwitchPreference mSwitchPreference;
	SwitchPreference mVibrateSwitchPreference;
	SwitchPreference mMuteSwitchPreference;
	EditTextPreference mEditTextPreference;
	// PreferenceScreen mDateSetting, mTimeSetting;

	// final String DATE = "setting_date";
	// final String TIME = "setting_time";
	final String ON = "switch";
	final String VIBRATE = "vibrate";
	final String COMMENT = "comment";
	final String MUTE = "mute";

	private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker dp, int y, int m, int d) {
			// TODO Auto-generated method stub
			Log.v(this, "onDateSet:" + y + "/" + m + "/" + d);
		}
	};
	private OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker tp, int h, int m) {
			// TODO Auto-generated method stub
			Log.v(this, "onTimeSet:" + h + ":" + m);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.addPreferencesFromResource(R.xml.add_alarm);

		final Calendar c = Calendar.getInstance();
		mSwitchPreference = (SwitchPreference) this.findPreference(ON);
		mMuteSwitchPreference = (SwitchPreference) this.findPreference(MUTE);
		/*
		 * mDateSetting = (PreferenceScreen) this.findPreference(DATE);
		 * mDateSetting .setOnPreferenceClickListener(new
		 * OnPreferenceClickListener() {
		 * 
		 * @Override public boolean onPreferenceClick(Preference arg0) { // TODO
		 * Auto-generated method stub DatePickerDialog dpd = new
		 * DatePickerDialog( AddAlarmFragment.this.getActivity(), Datelistener,
		 * c.get(Calendar.YEAR), c .get(Calendar.MONTH), c
		 * .get(Calendar.DAY_OF_MONTH)); dpd.show(); return false; } });
		 * 
		 * mTimeSetting = (PreferenceScreen) this.findPreference(TIME);
		 * mTimeSetting .setOnPreferenceClickListener(new
		 * OnPreferenceClickListener() {
		 * 
		 * @Override public boolean onPreferenceClick(Preference arg0) { // TODO
		 * Auto-generated method stub TimePickerDialog tpd = new
		 * TimePickerDialog( AddAlarmFragment.this.getActivity(),
		 * mOnTimeSetListener, c.get(Calendar.HOUR_OF_DAY), c
		 * .get(Calendar.MINUTE), true); tpd.show(); return false; } });
		 */
		mVibrateSwitchPreference = (SwitchPreference) this
				.findPreference(VIBRATE);
		mEditTextPreference = (EditTextPreference) this.findPreference(COMMENT);
		mEditTextPreference.setOnPreferenceChangeListener(this);
	}

	void save(int year, int month, int day, int hour, int minute) {
		boolean on = mSwitchPreference.isChecked();
		boolean vibrate = mVibrateSwitchPreference.isChecked();
		String comment = mEditTextPreference.getEditText().getText().toString();
		boolean mute = mMuteSwitchPreference.isChecked();
		Log.v(this, "save Year:" + year + " Month:" + month + " Day:" + day
				+ " Hour:" + hour + " Minute:" + minute + " on:" + on
				+ " vibrate:" + vibrate + " comment:" + comment + "	mute:"
				+ mute);
		// /////////////////////
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);

		ContentValues value = new ContentValues();
		value.put(COLUMN_ON, on);
		value.put(COLUMN_VIBRATE, vibrate);
		value.put(COLUMN_COMMENT, comment);
		value.put(COLUMN_MUTE, mute);
		value.put(COLUMN_TIME, c.getTimeInMillis());
		this.getActivity().getContentResolver().insert(URI, value);
	}

	@Override
	public boolean onPreferenceChange(Preference pf, Object arg1) {
		// TODO Auto-generated method stub
		Log.v(this, "onPreferenceChange " + pf.getKey() + "---"
				+ mEditTextPreference.getEditText().getText().toString());
		if (COMMENT.equals(pf.getKey())) {
			mEditTextPreference.setSummary(mEditTextPreference.getEditText()
					.getText().toString());
		}
		return false;
	}
}
