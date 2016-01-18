package kg.gtss.utils;

public class StringUtil {
	/**
	 * wether two strings is equal
	 * */
	public final static boolean stringEquals(String s1, String s2) {
		if (null == s1 || null == s2)
			return false;
		if (s1.length() == 0 || s2.length() == 0)
			return false;
		if (s1.equals(s2))
			return true;
		else
			return false;
	}

	public final static boolean isEmpty(String comment) {
		return null == comment || 0 == comment.length()
				|| null == comment.trim() || 0 == comment.trim().length();
	}

}
