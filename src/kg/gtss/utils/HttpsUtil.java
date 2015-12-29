package kg.gtss.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpStatus;

import android.os.AsyncTask;

/**
 * 鲍杭 本类是一个https请求的工具类，用与负责建立https连接，以及数据的传输
 * */
public class HttpsUtil {
	static String TAG = "HttpsUtil";
	static TrustManager[] xtmArray = new MytmArray[] { new MytmArray() };// 创建信任规则列表
	private final static int CONNENT_TIMEOUT = 15000;
	private final static int READ_TIMEOUT = 15000;
	private static String mCookie;
	public static int httpsResponseCode;

	static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * 信任所有主机-对于任何证书都不做检查 Create a trust manager that does not validate
	 * certificate chains， Android 采用X509的证书信息机制，Install the all-trusting trust
	 * manager
	 */
	private static void trustAllHosts() {
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, xtmArray, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
			// 不进行主机名确认,对所有主机
			HttpsURLConnection.setDefaultHostnameVerifier(DO_NOT_VERIFY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * https get方法，返回值是https请求，服务端返回的数据string类型，数据进行xml解析
	// * */
	// public static String HttpsGet(String httpsurl) {
	// return HttpsPost(httpsurl, null);
	//
	// }
	/**
	 * https post方法，返回值是https请求，服务端返回的数据string类型，数据进行xml解析
	 * */
	public static String HttpsPost(String httpsurl, String data) {
		String result = null;
		HttpURLConnection http = null;
		URL url;
		try {
			url = new URL(httpsurl);
			// 判断是http请求还是https请求
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				http = (HttpsURLConnection) url.openConnection();
				((HttpsURLConnection) http).setHostnameVerifier(DO_NOT_VERIFY);// 不进行主机名确认
			} else {
				http = (HttpURLConnection) url.openConnection();
			}

			http.setConnectTimeout(CONNENT_TIMEOUT);// 设置超时时间
			http.setReadTimeout(READ_TIMEOUT);
			if (data == null) {
				Log.v(TAG, "get");
				http.setRequestMethod("GET");// 设置请求类型
				http.setDoInput(true);
				// http.setRequestProperty("Content-Type", "text/xml");
				if (mCookie != null)
					http.setRequestProperty("Cookie", mCookie);
			} else {
				Log.v(TAG, "post");
				http.setRequestMethod("POST");// 设置请求类型为post
				http.setDoInput(true);
				http.setDoOutput(true);
				// http.setRequestProperty("Content-Type", "text/xml");
				if (mCookie != null && mCookie.trim().length() > 0)
					http.setRequestProperty("Cookie", mCookie);

				DataOutputStream out = new DataOutputStream(
						http.getOutputStream());
				out.writeBytes(data);
				out.flush();
				out.close();
			}

			// 设置http返回状态200（ok）还是403
			httpsResponseCode = http.getResponseCode();
			Log.v(TAG, "httpsResponseCode " + httpsResponseCode);
			BufferedReader in = null;
			if (httpsResponseCode == 200) {
				getCookie(http);
				in = new BufferedReader(new InputStreamReader(
						http.getInputStream()));
			} else
				in = new BufferedReader(new InputStreamReader(
						http.getErrorStream()));
			String temp = in.readLine();
			while (temp != null) {
				if (result != null)
					result += temp;
				else
					result = temp;
				temp = in.readLine();
			}
			in.close();
			http.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	class PostTask extends AsyncTask<Void, String, String> {
		String uri;

		PostTask(String uri) {
			this.uri = uri;
		}

		@Override
		protected String doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return postHttpCotent(uri);

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

	public static String postHttpCotent(String url) {
		StringBuffer sbuff = new StringBuffer();
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			if (null == conn)
				Log.v(TAG, "NULLLLLLLLL");
			else 
				Log.v(TAG,"!!!!!!!!!!");
			conn.setConnectTimeout(5000);
			// request using POST method

			conn.setRequestMethod("POST");
			Log.v(TAG, "acess " + url + " return " + conn.getResponseCode());

			//
			if (null == conn || conn.getResponseCode() != HttpStatus.SC_OK) {
				return null;
			}
			// �õ���ȡ������(��)
			InputStreamReader in = new InputStreamReader(conn.getInputStream(),
					"UTF-8");
			// Ϊ�������BufferedReader
			BufferedReader buffer = new BufferedReader(in);
			String inputLine = null;
			// ʹ��ѭ������ȡ��õ����
			while (((inputLine = buffer.readLine()) != null)) {
				sbuff.append(inputLine);
			}
			// �ر�InputStreamReader
			in.close();
			// �ر�http����
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbuff.toString();
	}

	/**
	 * 得到cookie
	 * 
	 */
	private static void getCookie(HttpURLConnection http) {
		String cookieVal = null;
		String key = null;
		mCookie = "";
		for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++) {
			if (key.equalsIgnoreCase("set-cookie")) {
				cookieVal = http.getHeaderField(i);
				cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
				mCookie = mCookie + cookieVal + ";";
			}
		}
	}
}