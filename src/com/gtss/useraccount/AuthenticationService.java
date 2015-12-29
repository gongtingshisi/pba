package com.gtss.useraccount;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * http://www.csdn.net/article/2010-12-24/286271
 * */
public class AuthenticationService extends Service {
	LocalAccountAuthenticator mLocalAccountAuthenticator;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		IBinder ret = null;

		if (intent.getAction().equals(android.accounts.AccountManager.

		ACTION_AUTHENTICATOR_INTENT))

			ret = getLocalAccountAuthenticator().getIBinder();

		return ret;

	}

	private LocalAccountAuthenticator getLocalAccountAuthenticator()

	{

		if (mLocalAccountAuthenticator == null)

			mLocalAccountAuthenticator = new LocalAccountAuthenticator(this);

		return mLocalAccountAuthenticator;

	}
}
