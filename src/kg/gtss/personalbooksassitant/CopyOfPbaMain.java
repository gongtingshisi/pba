package kg.gtss.personalbooksassitant;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kg.gtss.search.SearchResultCursorAdapter;
import kg.gtss.search.SearchResultListActivity;
import kg.gtss.utils.Common;
import kg.gtss.utils.FileUtils;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;

import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.widget.SimpleCursorAdapter;
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
import com.gtss.douban.DouBanBooks;
import com.gtss.douban.DouBanContentProvider;
import com.gtss.douban.DouBanDatabase;
import com.gtss.douban.DouBanQuery;
import com.gtss.douban.KgCursorLoader;
import com.gtss.useraccount.UserAccountActivity;
import com.kg.zxing.CaptureActivity;
import com.progress.bookreading.ReadingProgressActivity;

//import android.util.Log;
/**
 * 这个包含了SearchResultListActivity的功能,可以考虑继承他,布局
 * */
public class CopyOfPbaMain extends FragmentActivity implements
		LoaderCallbacks<Cursor>, OnGetPoiSearchResultListener, OnTouchListener {
	Context mContext;

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
	/**
	 * 搜索到的所有书店
	 * */
	List<PoiInfo> mBookshopList;
	/**
	 * 用于存储书店结果的适配器
	 * */
	BookshopAdapter mBookshopAdapter;
	/**
	 * // 为你想要跳转到的页码
	 * */
	private int load_Index = 0;

	private PoiSearch mPoiSearch = null;
	/**
	 * POI查询结果每页显示几个，如何翻页？10 on default
	 * */
	final int PoiSearchResultCapacityPerPage = 100;
	MapView mMapView;
	/**
	 * 用于显示搜索到的书店列表
	 * */
	ListView mBookshopListView;
	/**
	 * 用于显示搜索的书店数量
	 * */
	TextView mBookshopNum;
	LocationClient mLocationClient;
	MyLocationListener mMyLocationListener = new MyLocationListener();
	TextView mLocInfo;
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	BaiduMap mBaiduMap;
	boolean isFirstLoc = true;// 是否首次定位

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation loc) {
			// TODO Auto-generated method stub
			if (loc == null || mMapView == null)
				return;
			// locate myself
			mLocInfo.setText(loc.getAddrStr() + ", "
					+ loc.getLocationDescribe());
/*
			Log.v(this,
					"country:" + loc.getCountry() + "	city:" + loc.getCity()
							+ "		addr:" + loc.getAddrStr() + "	building:"
							+ loc.getBuildingID() + "	district:"
							+ loc.getDistrict());
*/
			// locate my location,map moves
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(loc.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(loc.getLatitude())
					.longitude(loc.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(loc.getLatitude(), loc.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}

			// poi search result
			try {
				mPoiSearch.searchInCity((new PoiCitySearchOption())
						.city(loc.getCity())
						.keyword(
								mContext.getResources().getString(
										R.string.bookshop))
						.pageCapacity(PoiSearchResultCapacityPerPage)
						.pageNum(load_Index));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// /////////////////
	// //////////////////////////////

	private DouBanDatabase mDouBanDatabase;

	// private SQLiteDatabase mDatabaseManager;

	private ShareActionProvider mShareActionProvider;
	private ActionBar mActionBar;
	// title on each fragment in pagerview from pageadapter
	private String[] mTabs;

	private MenuItem menuScanBookItem;

	// format to match
	final private static String ScanningResultFormatPattern = "Format:";
	final private static String ScanningResultContentsPattern = "Contents:";
	// isbn means ean13
	final private static String ISBN_TYPE = "EAN_13";

	private int LOADER_ID = Common.LOADER_ID_PbaMainActivity;

	// current isbn scanned
	private static String ScannedISBN;
	/**
	 * search books from internet
	 * */
	// private Button mSearchBooksInfoBtn;
	private TextView mTitleText;
	private ImageView mBookCover;
	private TextView mAuthorText;
	private TextView mSummaryText;
	private TextView mISBNText;
	private TextView mDateText;

	// the uri fetched from server,means the book cover image
	Uri mBookCoverImgUri;
	/**
	 * store the info searched from DouBan internet
	 * */
	private DouBanBooks mSearchBookInfoFromDouBan = new DouBanBooks();
	// all tab view
	private View v1, v2, v3;
	// container all tab layout
	private static ArrayList<View> ViewList = new ArrayList<View>();
	// the indicater where we stay,in general,it's a short underline
	private PagerTabStrip mPagerTabStrip;

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	// MyPagerAdapter mMyPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	/**
	 * current index of page
	 * */
	private int mCurrentpageIndex = 0;

	// v3 :local manager
	/**
	 * the list view of local books manager
	 * */
	private ListView mLocalManagerListView;
	/**
	 * CursorLoader needs a period to finish plenty of db info
	 * */
	private ProgressBar mLocalManagerProgressView;
	/**
	 * the adpter to list view of local books manager
	 * */
	// private SimpleCursorAdapter mSimpleCursorAdapter;

	/**
	 * get info from internet about this book,even though it's invalid
	 * */
	public static final int MESSAGE_REFRESH_BOOKINFO = 0001;

	public static final int MESSAGE_INVALID_BOOKINFO = 0002;
	/**
	 * get isbn through zxing or mine scanning(which will be ported into)
	 * */
	public static final int MESSAGE_FETCH_BOOK_ISBN = 0003;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_REFRESH_BOOKINFO:

				mTitleText.setText(getString(R.string.book_title)
						+ mSearchBookInfoFromDouBan.title);
				mAuthorText.setText(getString(R.string.book_author)
						+ mSearchBookInfoFromDouBan.author.get(0));
				mSummaryText.setText(getString(R.string.book_summary)
						+ mSearchBookInfoFromDouBan.summary);
				mISBNText.setText(getString(R.string.isbn)
						+ mSearchBookInfoFromDouBan.isbn13);
				mDateText.setText(getString(R.string.date)
						+ mSearchBookInfoFromDouBan.date);
				// set book cover,should placed in another thread
				if (mBookCoverImgUri != null) {
					mBookCover.setImageURI(mBookCoverImgUri);
				} else {
					// text display
				}
				break;
			case MESSAGE_INVALID_BOOKINFO:

				mTitleText.setText(getString(R.string.book_title)
						+ getString(R.string.unknown));
				mAuthorText.setText(getString(R.string.book_author)
						+ getString(R.string.unknown));
				mSummaryText.setText(getString(R.string.book_summary)
						+ getString(R.string.unknown));
				mISBNText.setText(getString(R.string.isbn)
						+ getString(R.string.unknown));
				mDateText.setText(getString(R.string.date)
						+ getString(R.string.unknown));
				break;
			// case MESSAGE_FETCH_BOOK_ISBN:
			//
			// mSearchBooksitem.setEnabled(true);
			// break;
			default:
				return;
			}
		}

	};

	/**
	 * init v1:collect book view
	 * */
	private void initV1CollectBookView() {
		v1 = getLayoutInflater().inflate(R.layout.books_scanning, null);
		mTitleText = (TextView) v1.findViewById(R.id.title);
		mBookCover = (ImageView) v1.findViewById(R.id.book_cover);
		mAuthorText = (TextView) v1.findViewById(R.id.author);
		mSummaryText = (TextView) v1.findViewById(R.id.summary);
		mISBNText = (TextView) v1.findViewById(R.id.isbn);
		mDateText = (TextView) v1.findViewById(R.id.date);
		ViewList.add(v1);
	}

	/**
	 * 
	 * init v2:internet view
	 * */
	private void initV2InternetView() {
		v2 = getLayoutInflater().inflate(R.layout.internet, null);
		mMapView = (MapView) v2.findViewById(R.id.bdMapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBookshopListView = (ListView) v2.findViewById(R.id.bookshop_list);
		mBookshopNum = (TextView) v2.findViewById(R.id.bookshop_result);
		mBookshopAdapter = new BookshopAdapter(this);
		mLocInfo = (TextView) v2.findViewById(R.id.my_location);
		ViewList.add(v2);
	}

	/**
	 * initialize baidu lbs feature
	 * */
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		// int span = 10000;
		// option.setScanSpan(span);
		option.setIsNeedAddress(true);
		option.setOpenGps(true);
		option.setLocationNotify(true);
		option.setIsNeedLocationDescribe(true);
		option.setIsNeedLocationPoiList(true);
		option.setIgnoreKillProcess(false);
		option.SetIgnoreCacheException(false);
		option.setEnableSimulateGps(false);
		mLocationClient.setLocOption(option);
		mLocationClient.start();

		// init poi search
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

	}

	/**
	 * init v3:local manager
	 * */
	private void initV3LocalManagerView() {
		v3 = getLayoutInflater().inflate(R.layout.local_manager, null);
		mLocalManagerListView = (ListView) v3
				.findViewById(R.id.local_manager_list);
		mLocalManagerProgressView = (ProgressBar) v3
				.findViewById(R.id.local_manager_load_progress);

		/*
		 * mLocalManagerListView.setOnItemClickListener(new
		 * OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View view,
		 * int position, long id) { // TODO Auto-generated method stub
		 * log("onItemClick " + position + "	" + id); if (mSimpleCursorAdapter
		 * != null && mSimpleCursorAdapter.getCount() > 0) { Cursor c =
		 * mSimpleCursorAdapter.getCursor(); c.moveToPosition(position);
		 * 
		 * Intent intent = new Intent(); intent.putExtra(
		 * DatabaseBookInterface.COLUMN_NAME_ISBN, c.getString(c
		 * .getColumnIndex(DatabaseBookInterface.COLUMN_NAME_ISBN)));
		 * 
		 * c.close();//must NOT be closed ,because Cursor index to
		 * mSimpleCursorAdapter,which will be used again
		 * 
		 * intent.setClassName("kg.gtss.personalbooksassitant",
		 * "kg.gtss.personalbooksassitant.SingleBookDetailActivity"); try {
		 * PbaMain.this.startActivity(intent); } catch (Exception e) {
		 * log("kg.gtss.personalbooksassitant.SingleBookDetailActivity NOT found."
		 * ); }
		 * 
		 * } } });
		 */mLocalManagerListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						log("onItemLongClick " + position + "	" + id);
						// Note: You cannot inflate a layout into a fragment
						// when that layout includes a <fragment>. Nested
						// fragments are only supported when added to a fragment
						// dynamically.
						// FragmentTransaction ft = PbaMain.this
						// .getSupportFragmentManager().beginTransaction();
						// mSingleBookDetailFragment = new
						// SingleBookDetailFragment();
						// ft.add(R.id.local_manager,
						// mSingleBookDetailFragment);
						// ft.commit();
						// 查询到这本书的名字传入
						deleteOneBook(position);
						return true;
					}
				});

		ViewList.add(v3);
	}

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
		((Activity) CopyOfPbaMain.this).startActivityForResult(i,
				OpenQrScanRequestCode);// startActivityForResult
	}

	void reloadData() {
		// log("PbaMain reload db loader");
		// 此处因为更新了数据库整个重新加载数据库,更新ui.另一个办法就是使用contentprovider,cursor.setcontentchange.
		// 此处这个方法比较重量,臃肿
		CopyOfPbaMain.this.getLoaderManager().destroyLoader(LOADER_ID);
		CopyOfPbaMain.this.getLoaderManager().initLoader(LOADER_ID, null,
				CopyOfPbaMain.this);
	}

	void launchReadingProgress() {
		Intent i = new Intent();
		i.setClass(mContext, ReadingProgressActivity.class);
		startActivity(i);
	}

	void launchPersonalEntry() {
		Intent i = new Intent();
		i.setClass(mContext, GridViewEntryActivity.class);
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
		Fragment f = new InternetResourcesFragment();
		mFragmentList.add(f);
		f = new BooksGatherFragment();
		mFragmentList.add(f);
		f = new AllLocalBooksFragment();
		mFragmentList.add(f);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.pba_main);

		initFragment();

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
							log("argo=" + arg0.getId() + " click");
							Toast.makeText(getApplicationContext(),
									"argo=" + arg0.getId() + " click", 300)
									.show();
							// scan books
							if (arg0.getId() == R.id.composer_button_photo) {
								launchZxing();
							} else if (arg0.getId() == R.id.composer_button_thought) {
								// reading progress
								launchReadingProgress();
							} else if (arg0.getId() == R.id.composer_button_people) {
								launchMainEntry();
							} else if (arg0.getId() == R.id.composer_button_place) {
								// search nearby
								launchPersonalEntry();
							}
						}
					});
		}

		SDKInitializer.initialize(getApplicationContext());
		mLocationClient = new LocationClient(this.getApplicationContext());
		mLocationClient.registerLocationListener(mMyLocationListener);
		initLocation();

		// create db
		mDouBanDatabase = new DouBanDatabase(CopyOfPbaMain.this,
				new DatabaseChangedCallback() {

					@Override
					public void DatabaseChangedCallbackUi() {
						// TODO Auto-generated method stub
						Log.v(this, "DatabaseChangedCallbackUi");
						reloadData();
					}
				});

		this.getLoaderManager().enableDebugLogging(Log.DEBUG);

		// // load all the tab view
		initV1CollectBookView();
		initV2InternetView();
		initV3LocalManagerView();

		mTabs = CopyOfPbaMain.this.getResources().getStringArray(R.array.tab);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this,
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				log("onClick	" + v.getTag());
			}
		});
		mViewPager.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				log("onFocusChange " + hasFocus);
			}
		});
		mViewPager.setOnDragListener(new OnDragListener() {

			@Override
			public boolean onDrag(View v, DragEvent event) {
				// TODO Auto-generated method stub
				log("onDrag ");
				return false;
			}
		});

		mViewPager.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				log("onLongClick");
				return false;
			}
		});

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
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// mPagerTabStrip = (PagerTabStrip) this.findViewById(R.id.pager_tab);
		// mPagerTabStrip.setTabIndicatorColorResource(R.color.red);
		// mPagerTabStrip.setDrawFullUnderline(false);
		// mPagerTabStrip.setBackgroundResource(R.color.gray);

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pba_main_menu, menu);
		menuScanBookItem = menu.findItem(R.id.action_scan);

		MenuItem menuSearchItem = menu.findItem(R.id.action_search);

		MenuItem menuShareItem = menu.findItem(R.id.action_share);
		mShareActionProvider = (ShareActionProvider) menuShareItem
				.getActionProvider();
		doShare(contructShareIntent());
		mShareActionProvider
				.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {

					@Override
					public boolean onShareTargetSelected(
							ShareActionProvider arg0, Intent arg1) {
						// TODO Auto-generated method stub
						return false;
					}
				});

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

		mLocationClient.stop();

		// 关闭定位图层
		if (null != mBaiduMap)
			mBaiduMap.setMyLocationEnabled(false);
		if (null != mPoiSearch)
			mPoiSearch.destroy();
		mMapView.onDestroy();
		mMapView = null;

		super.onDestroy();

		// 使fragment彻底从activity上detach掉
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		mMapView.onPause();
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
		// 在此处需要重新刷新一下listview,可能已经在其他activity修改了数据库
		reloadData();

		mMapView.onResume();
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

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		log("onMenuItemSelected featureId:" + featureId + ",item:"
				+ item.getTitle());
		if (item.getItemId() == R.id.action_about) {
			showAboutDialog();
		} else if (item.getItemId() == R.id.action_search) {
			this.onSearchRequested();
		} else if (item.getItemId() == R.id.action_login) {
			Intent i = new Intent();
			i.setClass(mContext, UserAccountActivity.class);
			this.startActivity(i);
		} else if (item.getItemId() == menuScanBookItem.getItemId()) {
			launchZxing();
		} else if (item.getItemId() == R.id.action_settings) {
			launchSettings();
		}

		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(Activity context, FragmentManager fm) {
			super(fm);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			// super.destroyItem(container, position, object);

			// TODO Auto-generated method stub

			container.removeView(ViewList.get(position));
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.

			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position);

			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return ViewList.size();
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
								+ CopyOfPbaMain.this.getResources().getString(
										R.string.isbn_scanned_result) + ":"
								+ ScannedISBN);
						// TODO Auto-generated method stub
					}
				});
		builder.show();
	}

	/**
	 * search book from search engine
	 * */
	void searchBook() {

		new DouBanQuery(CopyOfPbaMain.this, new FeedbackResult() {

			@Override
			public void feedbackResult(int result, Uri uri) {
				// TODO Auto-generated method stub
				switch (result) {
				case Common.SEARCH_BOOK_SUCESS:
					mBookCoverImgUri = uri;
					mHandler.sendEmptyMessage(MESSAGE_REFRESH_BOOKINFO);
					mSearchBookInfoFromDouBan.date = TimeUtils.getCurrentTime();
					mDouBanDatabase.insert(mSearchBookInfoFromDouBan);

					Log.v(this, "insert");
					break;
				case Common.SEARCH_BOOK_FAIL_NO_NETWORK:
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.unconnected_network),
							Toast.LENGTH_SHORT).show();
					mHandler.sendEmptyMessage(MESSAGE_INVALID_BOOKINFO);
					break;
				case Common.SEARCH_BOOK_FAIL_UNKNOWN:
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.search_fail), Toast.LENGTH_SHORT)
							.show();
					mHandler.sendEmptyMessage(MESSAGE_INVALID_BOOKINFO);
					break;
				default:
					break;
				}
			}
		}, mSearchBookInfoFromDouBan).execute(ScannedISBN);

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
						searchBook();
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

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {

		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();

		}

		@Override
		public void onDestroyView() {
			// TODO Auto-generated method stub
			super.onDestroyView();

		}

		@Override
		public void onDetach() {
			// TODO Auto-generated method stub
			super.onDetach();

		}

		@Override
		public void onPause() {
			// TODO Auto-generated method stub
			super.onPause();

		}

		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();

		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();

		}

		@Override
		public void onStop() {
			// TODO Auto-generated method stub
			super.onStop();

		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);

		}

		@Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
			super.onAttach(activity);

		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);

		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onViewCreated(view, savedInstanceState);

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			// get index tab.in fact,it equals mCurrentPageIndex
			int index = getArguments().getInt(
					DummySectionFragment.ARG_SECTION_NUMBER);
			// int layoutResource;
			// View root;

			// switch (index) {
			// case 1:
			// case 2:
			// default:
			// root = inflater.inflate(R.layout.dummy, null);
			// ScannedResultText = (TextView) root
			// .findViewById(R.id.isbn_scanned);
			// }
			// return root;
			// /!!!!!!!!java.lang.IllegalStateException: The specified child
			// already has a parent. You must call removeView() on the child's
			// parent first.

			return ViewList.get(index);
		}
	}

	/**
	 * my PagerAdapter
	 * */
	private class MyPagerAdapter extends PagerAdapter {
		private ArrayList<View> mViewList;

		public MyPagerAdapter(ArrayList<View> a) {
			mViewList = a;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mViewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViewList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			// return super.instantiateItem(container, position);
			// java.lang.IllegalStateException: The specified child already has
			// a parent. You must call removeView() on the child's parent first.

			container.removeView(mViewList.get(position));
			container.addView(mViewList.get(position));
			return mViewList.get(position);

			// TODO Auto-generated method stub
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		log("onCreateLoader " + id + "	");

		return new CursorLoader(this, DouBanContentProvider.CONTENT_URI,
				DouBanContentProvider.PROJECTION, SELECT, SELECT_ARGS, ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub
		log("onLoadFinished 	");
		if (null == data) {
			log("LoaderCallbacks null");
		} else {
			/*
			 * mSimpleCursorAdapter = new SimpleCursorAdapter(this,
			 * R.layout.local_scanned_books, data, new String[] {
			 * DouBanBooks.TYPE_title, DouBanBooks.TYPE_author }, new int[] {
			 * R.id.book_title, R.id.book_author });
			 */
			// TimeLineAdapter adapter = new TimeLineAdapter(PbaMain.this);
			// mLocalManagerListView.setAdapter(adapter);
			// mLocalManagerListView.setAdapter(mSimpleCursorAdapter);
			mLocalManagerProgressView.setVisibility(View.INVISIBLE);
			bindAdapter(CopyOfPbaMain.this, data);
		}
	}

	/**
	 * 
	 * bind data to view
	 * **/
	void bindAdapter(Context context, Cursor c) {
		mTimeLineCursorAdapter = new SearchResultCursorAdapter(context, c,
				mDouBanDatabase);

		mLocalManagerListView.setAdapter(mTimeLineCursorAdapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		log("onLoaderReset 	");
	}

	void log(String msg) {
		Log.v(this, msg);
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		// TODO Auto-generated method stub
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		} else {
			Log.v(this, "&&&&&& " + result.toString());
			Toast.makeText(this, result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 搜索结果标注如何自定义点击处理
	 * */
	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

	/**
	 * poi搜索结果书店列表适配器
	 * */
	class BookshopAdapter extends BaseAdapter {
		Context context;

		public BookshopAdapter(Context c) {
			context = c;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (null == mBookshopList)
				return 0;

			return mBookshopList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub

			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub

			return 0;
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;
			if (null != v) {
				holder = (ViewHolder) v.getTag();

			} else {
				holder = new ViewHolder();
				v = LayoutInflater.from(context).inflate(
						R.layout.bookshopinfo_item, null);
				holder.id = (TextView) v.findViewById(R.id.bookshop_id);
				holder.info = (TextView) v.findViewById(R.id.bookshop_info);
				v.setTag(holder);
			}

			Bookshop bookshop = new Bookshop();
			bookshop.name = mBookshopList.get(position).name;
			bookshop.addr = mBookshopList.get(position).address;
			bookshop.phone = mBookshopList.get(position).phoneNum;
			// 总共搜索到的书名可能很大，但是每一页的容量却很小，这块应使用总序号
			holder.id.setText(" " + (position + 1));
			holder.info.setText(bookshop.toString());

			return v;
		}
	}

	/**
	 * 一条书店信息在listview中的显示视图
	 * */
	class ViewHolder {
		TextView id;
		TextView info;
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG).show();
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBookshopList = result.getAllPoi();
			// bind the poi search result to adapter
			mBookshopListView.setAdapter(mBookshopAdapter);
			// mBookshopAdapter.notifyDataSetChanged();
			mBookshopNum.setText(this.getResources().getString(
					R.string.bookshop_num, mBookshopList.size())
					+ "(" + result.getTotalPoiNum() + ")");
			Log.v(this, "%%%%" + result.getTotalPoiNum() + " listsize:"
					+ mBookshopList.size());
			Log.v(this,
					this.getResources().getString(R.string.bookshop_num,
							mBookshopList.size()));
			for (PoiInfo pi : result.getAllPoi()) {
				// Log.v(this, "addr:" + pi.address + ",name:" + pi.name
				// + ",phone:" + pi.phoneNum);
			}

			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(this, strInfo, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

		onClickView(null, true);

		return false;
	}
}
