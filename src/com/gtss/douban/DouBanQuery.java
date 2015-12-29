package com.gtss.douban;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;

import java.text.SimpleDateFormat;
import java.util.Date;

import kg.gtss.personalbooksassitant.FeedbackResult;
import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Common;
import kg.gtss.utils.DataStream;
import kg.gtss.utils.FileUtils;
import kg.gtss.utils.JsonUtils;
import kg.gtss.utils.Log;
import kg.gtss.utils.StringUtil;
import kg.gtss.utils.TimeUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.gtss.douban.DouBanBooks.Images;
import com.gtss.douban.DouBanBooks.Rating;
import com.gtss.douban.DouBanBooks.Tags;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * A task to query from Database preferred,then DouBan.For pictures,local cache
 * preferred,then website
 * */
public class DouBanQuery extends AsyncTask<String, Integer, Void> {
	// private static String this = "DouBanQuery";

	/**
	 * allowed to query from Database preferred,then DouBan
	 * */
	final static private boolean mPreferredDatabase = true;
	DouBanDatabase mDouBanDatabase;

	private String mUrl = "http://api.douban.com/v2/book/isbn/";// 9787532750337
	private HttpClient mHttpClient = new DefaultHttpClient();
	private DouBanBooks mHandledBooksInfo;
	private FeedbackResult mFeedbackResult;
	private Uri mImgUri = null;

	/**
	 * searching result
	 * */
	private boolean mExecRet = false;
	private static Context mContext;

	/**
	 * reference transpation
	 * */
	public DouBanQuery(Context context, FeedbackResult feed,
			DouBanBooks bookInfo) {
		mHandledBooksInfo = bookInfo;
		mFeedbackResult = feed;
		this.mContext = context;

	}

	void fetchImg() throws ClientProtocolException, IOException {
		// start to fetch samll book cover
		HttpResponse rsp = mHttpClient.execute(new HttpGet(
				mHandledBooksInfo.images.large ));
		if (rsp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			Log.v(this, "fetch small book cover error "
					+ rsp.getStatusLine().getStatusCode());
		}
		// fetch small image.stored into sdcard by isbn to keep unique
		// and search out easier
		String[] tmp = mHandledBooksInfo.images.large.split("/");
		String fileName = tmp[tmp.length - 1];
		File book_cover_img = FileUtils.write2SDFromInput(
				Common.SdCardDouBanCacheDirName, mHandledBooksInfo.isbn13, rsp
						.getEntity().getContent());

		mImgUri = Uri.fromFile(book_cover_img);
		mHandledBooksInfo.imguri = mImgUri.toString();

		// start to fetch large book cover
		HttpResponse rspL = mHttpClient.execute(new HttpGet(
				mHandledBooksInfo.images.large));
		if (rspL.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			Log.v(this, "fetch large book cover error "
					+ rspL.getStatusLine().getStatusCode());
		}
		// fetch small image.stored into sdcard by isbn to keep unique
		// and search out easier
		String[] tmpL = mHandledBooksInfo.images.large.split("/");
		String fileNameL = tmpL[tmpL.length - 1];
		File book_cover_imgL = FileUtils.write2SDFromInput(
				Common.SdCardDouBanCacheDirName, mHandledBooksInfo.isbn13
						+ ".l", rspL.getEntity().getContent());

	}

	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		Cursor c = null;
		if (mPreferredDatabase) {
			if (null == mDouBanDatabase) {

				mDouBanDatabase = new DouBanDatabase(mContext,
						new DatabaseChangedCallback() {

							@Override
							public void DatabaseChangedCallbackUi() {
								// TODO Auto-generated method stub

							}
						});
			}
			if (null != params[0]) {
				c = mDouBanDatabase.queryDatabaseByIsbn(params[0]);
			}

			if (null != c && c.getCount() > 0) {
				c.moveToNext();
				if (StringUtil
						.stringEquals(
								params[0],
								c.getString(c
										.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_ISBN)))) {
					Log.v(this, "start searched in db ,ja");
					mHandledBooksInfo.isbn13 = params[0];
					mHandledBooksInfo.title = c
							.getString(c
									.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_TITLE));
					mHandledBooksInfo.author
							.add(c.getString(c
									.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_AUTHOR)));

