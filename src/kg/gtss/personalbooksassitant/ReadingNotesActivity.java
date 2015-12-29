package kg.gtss.personalbooksassitant;

import kg.gtss.alarm.AddReadingAlarm;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ReadingNotesActivity extends AddReadingAlarm {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setTitle("阅读笔记");
		mAddBtn.setText(this.getString(R.string.add_note));
		mAddBtn.setTag(this);
		mListView.setAdapter(null);
		
	}
}
