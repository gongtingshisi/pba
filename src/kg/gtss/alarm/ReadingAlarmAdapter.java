package kg.gtss.alarm;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.personalbooksassitant.R.id;
import kg.gtss.personalbooksassitant.R.layout;
import kg.gtss.utils.Log;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadingAlarmAdapter extends BaseAdapter {
	Context mContext;

	public ReadingAlarmAdapter(Context c) {
		mContext = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
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
		final int position = pos;
		ViewHolder holder = null;
		if (null == v) {
			v = LayoutInflater.from(mContext).inflate(
					R.layout.add_reading_alarm_item, null);
			holder = new ViewHolder();
			holder.time = (TextView) v.findViewById(R.id.alarm_time);
			holder.days = (TextView) v.findViewById(R.id.alarm_days);
			holder.content = (TextView) v.findViewById(R.id.alarm_content);
			holder.onoff = (ImageView) v.findViewById(R.id.alarm_switch);
			holder.onoff.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

				}
			});
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});

		return v;
	}

	class ViewHolder {
		TextView time;
		TextView days;
		TextView content;
		ImageView onoff;
	}
}
