package kg.gtss.personalbooksassitant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kg.gtss.search.SearchResultCursorAdapter;
import kg.gtss.search.SearchResultListActivity;

import kg.gtss.utils.FileUtils;
import kg.gtss.utils.Log;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.SearchManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;

import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.gtss.bdmap.Bookshop;
import com.gtss.douban.DatabaseBookInterface;
import com.gtss.douban.DatabaseChangedCallback;

import com.gtss.douban.DouBanDatabase;

import com.gtss.useraccount.UserAccountActivity;
import com.laomo.zxing.CaptureActivity;
import com.progress.bookreading.ReadingProgressActivity;

//import android.util.Log;
/**
 * 这个包含了SearchResultListActivity的功能,可以考虑继承他,布局
 * */
public class PbaMain extends FragmentActivity implements OnTouchListener {
	Context mContext;

	final int MENU_SCAN = 1, MENU_SEARCH = 2, MENU_SHARE = 3,
			MENU_SETTINGS = 4, MENU_LOGIN = 5, MENU_ABOUT = 6;

	ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();

	SearchResultCursorAdapter mTimeLineCursorAdapter;

	String SELECT = null;
	String[] SELECT_ARGS = new String[] {};
	String ORDER = null;

	/**
	 * requestcode:to open qr
	 * */
	private final static int OpenQrScanRequestCode = 0;

	/** Called when the activity is first created. */
	private boolean areButtonsShowing;
	private RelativeLayout composerButtonsWrapper;

	private ImageView composerButtonsShowHideButtonIcon;

	private RelativeLayout composerButtonsShowHideButton;

	// //////////////////////////////
	// ////////////////

	BitmapDescriptor mCurrentMarker;

	// /////////////////
	// //////////////////////////////

	private DouBanDatabase mDouBanDatabase;

	// private SQLiteDatabase mDatabaseManager;

	private ShareActionProvider mShareActionProvider;

	// title on each fragment in pagerview from pageadapter
	private String[] mTabs;

	private MenuItem menuScanBookItem;

	// isbn means ean13
	final private static String ISBN_TYPE = "EAN_13";

	// current isbn scanned
	private static String ScannedISBN;

	MyFragmentPagerAdapter mMyFragmentPagerAdapter;

	// MyPagerAdapter mMyPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	/**
	 * current index of page
	 * */
	private int mCurrentpageIndex = 0;

