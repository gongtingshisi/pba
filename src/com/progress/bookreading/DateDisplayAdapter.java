package com.progress.bookreading;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Unused! 用于生成时间的适配器.响应触摸手势
 * */
public class DateDisplayAdapter extends BaseAdapter implements
		OnGestureListener, OnTouchListener {
	protected static int DEFAULT_WEEKS = 4;
	Context mContext;
	GestureDetector mGestureDetector;

	int mIndex = -1;

	public DateDisplayAdapter(Context c) {
		mContext = c;
		mGestureDetector = new GestureDetector(c, this);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return DEFAULT_WEEKS;
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(int pos, View v, ViewGroup vg) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		if (null == v) {
			holder = new ViewHolder();
			v = LayoutInflater.from(mContext).inflate(
					R.layout.reading_progress_single, null);
			holder.trend = (ReadingProgressGridView) v
					.findViewById(R.id.readingProgressGridView);
			// set index BEFORE draw different layout
			holder.trend.setMonthIndex(pos);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		return v;
	}

	class ViewHolder {
		ReadingProgressGridView trend;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent ev) {
		// TODO Auto-generated method stub
		return mGestureDetector.onTouchEvent(ev);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		Log.v(this, "onfling");
		return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setIndex(int index) {
		mIndex = index;
	}
}
