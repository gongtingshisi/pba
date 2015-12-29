package kg.gtss.utils;

public class Log {
	static String TAG = Common.PKGNAME;
	public static boolean DEBUG = true;

	public static void i(Object o, String s) {
		if (DEBUG)
			android.util.Log.v(TAG, o.getClass().getSimpleName() + "    " + s);
	}

	public static void d(Object o, String s) {
		if (DEBUG)
			android.util.Log.d(TAG, o.getClass().getSimpleName() + "    " + s);
	}

	public static void v(Object o, String s) {
		if (DEBUG)
			android.util.Log.v(TAG, o.getClass().getSimpleName() + "    " + s);
	}
}
