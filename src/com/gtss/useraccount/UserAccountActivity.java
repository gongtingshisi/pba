package com.gtss.useraccount;

import java.util.ArrayList;
import java.util.List;

import com.gtss.login.DouBanAuthorization;
import com.gtss.login.DouBanLogin;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.JsonUtils;
import kg.gtss.utils.Log;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.os.Bundle;
import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * To support multi-users account http://www.csdn.net/article/2010-12-24/286271
 * 
 * http://www.eoeandroid.com/thread-45894-1-1.html
 * */
public class UserAccountActivity extends Activity {

	AccountManager mAccountManager;
	MyArrayAdapter mMyArrayAdapter;
	Account[] mAccount;
	ArrayList<Account> mAccountList = new ArrayList<Account>();
	AuthenticatorDescription[] mAuthenticatorDescription;
	Context mContext;

	ImageView LogInWithDoubanButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.login);
		LogInWithDoubanButton = (ImageView) this
				.findViewById(R.id.login_with_douban);
		LogInWithDoubanButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(UserAccountActivity.this, "login with douban.");
				Intent i = new Intent();
				i.setClass(UserAccountActivity.this, DouBanAuthorization.class);
				startActivity(i);
			}
		});

		mContext = this;
		// mAccountManager = (mAccountManager) this
		// .getSystemService(Context.ACCOUNT_SERVICE);
		mAccountManager = mAccountManager.get(this);

		log(LocalAccountInfo.ACCOUNT_TYPE);
		mAccount = mAccountManager.getAccounts();

		mAuthenticatorDescription = mAccountManager.getAuthenticatorTypes();
		for (int i = 0; i < mAuthenticatorDescription.length; i++) {
			if (mAuthenticatorDescription[i].packageName
					.equals(LocalAccountInfo.ACCOUNT_TYPE)) {

			}
		}

		// mAuthenticatorDescription = mAccountManager.getAuthenticatorTypes();
		for (int i = 0; i < mAccount.length; i++) {
			// String pkgName = mAuthenticatorDescription[i].packageName;
			//
			if (mAccount[i].type.equals(LocalAccountInfo.ACCOUNT_TYPE)) {
				mAccountList.add(mAccount[i]);

				log("name:" + mAccount[i].name + "	,type:" + mAccount[i].type
						+ "	,content:" + mAccount[i].describeContents() + "	,"
						+ mAccount[i].toString());
			}
		}
		mMyArrayAdapter = new MyArrayAdapter(this,
				R.layout.single_account_layout);

		// this.getListView().setAdapter(mMyArrayAdapter);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	void log(String msg) {
		Log.v(this, msg);

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		i.setClass(this, LocalAccountAuthenticatorActivity.class);
		this.startActivity(i);
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		menu.add("1");

		return super.onPrepareOptionsMenu(menu);
	}

	class MyArrayAdapter extends ArrayAdapter {
		int layoutRes;

		public MyArrayAdapter(Context context, int resource) {
			super(context, resource);
			// TODO Auto-generated constructor stub
			layoutRes = resource;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mAccountList.size();
		}

		@Override
		public Account getItem(int position) {
			// TODO Auto-generated method stub
			return mAccountList.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(layoutRes,
						null);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ImageView icon = (ImageView) convertView
					.findViewById(R.id.account_icon);
			TextView name = (TextView) convertView
					.findViewById(R.id.account_name);
			TextView type = (TextView) convertView
					.findViewById(R.id.account_type);

			holder.icon = R.drawable.ic_launcher;
			holder.name = mAccountList.get(position).name;
			holder.type = mAccountList.get(position).type;

			log("name:" + mAccountList.get(position).name + "	,type:"
					+ mAccountList.get(position).type + "	,content:"
					+ mAccountList.get(position).describeContents());

			icon.setImageResource(holder.icon);
			name.setText(holder.name);
			type.setText(holder.type);

			return convertView;
		}
	}

	class ViewHolder {
		public int icon;
		public String name;
		public String type;
	}

}
