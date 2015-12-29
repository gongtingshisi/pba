package kg.gtss.personalbooksassitant;

import java.util.Calendar;

import kg.gtss.utils.Log;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * unused!
 * */
public class TimeLineAdapter extends BaseAdapter {

	Context mContext;

	public TimeLineAdapter(Context con) {
		mContext = con;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 20;
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
	public View getView(int pos, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int position = pos;
		ViewHolder holder = null;
		// ListView will reuse 11 items
		if (null == view) {
			holder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.single_time_node_view_layout, null);
			holder.name = (TextView) view
					.findViewById(R.id.single_time_node_view_layout_name);
			holder.favorite = (ImageView) view.findViewById(R.id.favorite);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		holder.name.setText("" + pos);// or the 12st one
										// will display
										// 0 again
		holder.btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				log("click " + position);

			}
		});
		holder.favorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((ImageView) v).setImageResource(R.drawable.favorite);
			}
		});
		return view;
	}

	class ViewHolder {
		TextView name;
		ImageView favorite;
		Button btn;
	}

	void log(String msg) {
		Log.v(this, msg);
	}
}