	public static void deleteOneBook(Context con, final DouBanDatabase db,
			CursorAdapter ca, int position) {

		if (ca != null && ca.getCount() > 0) {

			Cursor c = ca.getCursor();
			c.moveToPosition(position);
			final String title = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_TITLE));
			final String author = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_AUTHOR));
			final String isbn = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_ISBN));

			AlertDialog.Builder builder = new AlertDialog.Builder(con);
			builder.setTitle("Delete it?");
			// 此处使用书名
			builder.setMessage("Are you sure to delete the book "
					+ (title + " " + author) + " ?");
			builder.setPositiveButton(R.string.ok_button,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 从数据库删除，并且刷新时间轴节目，以及其他节目信息，比如这本书可能刚刚是在其他界面查询过的
							db.deleteBookByISBN(isbn);
							Log.v(this, "delete ");

						}
					}).setNegativeButton(R.string.no_button, null);
			builder.show();
		}
	}

	void deleteOneBook(int position) {

		if (mTimeLineCursorAdapter != null
				&& mTimeLineCursorAdapter.getCount() > 0) {

			Cursor c = mTimeLineCursorAdapter.getCursor();
			c.moveToPosition(position);
			final String title = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_TITLE));
			final String author = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_AUTHOR));
			final String isbn = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_ISBN));

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Delete it?");
			// 此处使用书名
			builder.setMessage("Are you sure to delete the book "
					+ (title + " " + author) + " ?");
			builder.setPositiveButton(R.string.ok_button,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 从数据库删除，并且刷新时间轴节目，以及其他节目信息，比如这本书可能刚刚是在其他界面查询过的
							mDouBanDatabase.deleteBookByISBN(isbn);
							Log.v(this, "delete ");

						}
					}).setNegativeButton(R.string.no_button, null);
			builder.show();
		}
	}

	public void onClickView(View v, boolean isOnlyClose) {
		if (isOnlyClose) {
			if (areButtonsShowing) {
				MenuRightAnimations.startAnimationsOut(composerButtonsWrapper,
						300);
				composerButtonsShowHideButtonIcon
						.startAnimation(MenuRightAnimations.getRotateAnimation(
								-315, 0, 300));
				areButtonsShowing = !areButtonsShowing;
			}

		} else {

			if (!areButtonsShowing) {
				MenuRightAnimations.startAnimationsIn(composerButtonsWrapper,
						300);
				composerButtonsShowHideButtonIcon
						.startAnimation(MenuRightAnimations.getRotateAnimation(
								0, -315, 300));
			} else {
				MenuRightAnimations.startAnimationsOut(composerButtonsWrapper,
						300);
				composerButtonsShowHideButtonIcon
						.startAnimation(MenuRightAnimations.getRotateAnimation(
								-315, 0, 300));
			}
			areButtonsShowing = !areButtonsShowing;
		}

	}

	void launchZxing() {
		Intent i = new Intent();
		// i.setAction("com.google.zxing.client.android.SCAN");
		i.setClass(mContext, CaptureActivity.class);
		((Activity) PbaMain.this).startActivityForResult(i,
				OpenQrScanRequestCode);// startActivityForResult
	}

	void launchReadingProgress() {
		Intent i = new Intent();
		i.setClass(mContext, ReadingProgressActivity.class);
		startActivity(i);
	}

	void launchMainEntry() {
		Intent i = new Intent();
		i.setClass(mContext, TabViewEntryActivity.class);
		startActivity(i);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	void initFragment() {

		Fragment f = new BooksGatherFragment();
		mFragmentList.add(f);
		f = new InternetResourcesFragment();
		mFragmentList.add(f);
		f = new LocalManagerFragment();
		mFragmentList.add(f);
	}

	void initAnimation() {
		MenuRightAnimations.initOffset(this);
		composerButtonsWrapper = (RelativeLayout) findViewById(R.id.composer_buttons_wrapper);
		composerButtonsShowHideButton = (RelativeLayout) findViewById(R.id.composer_buttons_show_hide_button);
		composerButtonsShowHideButtonIcon = (ImageView) findViewById(R.id.composer_buttons_show_hide_button_icon);

		composerButtonsShowHideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickView(v, false);
			}
		});
		for (int i = 0; i < composerButtonsWrapper.getChildCount(); i++) {
			composerButtonsWrapper.getChildAt(i).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View arg0) {

							// scan books
							if (arg0.getId() == R.id.composer_button_photo) {
								launchZxing();
							} else if (arg0.getId() == R.id.composer_button_thought) {
								// reading progress
								launchReadingProgress();
							} else if (arg0.getId() == R.id.composer_button_people) {
								launchMainEntry();
							} else if (arg0.getId() == R.id.composer_button_place) {

							}
						}
					});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		/*
		 * this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */

		setContentView(R.layout.pba_main);

		initFragment();
		initAnimation();

		// create db
		mDouBanDatabase = new DouBanDatabase(PbaMain.this,
				new DatabaseChangedCallback() {

					@Override
					public void DatabaseChangedCallbackUi() {
						// TODO Auto-generated method stub
						Log.v(this, "DatabaseChangedCallbackUi");

					}
				});

		mTabs = PbaMain.this.getResources().getStringArray(R.array.tab);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

				mCurrentpageIndex = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				// log( "onPageScrolled " + arg0 + " " + arg1 + " " +
				// arg2);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				// log( "onPageScrollStateChanged " + arg0);
			}
		});
		mViewPager.setOnTouchListener(this);
		mViewPager.setAdapter(mMyFragmentPagerAdapter);

		try {
			FileUtils.copyBigDataTo(this.getAssets().open("ic_launcher.png"),
					getFilesDir().getAbsolutePath() + "/ic_launcher.png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Intent contructShareIntent() {
		log("pwd " + getFilesDir().getAbsolutePath());
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		Uri uri = Uri.fromFile(new File(getFilesDir(), "ic_launcher.jpg"));
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		return shareIntent;
	}

	// Somewhere in the application.
	private void doShare(Intent shareIntent) {
		// When you want to share set the share intent.
		mShareActionProvider.setShareIntent(shareIntent);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case MENU_SCAN:
			menuScanBookItem = item;
			launchZxing();
			return true;
		case MENU_SEARCH:
			onSearchRequested();
			return true;
		case MENU_SHARE:
			item.setActionProvider(new ShareActionProvider(this));
			mShareActionProvider = (ShareActionProvider) item
					.getActionProvider();
			doShare(contructShareIntent());
			mShareActionProvider
					.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {

						@Override
						public boolean onShareTargetSelected(
								ShareActionProvider arg0, Intent i) {
							// TODO Auto-generated method stub
							Log.v(this, "ShareActionProvider " + i.getAction()
									+ "   " + i.getExtras());
							return false;
						}
					});
			return true;
		case MENU_SETTINGS:
			launchSettings();
			return true;
		case MENU_LOGIN:
			Intent i = new Intent();
			i.setClass(mContext, UserAccountActivity.class);
			this.startActivity(i);
			return true;
		case MENU_ABOUT:
			showAboutDialog();
			return true;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, MENU_SCAN, MENU_SCAN,
				this.getString(R.string.scan_books)).setIcon(
				this.getResources().getDrawable(R.drawable.composer_button));
		menu.add(Menu.NONE, MENU_SEARCH, MENU_SEARCH,
				this.getString(R.string.search)).setIcon(
				R.drawable.composer_camera);
		menu.add(Menu.NONE, MENU_SHARE, MENU_SHARE,
				this.getString(R.string.share)).setIcon(
				R.drawable.composer_music);
		menu.add(Menu.NONE, MENU_SETTINGS, MENU_SETTINGS,
				getString(R.string.settings))
				.setIcon(R.drawable.composer_place);
		menu.add(Menu.NONE, MENU_LOGIN, MENU_LOGIN,
				this.getString(R.string.login)).setIcon(
				R.drawable.composer_sleep);
		menu.add(Menu.NONE, MENU_ABOUT, MENU_ABOUT,
				this.getString(R.string.about)).setIcon(
				R.drawable.composer_thought);
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		log("onBackPressed.");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();

		// 使fragment彻底从activity上detach掉
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// mDouBanDatabase.queryDatabase();

	}

	@Override
	public boolean onSearchRequested() {
		// TODO Auto-generated method stub
		// globalSearch will launch web browser search
		Log.v(this, "onSearchRequested");

		this.startSearch(null, false, null, false);
		return true; // !
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		// super.onNewIntent(intent);
		this.setIntent(intent);
		Log.v(this, "onNewIntent search");
		handleSearchIntent(intent);
	}

	void handleSearchIntent(Intent intent) {
		Log.v(this, "handleSearchIntent");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			log("___________u wanna search :" + query);
			doMySearch(query);
		}
	}

	void doMySearch(String query) {
		// Cursor c=mDouBanDatabase.queryBookFuzzy(query);

		Intent i = new Intent();
		i.putExtra("query", query);
		i.setClass(this, SearchResultListActivity.class);
		this.startActivity(i);
	}

	void launchSettings() {
		Intent i = new Intent();
		i.setClass(mContext, PersonalSettingsActivity.class);
		this.startActivity(i);
	}

	private void showAboutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.author);
		builder.setMessage(R.string.author_contact);
		builder.setPositiveButton(R.string.ok_button,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				});
		builder.show();
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(Activity context, FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position < 0 || position > mFragmentList.size())
				return null;

			return mFragmentList.get(position);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// log( "SectionsPagerAdapter getPageTitle " + position);
			Locale l = Locale.getDefault();
			if (position < 0 || position >= mTabs.length) {
				log("getPageTitle position out of bounds!");
				return null;
			}
			return mTabs[position].toLowerCase(l);
		}
	}

	/**
	 * show a result dialog
	 * */
	private void showDialog(int title, CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(R.string.ok_button,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// mHandler.sendEmptyMessage(MESSAGE_FETCH_BOOK_ISBN);

						log("ScannedISBN===>"
								+ PbaMain.this.getResources().getString(
										R.string.isbn_scanned_result) + ":"
								+ ScannedISBN);
						// TODO Auto-generated method stub
					}
				});
		builder.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.v(this, "onActivityResult");
		if (OpenQrScanRequestCode == requestCode) {
			if (RESULT_OK == resultCode) {
				ZxingDataParcelable data = (ZxingDataParcelable) intent
						.getParcelableExtra("zxing");
				if (null != data) {
					Log.v(this, data.toString());
					if (data.format != null && ISBN_TYPE.equals(data.format)
							&& null != data.isbn) {
						// mHandler.sendEmptyMessage(MESSAGE_FETCH_BOOK_ISBN);
						ScannedISBN = data.isbn;
						if (mFragmentList.get(mCurrentpageIndex) instanceof BooksGatherFragment)
							((BooksGatherFragment) mFragmentList
									.get(mCurrentpageIndex))
									.searchBook(data.isbn);
						return;
					}
				}

				// drop other product code
				showDialog(R.string.result_failed,
						getString(R.string.result_failed_parse));
				return;
			}
		}
	}

	void log(String msg) {
		Log.v(this, msg);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

		onClickView(null, true);

		return false;
	}

}
