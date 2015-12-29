package kg.gtss.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import kg.gtss.personalbooksassitant.R;

import org.apache.http.HttpStatus;

import android.widget.Toast;

/**
 * usefull for json parse.such as parsxing json from website
 * */
public class JsonUtils {
	static String TAG = "JsonUtils";

	public static String getHttpCotent2(String url) {
		StringBuffer sbuff = new StringBuffer();
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			conn.setConnectTimeout(5000);
			// request using GET method
			conn.setRequestMethod("GET");
			try {
				Log.v(TAG, "acess " + url + " return " + conn.getResponseCode());
			} catch (UnknownHostException e) {

				Log.v(TAG, "unconnected network.");
			}
			if (conn != null && conn.getResponseCode() != HttpStatus.SC_OK) {
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
}
