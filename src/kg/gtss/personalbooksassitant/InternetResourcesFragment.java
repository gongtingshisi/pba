package kg.gtss.personalbooksassitant;

import java.util.List;

import kg.gtss.utils.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用于管理本地互联网资源的fragment
 * */
public class InternetResourcesFragment extends Fragment implements
		OnGetPoiSearchResultListener {
	Activity mActivity;
	MapView mMapView;
	ListView mBookshopListView;
	TextView mBookshopNum;
	TextView mLocInfo;
	BaiduMap mBaiduMap;
	boolean isFirstLoc = true;// 是否首次定位
	LocationClient mLocationClient;
	/**
	 * // 为你想要跳转到的页码
	 * */
	private int load_Index = 0;
	/**
	 * 搜索到的所有书店
	 * */
	List<PoiInfo> mBookshopList;
	MyLocationListener mMyLocationListener = new MyLocationListener();
	/**
	 * 用于存储书店结果的适配器
	 * */
	BookshopAdapter mBookshopAdapter;
	private PoiSearch mPoiSearch = null;
	/**
	 * POI查询结果每页显示几个，如何翻页？10 on default
	 * */
	final int PoiSearchResultCapacityPerPage = 100;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		initLocation();
		View v2 = inflater.inflate(R.layout.internet, null);

		mMapView = (MapView) v2.findViewById(R.id.bdMapView);
		mBaiduMap = mMapView.getMap();
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBookshopListView = (ListView) v2.findViewById(R.id.bookshop_list);
		mBookshopNum = (TextView) v2.findViewById(R.id.bookshop_result);
		mBookshopAdapter = new BookshopAdapter(this.getActivity());
		mLocInfo = (TextView) v2.findViewById(R.id.my_location);

		return v2;
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

	/**
	 * initialize baidu lbs feature
	 * */
	private void initLocation() {
		SDKInitializer.initialize(getActivity().getApplicationContext());
		mLocationClient = new LocationClient(getActivity()
				.getApplicationContext());
		mLocationClient.registerLocationListener(mMyLocationListener);

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

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationClient.stop();

		// 关闭定位图层
		if (null != mBaiduMap)
			mBaiduMap.setMyLocationEnabled(false);
		if (null != mPoiSearch)
			mPoiSearch.destroy();
		mMapView.onDestroy();
		mMapView = null;
	}

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
								mActivity.getResources().getString(
										R.string.bookshop))
						.pageCapacity(PoiSearchResultCapacityPerPage)
						.pageNum(load_Index));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this.getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Log.v(this, "&&&&&& " + result.toString());
			Toast.makeText(this.getActivity(),
					result.getName() + ": " + result.getAddress(),
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		mMapView.onResume();
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(mActivity, "未找到结果", Toast.LENGTH_LONG).show();
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
				Log.v(this, "addr:" + pi.address + ",name:" + pi.name
						+ ",phone:" + pi.phoneNum);
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
			Toast.makeText(mActivity, strInfo, Toast.LENGTH_LONG).show();
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
}
