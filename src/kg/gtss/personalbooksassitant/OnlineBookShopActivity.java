package kg.gtss.personalbooksassitant;

import kg.gtss.utils.Log;
import android.app.Activity;
import android.os.Bundle;

public class OnlineBookShopActivity extends OnlineResourcesListActivity {
	void loadOnLineResource() {
		Log.v(this, "loadOnLineResource");
		mOnLineResources.add(new OnlineResourcesListActivity.OnLineResource(
				"http://www.dangdang.com/", R.drawable.dangdang, "当当网"));

		mOnLineResources.add(new OnlineResourcesListActivity.OnLineResource(
				"http://www.amazon.cn/", R.drawable.amazon, "亚马逊网"));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mListView.setAdapter(mOnLineResourcesAdapter);
	}
}
