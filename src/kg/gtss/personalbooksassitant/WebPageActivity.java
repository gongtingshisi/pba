package kg.gtss.personalbooksassitant;

import kg.gtss.utils.Log;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebPageActivity extends Activity {
	ProgressWebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*
		 * this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 * this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		mWebView = new ProgressWebView(this, null);
		this.setContentView(mWebView);
		mWebView.loadUrl(this.getIntent().getStringExtra("url"));

		// 启用支持javascript
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);

		// 优先使用缓存
		mWebView.getSettings()
				.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				Log.v(this, "WebPageActivity WebViewClient load " + url);
				view.loadUrl(url);
				return true;
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (mWebView.canGoBack()) {
				mWebView.goBack();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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

	/**
	 * 带进度条的WebView
	 * 
	 * @author 农民伯伯
	 * @see http://www.cnblogs.com/over140/archive/2013/03/07/2947721.html
	 * 
	 */
	@SuppressWarnings("deprecation")
	public class ProgressWebView extends WebView {

		private ProgressBar progressbar;

		public ProgressWebView(Context context, AttributeSet attrs) {
			super(context, attrs);
			progressbar = new ProgressBar(context, null,
					android.R.attr.progressBarStyleHorizontal);
			progressbar.setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, 5, 0, 0));
			addView(progressbar);
			// setWebViewClient(new WebViewClient(){});
			setWebChromeClient(new WebChromeClient());
		}

		public class WebChromeClient extends android.webkit.WebChromeClient {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressbar.setVisibility(GONE);
				} else {
					if (progressbar.getVisibility() == GONE)
						progressbar.setVisibility(VISIBLE);
					progressbar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}

		}

		@Override
		protected void onScrollChanged(int l, int t, int oldl, int oldt) {
			LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
			lp.x = l;
			lp.y = t;
			progressbar.setLayoutParams(lp);
			super.onScrollChanged(l, t, oldl, oldt);
		}
	}
}
