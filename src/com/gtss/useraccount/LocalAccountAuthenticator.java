package com.gtss.useraccount;

import kg.gtss.utils.Log;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * http://www.csdn.net/article/2010-12-24/286271
 * */
public class LocalAccountAuthenticator extends AbstractAccountAuthenticator {
	Context mContext;

	public LocalAccountAuthenticator(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String

	accountType, String authTokenType, String[] requiredFeatures, Bundle

	options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		log(accountType + " - " + authTokenType);

		Bundle ret = new Bundle();

		Intent intent = new Intent(mContext,
				LocalAccountAuthenticatorActivity.class);

		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
				response);

		ret.putParcelable(AccountManager.KEY_INTENT, intent);

		return ret;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, Bundle arg2) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse arg0, Account arg1,
			String arg2, Bundle arg3) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthTokenLabel(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse arg0, Account arg1,
			String[] arg2) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse arg0,
			Account arg1, String arg2, Bundle arg3)
			throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	void log(String msg) {
		Log.v(this, msg);
	}
}
