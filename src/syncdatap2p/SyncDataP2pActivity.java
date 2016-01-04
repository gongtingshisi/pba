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
 * INFO.android:launchMode="singleTask".adb logcat pba:v WifiP2pManager:v
 * WifiP2pService:v *:s
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
	WifiP2pDnsSdServiceRequest serviceRequest;
	String DEVICE_NAME = "deviceName";
	String DEVICE_STATUS = "deviceStatus";

	public static final String TXTRECORD_PROP_AVAILABLE = "available";
	String WifiP2pDnsSdServiceName = "_pba_name";
	String WifiP2pDnsSdServiceType = "_presence._tcp";

	// DeviceDetailFragment mDeviceDetailFragment;
	private void startRegistration(String deviceName, String status) {
		// information about your service.
		Map record = new HashMap();
		record.put(DEVICE_NAME, deviceName);
		record.put(DEVICE_STATUS, status);

		// Serviceinformation. Pass it an instance name, service type
		// _protocol._transportlayer , and the map containing
		// information other devices will want once they connect to this one.
		WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo
				.newInstance(WifiP2pDnsSdServiceName, WifiP2pDnsSdServiceType,
						record);

		// Add thelocal service, sending the service info, network channel,
		// andlistener that will be used to indicate success or failure of
		// therequest.
		mWifiP2pManager.addLocalService(mChannel, serviceInfo,
				new ActionListener() {
					@Override
					public void onSuccess() {
						// Command successful! Code isn't necessarily needed
						// here,
						// Unless you want to update the UI or add logging
						// statements.
					}

					@Override
					public void onFailure(int arg0) {
						// Command failed. Check for P2P_UNSUPPORTED, ERROR, or
						// BUSY
					}
				});

		discoverService();
	}

	private void discoverService() {
		DnsSdTxtRecordListener txtListener = new DnsSdTxtRecordListener() {

			@Override
			public void onDnsSdTxtRecordAvailable(String domain,
					Map<String, String> record, WifiP2pDevice device) {
				// TODO Auto-generated method stub
				Log.d(this, "DnsSdTxtRecord available -" + record.toString());
			}
		};
		DnsSdServiceResponseListener servListener = new DnsSdServiceResponseListener() {

			@Override
			public void onDnsSdServiceAvailable(String instanceName,
					String registrationType, WifiP2pDevice device) {
				// TODO Auto-generated method stub
				Log.d(this, "onBonjourServiceAvailable " + instanceName);
				if (WifiP2pDnsSdServiceName.equals(instanceName)) {

					WifiP2pConfig config = new WifiP2pConfig();
					config.deviceAddress = device.deviceAddress;
					config.wps.setup = WpsInfo.PBC;

					if (serviceRequest != null)
						mWifiP2pManager.removeServiceRequest(mChannel,
								serviceRequest, new ActionListener() {

									@Override
									public void onSuccess() {
										Log.v(this, "停止搜索服务成功...");
									}

									@Override
									public void onFailure(int arg0) {
										Log.v(this, "停止搜索服务失败,错误码:" + arg0);
									}
								});

					mWifiP2pManager.connect(mChannel, config,
							mDeviceListFragment);
				}
			}
		};

		mWifiP2pManager.setDnsSdResponseListeners(mChannel, servListener,
				txtListener);

		serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
		mWifiP2pManager.addServiceRequest(mChannel, serviceRequest,
				new ActionListener() {
					@Override
					public void onSuccess() {
						// Success!
					}

					@Override
					public void onFailure(int code) {
						// Command failed. Check forP2P_UNSUPPORTED, ERROR, or
						// BUSY
					}
				});
		mWifiP2pManager.discoverServices(mChannel, new ActionListener() {
			@Override
			public void onSuccess() {
				// Success!
			}

			@Override
			public void onFailure(int code) {
			}
		});
	}

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

		initIntentFilter();

		mWifiP2pManager = (WifiP2pManager) this
				.getSystemService(Service.WIFI_P2P_SERVICE);

		Log.v(this, "initialize channel...");// WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
		mChannel = mWifiP2pManager.initialize(this, this.getMainLooper(),
				mDeviceListFragment);// 1.INITIALIZE

		this.registerReceiver(mWifiP2pMsgReceiver, mIntentFilter);
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



	public class WifiP2pMsgReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			Log.v(this, "onReceive " + intent.getAction());

			String action = intent.getAction();
			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				// 确定Wi-Fi Direct模式是否已经启用
				int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,
						-1);
				if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
					Log.v(this, "WifiP2p enabled ...");

				} else {
					Log.v(this, "WifiP2p disenabled ... ");

				}

			} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION
					.equals(action)) {
				// 对等点列表已经改变
				// Log.v(this, "request peers...");
				// mWifiP2pManager.requestPeers(mChannel,
				// mDeviceListFragment);// 3.REQUEST
				// PEERS
				// set Invited status,waiting to accepted
				mDeviceListFragment
						.setLocalStatus(WifiP2pDevice.AVAILABLE + "");

			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
					.equals(action)) {
				// 表示此处已经p2p连接上了

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

				WifiP2pDevice device = (WifiP2pDevice) intent
						.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
				Log.v(this, "Getting local device info ..." + device.deviceName
						+ ":" + device.status);
				mDeviceListFragment.setLocalNameStatus(device.deviceName,
						device.status + "");

				startRegistration(device.deviceName, device.status + "");
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
