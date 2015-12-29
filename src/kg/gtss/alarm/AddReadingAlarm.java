package kg.gtss.alarm;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.personalbooksassitant.R.id;
import kg.gtss.personalbooksassitant.R.layout;
import kg.gtss.personalbooksassitant.ReadingNotesActivity;
import kg.gtss.personalbooksassitant.TabSubView;
import kg.gtss.utils.Log;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * add an alarm for reading
 * */
public class AddReadingAlarm extends Activity implements OnClickListener {
	public ListView mListView;
	public TabSubView mAddBtn;

	// TabSubView mSettingsBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setTitle("添加阅读提醒");
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.setContentView(R.layout.add_reading_alarm);
		mListView = (ListView) this.findViewById(R.id.add_alarm_list);
		mListView.setAdapter(new ReadingAlarmAdapter(this));
		
		mAddBtn = (TabSubView) this.findViewById(R.id.add_alarm_add_btn);
		// mSettingsBtn = (TabSubView) this
		// .findViewById(R.id.add_alarm_settings_btn);
		mAddBtn.setOnClickListener(this);
		// mSettingsBtn.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_alarm_add_btn:
			if (v.getTag() instanceof ReadingNotesActivity) {
				Log.v(this, "create a note ~");
				return;
			}
			Intent i = new Intent();
			i.setClass(this, AddAlarm.class);
			this.startActivity(i);
			break;
		// case R.id.add_alarm_settings_btn:
		// break;
		default:
			break;
		}
	}
}
