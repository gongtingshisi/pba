package kg.gtss.search;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.search_layout);

		Intent intent = this.getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.v(this, "u wanna search :" + query);
		}
	}

	@Override
	public boolean onSearchRequested() {
		// TODO Auto-generated method stub
		// globalSearch will launch web browser search
		Log.v(this, "onSearchRequested");
		this.startSearch(null, false, null, false);
		return true; // !
	}
}
