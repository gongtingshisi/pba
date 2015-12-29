package kg.gtss.personalbooksassitant;

import java.util.ArrayList;

import kg.gtss.utils.Log;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class OnlineResourcesListActivity extends Activity {
	ArrayList<OnLineResource> mOnLineResources = new ArrayList<OnLineResource>();
	ListView mListView;
	OnLineResourcesAdapter mOnLineResourcesAdapter;

	void loadOnLineResource() {
		Log.v(this, "loadOnLineResource");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setTitle("我的在线");
		this.getActionBar().setDisplayHomeAsUpEnabled(true);

		this.setContentView(R.layout.online_resources_list);
		mListView = (ListView) this.findViewById(R.id.online_resources_list);

		mOnLineResourcesAdapter = new OnLineResourcesAdapter();

		loadOnLineResource();

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

	/**
	 * 在线图书资源列表
	 * */
	class OnLineResourcesAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub

			return mOnLineResources.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int pos, View v, ViewGroup arg2) {
			// TODO Auto-generated method stub

			ViewHolder holder;
			final int position = pos;
			if (null != v) {
				holder = (ViewHolder) v.getTag();
			} else {
				holder = new ViewHolder();
				v = LayoutInflater.from(OnlineResourcesListActivity.this)
						.inflate(R.layout.online_resource_lit_item, null);
				holder.icon = (ImageView) v
						.findViewById(R.id.online_resource_list_item_image);

			}
			holder.icon
					.setImageResource(mOnLineResources.get(position).drawable);
			holder.icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.v(this, "OnlineResourcesListActivity loading " + mOnLineResources.get(position).url);
					Intent i = new Intent();
					i.putExtra("url", mOnLineResources.get(position).url);
					i.setClass(OnlineResourcesListActivity.this,
							WebPageActivity.class);
					OnlineResourcesListActivity.this.startActivity(i);
				}
			});
			return v;
		}
	}

	class OnLineResource {
		String url;
		int drawable;
		String name;

		public OnLineResource(String u, int d, String n) {
			url = u;
			drawable = d;
			name = n;
		}
	}

	class ViewHolder {
		// TextView name;
		ImageView icon;
	}
}
