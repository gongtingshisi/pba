package kg.gtss.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A class to handle data stream.
 * */
public class DataStream {
	/**
	 * 1. String --> InputStream
	 * @param str
	 * @return
	 */
	public static InputStream String2InputStream(String str){
	    ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
	    return stream;
	}
	 
	/***
	 * 2. InputStream --> String
	 * @param is
	 * @return
	 */
	public static String inputStream2String(InputStream is){
	    BufferedReader in = new BufferedReader(new InputStreamReader(is));
	    StringBuffer buffer = new StringBuffer();
	    String line = "";
	    try {
			while ((line = in.readLine()) != null){
			  buffer.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return buffer.toString();
	}
}
