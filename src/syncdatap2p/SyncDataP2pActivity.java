package syncdatap2p;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.FileUtils;
import kg.gtss.utils.Log;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * INITIALIZE->DISCOVER->REQUEST->CONNECT->REQUEST CONNECTION
 * INFO.android:launchMode="singleTask"
 * */
public class SyncDataP2pActivity extends Activity {
	// adb logcat wifidirectdemo:v GroupOwnerSocketHandler:v
	// ClientSocketHandler:v ChatHandler:v *:s
	Context mContext;
	WifiP2pMsgReceiver mWifiP2pMsgReceiver = new WifiP2pMsgReceiver();
	WifiP2pManager mWifiP2pManager;
	Channel mChannel;
	IntentFilter mIntentFilter = new IntentFilter();
	DeviceListFragment mDeviceListFragment;

	public static final String TXTRECORD_PROP_AVAILABLE = "available";
	String WifiP2pDnsSdServiceName = "pba_name";
	String WifiP2pDnsSdServiceType = "_presence._tcp";

	// DeviceDetailFragment mDeviceDetailFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		this.setTitle("与好友同步书单");
		this.getActionBar().setDisplayHomeAsUpEnabled(true);

		this.setContentView(R.layout.wifip2p_state);
		mDeviceListFragment = (DeviceListFragment) this.getFragmentManager()
				.findFragmentById(R.id.frag_list);
		// mDeviceDetailFragment = (DeviceDetailFragment) this
		// .getFragmentManager().findFragmentById(R.id.frag_detail);

		initIntentFilter();

		mWifiP2pManager = (WifiP2pManager) this
				.getSystemService(Service.WIFI_P2P_SERVICE);

		Log.v(this, "initialize channel...");
		mChannel = mWifiP2pManager.initialize(this, this.getMainLooper(),
				mDeviceListFragment);// 1.INITIALIZE

		Log.v(this, "initialize discover ...");
		mWifiP2pManager.discoverPeers(mChannel, mDiscoverActionListener);// 2.DISCOVER

		// initService();