					mHandledBooksInfo.date = c
							.getString(c
									.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_DATE));
					mImgUri = FileUtils.getPicturesUri(
							Common.SdCardDouBanCacheDirName, params[0]);
					mHandledBooksInfo.imguri = (null == mImgUri) ? null
							: mImgUri.toString();

					mHandledBooksInfo.summary = c
							.getString(c
									.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_SUMMARY));

					// here if we find that database has record,but cache has no
					// imgs,we continue to go to douban
					if (null != mImgUri) {
						mExecRet = true;
						return null;
					}
				}// if matched
				c.close();
			}// cursor size >0

		}// mPreferredDatabase
		android.util.Log.v("aa",
				"@@@@@@@@@@@@@@@@@@@@@@@@@ to search from  douban,ha ");
		String result = null, s;
		HttpResponse resp = null;
		HttpGet get;
		// conjunct url with params
		if (null != params[0])
			mUrl += params[0];
		try {
			if (true) {
				result = JsonUtils.getHttpCotent2(mUrl);// getHttpCotent2(mUrl);
				Log.v(this, "server says:" + result);
				// json parse
				mExecRet = parseDouBanJson(mHandledBooksInfo, result);
				fetchImg();

			} else/*
				 * This will cause a 500 server internal error.shit :(
				 */{
				get = new HttpGet(mUrl);
				resp = mHttpClient.execute(get);

				if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					Log.v(this, "server return "
							+ resp.getStatusLine().getStatusCode());
				}
				result = DataStream.inputStream2String(resp.getEntity()
						.getContent());
			}

		} catch (UnknownHostException e) {
			Toast.makeText(mContext,
					mContext.getString(R.string.unconnected_network),
					Toast.LENGTH_SHORT).show();
			Log.v(this, "unconnected network.");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		if (mExecRet) {
			Log.v(this, "onPostExecute .search book info sucess.");
			mFeedbackResult.feedbackResult(Common.SEARCH_BOOK_SUCESS, mImgUri);
		} else {
			Log.v(this, "onPostExecute .search book info fail..");
			mFeedbackResult.feedbackResult(Common.SEARCH_BOOK_FAIL_NO_NETWORK,
					mImgUri);
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	/**
	 * return string form,reserved for json or xml parsing.
	 * http://maosidiaoxian.iteye.com/blog/1767763
	 * http://www.douban.com/group/topic/35873259/
	 * */
	/*
	 * public String getHttpCotent2(String url) { StringBuffer sbuff = new
	 * StringBuffer(); try { HttpURLConnection conn = (HttpURLConnection) new
	 * URL(url) .openConnection(); conn.setConnectTimeout(5000); // request
	 * using GET method conn.setRequestMethod("GET"); try { Log.v(this, "acess "
	 * + url + " return " + conn.getResponseCode()); } catch
	 * (UnknownHostException e) { Toast.makeText(mContext,
	 * mContext.getString(R.string.unconnected_network),
	 * Toast.LENGTH_SHORT).show(); Log.v(this, "unconnected network."); } if
	 * (conn != null && conn.getResponseCode() != HttpStatus.SC_OK) { return
	 * null; } // �õ���ȡ������(��) InputStreamReader in = new
	 * InputStreamReader(conn.getInputStream(), "UTF-8"); //
	 * Ϊ�������BufferedReader BufferedReader buffer = new BufferedReader(in);
	 * String inputLine = null; // ʹ��ѭ������ȡ��õ���� while (((inputLine =
	 * buffer.readLine()) != null)) { sbuff.append(inputLine); } //
	 * �ر�InputStreamReader in.close(); // �ر�http���� conn.disconnect(); }
	 * catch (Exception e) { e.printStackTrace(); } return sbuff.toString(); }
	 */

	/**
	 * parse json of DouBan;if i can cp ,like = ,it will be very convinient.It's
	 * really a boring process,did I do error??? params1:to store result;
	 * params2:the mixed data string;
	 * */
	private boolean parseDouBanJson(DouBanBooks info, String result) {
		if (null == result)
			return false;
		JSONTokener parser = new JSONTokener(result);
		try {
			// get json object
			JSONObject obj = (JSONObject) parser.nextValue();

			// get rating
			JSONObject rating = obj.getJSONObject(DouBanBooks.TYPE_rating);

			info.rating.max = rating.getDouble(Rating.TYPE_rating_max);
			info.rating.average = rating.getDouble(Rating.TYPE_rating_average);
			info.rating.min = rating.getDouble(Rating.TYPE_rating_min);
			info.rating.numRaters = rating.getInt(Rating.TYPE_rating_numRaters);
			Log.v(this, "info.rating.max:" + info.rating.max);

			// get subtitle
			info.subtitle = obj.getString(DouBanBooks.TYPE_subtitle);
			Log.v(this, "parse info.subtitle:" + info.subtitle);

			// get author info
			// JSONArray arr = obj.getJSONArray(DouBanBooks.TYPE_author);
			// for (int i = 0; i < arr.length(); i++) {
			// info.author.name[i] = (String) arr.get(i);
			// }
			JSONArray ar = obj.getJSONArray(DouBanBooks.TYPE_author);

			for (int i = 0; i < ar.length(); i++) {
				// first clear previous cache!!!!
				info.author.clear();
				info.author.add(ar.getString(i));
				Log.v(this, "author:" + info.author.get(i));
			}

			// get pubdate
			info.pubdate = obj.getString(DouBanBooks.TYPE_pubdate);

			// get tags
			JSONArray tags = obj.getJSONArray(DouBanBooks.TYPE_tags);
			Log.v(this, "size:" + tags.length() + ",,,,tags:" + tags);

			for (int i = 0; i < tags.length(); i++) {
				JSONObject childtags = tags.optJSONObject(i);

				int count = childtags.getInt(Tags.TYPE_tags_count);
				String name = childtags.getString(Tags.TYPE_tags_name);
				String title = childtags.getString(Tags.TYPE_tags_title);
				// first clear previous cache!!!!
				info.tags.clear();
				info.tags.add(new Tags(count, name, title));
				Log.v(this, "tag name--->" + name);// /////
			}

			// get origin_title
			info.origin_title = obj.getString(DouBanBooks.TYPE_origin_title);

			// get image
			info.image = obj.getString(DouBanBooks.TYPE_image);

			// get binding
			info.binding = obj.getString(DouBanBooks.TYPE_binding);

			// get translator
			JSONArray trans = obj.getJSONArray(DouBanBooks.TYPE_translater);

			for (int i = 0; i < trans.length(); i++) {
				// first clear previous cache!!!!
				info.translator.clear();
				info.translator.add(trans.getString(i));
			}

			// get catalog
			// JSONArray cata = obj.getJSONArray(DouBanBooks.TYPE_catalog);
			// for (int i = 0; i < cata.length(); i++) {
			// info.catalog[i] = (String) cata.get(i);
			// }
			info.catalog = obj.getString(DouBanBooks.TYPE_catalog);

			// get pages
			info.pages = obj.getInt(DouBanBooks.TYPE_pages);

			// get images
			JSONObject images = obj.getJSONObject(DouBanBooks.TYPE_images);

			info.images.small = images.getString(Images.TYPE_images_small);
			info.images.medium = images.getString(Images.TYPE_images_medium);
			info.images.large = images.getString(Images.TYPE_images_large);

			// get alt
			info.alt = obj.getString(DouBanBooks.TYPE_alt);

			// get id
			info.id = obj.getLong(DouBanBooks.TYPE_id);

			// get publisher
			info.publisher = obj.getString(DouBanBooks.TYPE_publisher);

			// get isbn10
			info.isbn10 = obj.getString(DouBanBooks.TYPE_isbn10);

			// get isbn13
			info.isbn13 = obj.getString(DouBanBooks.TYPE_isbn13);

			// get title
			info.title = obj.getString(DouBanBooks.TYPE_title);

			// get url
			info.url = obj.getString(DouBanBooks.TYPE_url);

			// get alt_title
			info.alt_title = obj.getString(DouBanBooks.TYPE_alt_title);

			// get author_info
			info.author_intro = obj.getString(DouBanBooks.TYPE_author_intro);

			// get summary
			info.summary = obj.getString(DouBanBooks.TYPE_summary);

			// get price
			info.price = obj.getString(DouBanBooks.TYPE_price);

			// get current time
			info.date = TimeUtils.getCurrentTime();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
