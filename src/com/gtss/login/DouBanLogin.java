package com.gtss.login;

import kg.gtss.utils.JsonUtils;
import kg.gtss.utils.Log;
import android.os.AsyncTask;

/**
 * sync task to login douban
 * */
public class DouBanLogin extends AsyncTask<Void, Void, String> {

	String TAG = "DouBanLogin";
	String URL = "https://www.douban.com/service/auth2/auth?client_id=01d488a704c29d13057d99f50e47d739&redirect_uri=https://www.example.com/back&response_type=code";

	@Override
	protected String doInBackground(Void... arg0) {
		// TODO Auto-generated method stub
		return JsonUtils.getHttpCotent2(URL);
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		Log.v(this, "login returns from douban:" + result);
	}

}
