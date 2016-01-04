package syncdatap2p;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.FileUtils;
import kg.gtss.utils.Log;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
	Context mContext;
	DeviceAdapter mDeviceAdapter;

	int PORT = 8000;

	private ArrayList<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mContext = this.getActivity();
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

	// void connect(String address) {
	// // TRY to connect a peer
	// // Log.v(this, "Try to connecting " + address);
	// WifiP2pConfig config = new WifiP2pConfig();
	// config.deviceAddress = address;
	// config.wps.setup = WpsInfo.PBC;// !
	// ((WifiP2pManager) getActivity().getSystemService(
	// Service.WIFI_P2P_SERVICE)).connect(
	// ((SyncDataP2pActivity) getActivity()).mChannel, config, this);// CONNECT
	// }

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
			status.setText(peers.get(position).status);

			if (null != peers && peers.size() > 0 && pos < peers.size()) {
				// connect(peers.get(pos).deviceAddress);
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
			// new ServerSocketHandler().start();
			new ServerSideSaveDataTask().execute();
		} else if (info.groupFormed && !info.isGroupOwner) {
			// 其他设备都作为客户端。在这种情况下，你会希望创建一个客户端线程来连接群主。
			// new ClientSocketHandler().start();
			new ClientSideTransferDataTask(
					info.groupOwnerAddress.getHostAddress(), PORT).execute();
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
		Log.v(this, "mConnectionActionListener OnFailure " + reason
				+ "  invited");
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
		Log.v(this, "connected sucess ~   invited");
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
				ServerSocket server = new ServerSocket(PORT);

				// accept a client connection
				Socket client = server.accept();
				Log.v(this, "Server says: accept from "
						+ client.getInetAddress().getHostAddress() + ":"
						+ client.getPort());
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
		String addr;
		int port;

		public ClientSideTransferDataTask(String address, int port) {
			this.addr = address;
			this.port = port;
		}

		@Override
		protected Object doInBackground(Object... arg0) {
			// TODO Auto-generated method stub

			int len;
			Socket socket = new Socket();
			byte buf[] = new byte[1024];

			try {
				/**
				 * Create a client socket with the host, port, and timeout
				 * information.
				 */
				socket.bind(null);
				socket.connect((new InetSocketAddress(addr, port)), 5000);

				/**
				 * Create a byte stream from a JPEG file and pipe it to the
				 * output stream of the socket. This data will be retrieved by
				 * the server device.
				 */
				OutputStream outputStream = socket.getOutputStream();

				InputStream inputStream = null;
				inputStream = mContext.getAssets().open("ic_launcher.png");
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
}
