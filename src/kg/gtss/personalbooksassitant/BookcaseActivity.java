package kg.gtss.personalbooksassitant;

import kg.gtss.utils.Log;
import android.os.Bundle;

public class BookcaseActivity extends OnlineResourcesListActivity {
	void loadOnLineResource() {
		Log.v(this, "loadOnLineResource");
		mOnLineResources.add(new OnlineResourcesListActivity.OnLineResource(
				"http://www.nlc.gov.cn/dsb_zyyfw/ts/tszyk/",
				R.drawable.national_library, "中国国家图书馆"));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mListView.setAdapter(mOnLineResourcesAdapter);
	}

}
