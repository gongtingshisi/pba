package kg.gtss.personalbooksassitant;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InternetSearchFragment extends Fragment {
	private String TAG = "PbaMain";// "InternetSearchFragment";

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.v(TAG, "attach InternetSearchFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v2 = inflater.inflate(R.layout.internet, null);
		return v2;
	}

}
