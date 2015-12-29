package kg.gtss.personalbooksassitant;

import kg.gtss.utils.Log;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * an entry activity
 * */
public class GridViewEntryActivity extends Activity {
	GridView mGridView;
	GridViewEntryAdapter mGridViewEntryAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (null != getActionBar()) {
			this.getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		this.setContentView(R.layout.gridview_entry);
		this.setTitle("我的中心");

		mGridView = (GridView) this.findViewById(R.id.grid);
		mGridViewEntryAdapter = new GridViewEntryAdapter(this);
		mGridView.setAdapter(mGridViewEntryAdapter);
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
