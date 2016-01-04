package kg.gtss.alarm;

import kg.gtss.personalbooksassitant.R;
import android.app.Activity;

import android.app.FragmentManager;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import android.widget.Button;
import android.widget.TimePicker;

//
public class AddAlarm extends Activity implements OnClickListener {
	AddAlarmFragment mFragment;
	TimePicker mTimePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		/*
		 * this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		FragmentManager fm = this.getFragmentManager();
		this.setContentView(R.layout.add_reading_alarm_activity);
		mFragment = (AddAlarmFragment) fm
				.findFragmentById(R.id.add_reading_alarm_fragment);
		fm.beginTransaction()
				.replace(R.id.add_reading_alarm_fragment, mFragment).commit();
		Button ok = (Button) this.findViewById(R.id.add_reading_alarm_ok_btn);
		mTimePicker = (TimePicker) this
				.findViewById(R.id.add_reading_alarm_timer);
		mTimePicker.setIs24HourView(true);
		ok.setOnClickListener(this);
		Button cancel = (Button) this
				.findViewById(R.id.add_reading_alarm_cancel_btn);
		cancel.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_reading_alarm_ok_btn:
			save();
			break;
		case R.id.add_reading_alarm_cancel_btn:
			break;
		}
		finish();
	}

	void save() {

		mFragment.save(mTimePicker.getCurrentHour(),
				mTimePicker.getCurrentMinute(), mTimePicker.is24HourView());
	}
}
