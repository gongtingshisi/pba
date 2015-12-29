package com.gtss.useraccount;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * http://www.csdn.net/article/2010-12-24/286271
 * */
public class LocalAccountAuthenticatorActivity extends
		AccountAuthenticatorActivity {
	EditText mName, mPwd;
	Button mDone;
	AccountManager mAM;

	Context mContext;

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

	void log(String msg) {
		Log.v(this, msg);
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
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		this.setTitle("登录");
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		mContext = this;
		mAM = AccountManager.get(this);
		this.setContentView(R.layout.add_new_account);
		mName = (EditText) this.findViewById(R.id.add_account_name);
		mPwd = (EditText) this.findViewById(R.id.add_account_pwd);
		mDone = (Button) this.findViewById(R.id.add_account_done);
		mDone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (null == mName.getText().toString()
						|| mName.getText().toString().length() <= 0
						|| null == mPwd.getText().toString()
						|| mPwd.getText().toString().length() <= 0) {
					return;
				}

				Bundle data = new Bundle();
				Account ac = new Account(mName.getText().toString(),
						LocalAccountInfo.ACCOUNT_TYPE);
				// data.putString(LocalAccountInfo.SERVER,
				// LocalAccountInfo.SERVER_SITE);
				if (mAM.addAccountExplicitly(ac, mPwd.getText().toString(),
						data)) {
					setAccountAuthenticatorResult(data);
				} else {
					log("add account " + mName.getText().toString() + " fail");
				}

				finish();
			}
		});
	}
}
