package kg.gtss.personalbooksassitant;

import android.net.Uri;

/**
 * I reserved an interface api to different engineer to callback when they
 * covers their duty,eg DouBan or Google when they fetch some query result
 * */
public interface FeedbackResult {
	/**
	 * notify to handle some feedback,such as refreshing display etc.
	 * paras:wether we sucess.and,the uri of this book,null if none. return
	 * */

	void feedbackResult(int ret, Uri uri);
}
