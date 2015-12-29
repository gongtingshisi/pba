package com.gtss.login;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.DataStream;
import kg.gtss.utils.HttpsUtil;
import kg.gtss.utils.Log;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * http://developers.douban.com/wiki/?title=oauth2#server_side_flow;
 * 
 * 1.获取 authorization_code https://www.douban.com/service/auth2/auth?
 * client_id=01d488a704c29d13057d99f50e47d739&
 * redirect_uri=https://www.example.com/back& response_type=code;
 * 
 * 2.获取 access_token https://www.douban.com/service/auth2/token?
 * client_id=01d488a704c29d13057d99f50e47d739& client_secret=47bafd624c6acbe0&
 * redirect_uri=https://www.example.com/back& grant_type=authorization_code&
 * code=***
 * */
public class DouBanAuthorization extends Activity {
	String TAG = "DouBanAuthorization";
	WebView mWebView;
	String APIKey = "01d488a704c29d13057d99f50e47d739";// client_id
	String Secret = "47bafd624c6acbe0";// client_secret
	final private static String CallBack = "https://www.example.com/back";// callback registered
	// for authrization
	String AuthorizationCodeURL = "https://www.douban.com/service/auth2/auth?client_id="
			+ APIKey + "&redirect_uri=" + CallBack + "&response_type=code";
	// for login
	String AccessTokenURL = "https://www.douban.com/service/auth2/token?client_id="
			+ APIKey
			+ "&client_secret="
			+ Secret
			+ "&redirect_uri="
			+ CallBack
			+ "&grant_type=authorization_code&code=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.authorization_code);
		mWebView = (WebView) this.findViewById(R.id.authorization_webpage);
		// new DouBanLogin().execute();
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setFocusable(true);
		mWebView.loadUrl(AuthorizationCodeURL);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.i(TAG, "onPageFinished: " + url + "网页加载完毕");
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.i(TAG, "shouldOverrideUrlLoading: " + url);
				mWebView.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				Log.v(TAG, "onPageStarted: " + url + "开始加载界面。。。。。");
				if (url.startsWith(CallBack)) {
					// 取消授权后的界面
					view.cancelLongPress();
					view.stopLoading();

					// 获取Code
					Uri uri = Uri.parse(url);
					String code = uri.getQueryParameter("code");

					String result = "";
					if (code != null) {

						result = HttpsUtil
								.postHttpCotent(AccessTokenURL + code);

						// HttpsUtil.HttpsPost(AccessTokenURL + code, "");
						Log.v(TAG, "Https地址: " + code);
						Log.v(TAG, "登录请求结果: " + result);
					}
					/*
					 * if (result.startsWith("{\"access_token\":")) { int i =
					 * result.indexOf(":"); int j = result.indexOf(",");
					 * WeiboConstant.ACCESS_TOKEN = result.substring(i + 2, j -
					 * 1); Log.e("ACCESS_TOKEN的值", WeiboConstant.ACCESS_TOKEN);
					 * finish(); Toast.makeText(SinaOAuthActivity.this, "登录成功",
					 * Toast.LENGTH_LONG).show(); }
					 */
				}
				super.onPageStarted(view, url, favicon);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
