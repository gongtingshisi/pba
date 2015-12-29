package kg.gtss.alarm;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

public class AddAlarmFragment extends PreferenceFragment implements
		OnPreferenceChangeListener {
	SwitchPreference mSwitchPreference;
	SwitchPreference mVibrateSwitchPreference;
	EditTextPreference mEditTextPreference;

	final String ON = "switch";
	final String VIBRATE = "vibrate";
	final String COMMENT = "comment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.addPreferencesFromResource(R.xml.add_alarm);
		mSwitchPreference = (SwitchPreference) this.findPreference(ON);
		mVibrateSwitchPreference = (SwitchPreference) this
				.findPreference(VIBRATE);
		mEditTextPreference = (EditTextPreference) this.findPreference(COMMENT);
	}

	void save(int hour, int minute, boolean is24) {
		boolean on = mSwitchPreference.isChecked();
		boolean vibrate = mVibrateSwitchPreference.isChecked();
		String comment = mEditTextPreference.getEditText().getText().toString();
		Log.v(this, "save Hour:" + hour + " Minute:" + minute + " 24:" + is24
				+ " on:" + on + " vibrate:" + vibrate + " comment:" + comment);
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
