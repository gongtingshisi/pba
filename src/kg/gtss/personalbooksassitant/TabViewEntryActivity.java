package kg.gtss.personalbooksassitant;

import com.progress.bookreading.ReadingProgressActivity;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

public class TabViewEntryActivity extends ActivityGroup {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.tabview_entry);
		if (null != getActionBar()) {
			this.getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		Intent intent = new Intent();
		intent.setClass(this, GridViewEntryActivity.class);

		TabHost host = (TabHost) this.findViewById(R.id.tabhost);
		host.setup();
		host.setup(getLocalActivityManager());

		TabHost.TabSpec tab1 = host
				.newTabSpec("tab1")
				.setIndicator(
						new TabSubView(this, getString(R.string.tab_personal),
								getResources().getDrawable(
										R.drawable.composer_camera)))
				.setContent(intent);
		host.addTab(tab1);

		Intent intent2 = new Intent();
		intent2.setClass(this, FriendNewsActivity.class);

		TabHost.TabSpec tab2 = host
				.newTabSpec("tab2")
				.setIndicator(
						new TabSubView(this, getString(R.string.tab_group),
								getResources().getDrawable(
										R.drawable.composer_music)))
				.setContent(intent2);
		host.addTab(tab2);

		Intent intent3 = new Intent();
		intent3.setClass(this, PersonalSettingsActivity.class);
		TabHost.TabSpec tab3 = host
				.newTabSpec("tab3")
				.setIndicator(
						new TabSubView(this, getString(R.string.settings),
								getResources().getDrawable(
										R.drawable.composer_sleep)))
				.setContent(intent3);
		host.addTab(tab3);

		host.setCurrentTab(0);
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
