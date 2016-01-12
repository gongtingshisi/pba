package kg.gtss.utils;

/**
 * some common info among all classes
 * */
public class Common {
	public final static String PKGNAME = "pba";

	public static String SdCardCacheDirName = "pba/";
	public static String SdCardDouBanCacheDirName = "douban/";
	public static String SdCardGoogleCacheDirName = "google/";
	public static String SdCardAmazonCacheDirName = "amazon/";

	public final static int LOADER_ID_Start = 1000;
	public final static int LOADER_ID_PbaMainActivity = LOADER_ID_Start + 1;
	public final static int LOADER_ID_SearchResultListActivity = LOADER_ID_Start + 2;
	public final static int LOADER_ID_ReadingProgressActivity = LOADER_ID_Start + 3;
	public final static int LOADER_ID_FavoriteBooksActivity = LOADER_ID_Start + 4;
	public final static int LOADER_ID_AllBooksActivity = LOADER_ID_Start + 5;
	public final static int LOADER_ID_LocalManagerFragment = LOADER_ID_Start + 6;
	public final static int LOADER_ID_AddReadingAlarm = LOADER_ID_Start + 7;

	public final static int SEARCH_BOOK_SUCESS = 0;
	public final static int SEARCH_BOOK_FAIL_NO_NETWORK = 1;
	public final static int SEARCH_BOOK_FAIL_UNKNOWN = 2;
}