		this.registerReceiver(mWifiP2pMsgReceiver, mIntentFilter);
	}

	ActionListener mDiscoverActionListener = new ActionListener() {

		@Override
		public void onFailure(int reason) {
			// TODO Auto-generated method stub
			// Log.v(this, "mDiscoverActionListener OnFailure " + reason);
			switch (reason) {
			case WifiP2pManager.P2P_UNSUPPORTED:
				// set error state
				mDeviceListFragment.setLocalStatus("Fail:P2P_UNSUPPORTED");
				break;
			case WifiP2pManager.ERROR:
				mDeviceListFragment.setLocalStatus("Fail:ERROR");
				break;
			case WifiP2pManager.BUSY:
				mDeviceListFragment.setLocalStatus("Fail:BUSY");
				break;
			}
		}

		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub

			// set Available state
			mDeviceListFragment
					.setLocalStatus(getDeviceStatus(WifiP2pDevice.AVAILABLE));
		}
	};

	void initService() {

		Map<String, String> record = new HashMap<String, String>();
		record.put(TXTRECORD_PROP_AVAILABLE, "visible");
		WifiP2pDnsSdServiceInfo p2pDnsService = WifiP2pDnsSdServiceInfo
				.newInstance(WifiP2pDnsSdServiceName, WifiP2pDnsSdServiceType,
						record);
		mWifiP2pManager.addLocalService(mChannel, p2pDnsService,
				new ActionListener() {

					@Override
					public void onFailure(int arg0) {
						// TODO Auto-generated method stub
						Log.v(this, "添加本地搜索服务失败..." + arg0);
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.v(this, "添加本地搜索服务成功...");
					}
				});
		mWifiP2pManager.setDnsSdResponseListeners(mChannel,
				new DnsSdServiceResponseListener() {

					@Override
					public void onDnsSdServiceAvailable(String instanceName,
							String registrationType, WifiP2pDevice srcDevice) {
						// TODO Auto-generated method stub
						Log.v(this, "onDnsSdServiceAvailable...");
						if (instanceName.equals(WifiP2pDnsSdServiceName)) {
							Log.v(this, "找到" + WifiP2pDnsSdServiceName
									+ "服务...设备名称:" + srcDevice.deviceName
									+ "		设备地址:" + srcDevice.deviceAddress
									+ "		设备状态:"
									+ getDeviceStatus(srcDevice.status));
						}
					}
				}, new DnsSdTxtRecordListener() {

					@Override
					public void onDnsSdTxtRecordAvailable(
							String fullDomainName, Map<String, String> record,
							WifiP2pDevice device) {
						// TODO Auto-generated method stub
						Log.v(this, "onDnsSdTxtRecordAvailable...");
					}
				});
		WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest
				.newInstance(WifiP2pDnsSdServiceName, WifiP2pDnsSdServiceType);
		mWifiP2pManager.addServiceRequest(mChannel, serviceRequest,
				new ActionListener() {

					@Override
					public void onFailure(int arg0) {
						// TODO Auto-generated method stub
						Log.v(this, "增加服务请求失败...");
					}

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.v(this, "增加请求服务成功...");
					}
				});
		mWifiP2pManager.discoverServices(mChannel, new ActionListener() {

			@Override
			public void onFailure(int arg0) {
				// TODO Auto-generated method stub
				Log.v(this, "初始化搜索服务失败,错误码:" + arg0);
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				Log.v(this, "初始化搜索服务成功...");
			}
		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(mWifiP2pMsgReceiver);
	}

	void initIntentFilter() {
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	}

	/**
	 * get device state
	 * */
	public static String getDeviceStatus(int deviceStatus) {
		String state;
		switch (deviceStatus) {
		case WifiP2pDevice.AVAILABLE:// 3
			state = "Available";
		case WifiP2pDevice.INVITED:// 1
			state = "Invited";
		case WifiP2pDevice.CONNECTED:// 0
			state = "Connected";
		case WifiP2pDevice.FAILED:// 2
			state = "Failed";
		case WifiP2pDevice.UNAVAILABLE:// 4
			state = "Unavailable";
		default:
			state = "Unknown";
		}
		return state;
	}

	/**
	 * server side:save data
	 * */
	public class ServerSideSaveDataTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub
			try {
				// use port on server side
				ServerSocket server = new ServerSocket(8000);
				Log.v(this, "address:"
						+ server.getInetAddress().getHostAddress() + ":"
						+ server.getLocalPort());

				// accept a client connection
				Socket client = server.accept();

				// store the data from client
				final File f = new File(
						Environment.getExternalStorageDirectory() + "/"
								+ mContext.getPackageName() + "/wifip2pshared-"
								+ System.currentTimeMillis() + ".jpg");
				File dirs = new File(f.getParent());
				if (!dirs.exists())
					dirs.mkdirs();
				f.createNewFile();
				InputStream inputstream = client.getInputStream();
				FileUtils.copyBigDataTo(inputstream, f.getAbsolutePath());

				// close server
				server.close();
				return f.getAbsolutePath();
			} catch (IOException e) {
				Log.v(this, e.getMessage());
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			if (result != null) {
				Log.v(this, "copy sucess !");
				// open file with gallery
				Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.parse("file://" + result), "image/*");
				mContext.startActivity(intent);
			}

		}

	}

	/**
	 * client side :transfer data
	 * */
	public class ClientSideTransferDataTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub

			String host = "5e:f8:a1:12:ce:1c";
			int port = 0;
			int len;
			Socket socket = new Socket();
			byte buf[] = new byte[1024];

			try {
				/**
				 * Create a client socket with the host, port, and timeout
				 * information.
				 */
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)), 500);

				/**
				 * Create a byte stream from a JPEG file and pipe it to the
				 * output stream of the socket. This data will be retrieved by
				 * the server device.
				 */
				OutputStream outputStream = socket.getOutputStream();
				ContentResolver cr = mContext.getContentResolver();
				InputStream inputStream = null;
				inputStream = cr.openInputStream(Uri
						.parse("path/to/picture.jpg"));
				while ((len = inputStream.read(buf)) != -1) {
					outputStream.write(buf, 0, len);
				}
				outputStream.close();
				inputStream.close();
			} catch (FileNotFoundException e) {
				// catch logic
			} catch (IOException e) {
				// catch logic
			}

			/**
			 * Clean up any open sockets when done transferring or if an
			 * exception occurred.
			 */
			finally {
				if (socket != null) {
					if (socket.isConnected()) {
						try {
							socket.close();
						} catch (IOException e) {
							// catch logic
						}
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub

		}

	}

	public class WifiP2pMsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			/* Log.v(this, "onReceive " + intent.getAction()); */

			String action = intent.getAction();
			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				// 确定Wi-Fi Direct模式是否已经启用
				int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,
						-1);
				if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
					Log.v(this, "WifiP2pManager.WIFI_P2P_STATE_ENABLED");
					// activity.setIsWifiP2pEnabled(true);
				} else {
					Log.v(this, "WifiP2pManager.WIFI_P2P_STATE_DISABLED ");
					// activity.setIsWifiP2pEnabled(false);
				}

			} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION
					.equals(action)) {
				// 对等点列表已经改变
				// Log.v(this, "request peers...");
				mWifiP2pManager.requestPeers(mChannel, mDeviceListFragment);// 3.REQUEST
																			// PEERS
				// set Invited status,waiting to accepted
				mDeviceListFragment
						.setLocalStatus(getDeviceStatus(WifiP2pDevice.AVAILABLE));

			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
					.equals(action)) {
				// 表示此处已经p2p连接上了
				Log.v(this, "connection changed~");
				WifiP2pInfo wifiP2pInfo = (WifiP2pInfo) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
				if (wifiP2pInfo != null
						&& wifiP2pInfo.groupOwnerAddress != null) {
					Log.v(this,
							"WIFI_P2P_CONNECTION_CHANGED_ACTION WifiP2pInfo:"
									+ "groupFormed:"
									+ wifiP2pInfo.groupFormed
									+ ", groupOwnerAddress:"
									+ wifiP2pInfo.groupOwnerAddress
											.getHostAddress()
									+ " ,isGroupOwner:"
									+ wifiP2pInfo.isGroupOwner);
				}
				NetworkInfo networkInfo = (NetworkInfo) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
				if (networkInfo.isAvailable() && networkInfo.isConnected()) {
					Log.v(this, "request Connection Info......");

					mWifiP2pManager.requestConnectionInfo(mChannel,
							mDeviceListFragment);// 5.REQUEST CONNECTION
													// INFO
				}
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
					.equals(action)) {
				// 用来获取本地设备信息的接口
				Log.v(this, "Getting local device info ...");
				WifiP2pDevice device = (WifiP2pDevice) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

				mDeviceListFragment.setLocalNameStatus(device.deviceName,
						getDeviceStatus(device.status));

			} else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION
					.equals(action)) {
				int state = intent.getIntExtra(
						WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
				if (WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED == state) {
					Log.v(this, "WIFI_P2P_DISCOVERY_CHANGED_ACTION start");
				} else if (WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED == state) {
					Log.v(this, "WIFI_P2P_DISCOVERY_CHANGED_ACTION stop");
				}

			}
		}
	}
}
