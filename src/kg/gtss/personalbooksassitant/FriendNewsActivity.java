package kg.gtss.personalbooksassitant;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * friend news
 * */
public class FriendNewsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setTitle("我的圈子");
		TextView v = new TextView(this);
		v.setText("news");
		this.setContentView(v);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
