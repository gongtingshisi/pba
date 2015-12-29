package syncdatap2p;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.Service;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DeviceListFragment extends ListFragment implements
		PeerListListener, ConnectionInfoListener, ChannelListener,
		ActionListener {
	TextView mLocalName, mLocalState;

	DeviceAdapter mDeviceAdapter;

	private ArrayList<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		this.setListAdapter(mDeviceAdapter = new DeviceAdapter(this
				.getActivity(), R.layout.p2p_list_item, peers));

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.p2p_list, null);
		mLocalName = (TextView) v.findViewById(R.id.my_name);
		mLocalState = (TextView) v.findViewById(R.id.my_status);

		return v;
	}

	public void setLocalNameStatus(String name, String status) {
		mLocalName.setText(name);
		mLocalState.setText(status);
	}

	public void setLocalStatus(String status) {
		mLocalState.setText(status);
	}

	void connect(String address) {
		// TRY to connect a peer
		// Log.v(this, "Try to connecting " + address);
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = address;
		config.wps.setup = WpsInfo.PBC;// !
		((WifiP2pManager) getActivity().getSystemService(
				Service.WIFI_P2P_SERVICE)).connect(
				((SyncDataP2pActivity) getActivity()).mChannel, config, this);// CONNECT
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList devices) {
		// TODO Auto-generated method stub
		if (null == devices)
			return;
		// Log.v(this, "onPeersAvailable Size :" +
		// devices.getDeviceList().size());
		peers.clear();//
		peers.addAll(devices.getDeviceList());

		mDeviceAdapter.notifyDataSetChanged();
	}

	class DeviceAdapter extends ArrayAdapter<WifiP2pDevice> {
		int resource;

		public DeviceAdapter(Context context, int r, List<WifiP2pDevice> objects) {
			super(context, r, objects);
			// TODO Auto-generated constructor stub
			resource = r;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final int pos = position;
			if (null == convertView) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						resource, null);
			}
			TextView name = (TextView) convertView
					.findViewById(R.id.p2p_peer_name);
			TextView status = (TextView) convertView
					.findViewById(R.id.p2p_peer_status);
			name.setText(peers.get(position).deviceName);
			status.setText(SyncDataP2pActivity.getDeviceStatus(peers
					.get(position).status));

			if (null != peers && peers.size() > 0 && pos < peers.size()) {
				connect(peers.get(pos).deviceAddress);
			}

			return convertView;
		}
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		Log.v(this, "onConnectionInfoAvailable... " + info.toString());
		// InetAddress在WifiP2pInfo结构体中。

		InetAddress groupOwnerAddress = info.groupOwnerAddress;

		// 组群协商后，就可以确定群主。
		if (info.groupFormed && info.isGroupOwner) {
			// 针对群主做某些任务。
			// 一种常用的做法是，创建一个服务器线程并接收连接请求。
		} else if (info.groupFormed) {
			// 其他设备都作为客户端。在这种情况下，你会希望创建一个客户端线程来连接群主。
		}

	}

	@Override
	public void onChannelDisconnected() {
		// TODO Auto-generated method stub
		Log.v(this, "onChannelDisconnected ! Care !");
	}

	@Override
	public void onFailure(int reason) {
		// TODO Auto-generated method stub
		// Log.v(this, "mConnectionActionListener OnFailure " + reason);
		switch (reason) {
		case WifiP2pManager.P2P_UNSUPPORTED:
			break;
		case WifiP2pManager.ERROR:
			break;
		case WifiP2pManager.BUSY:
			break;
		}
	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		// Log.v(this, "connected sucess ~");
	}

}
