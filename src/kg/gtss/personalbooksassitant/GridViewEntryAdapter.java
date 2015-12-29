package kg.gtss.personalbooksassitant;

import java.util.ArrayList;

import syncdatap2p.SyncDataP2pActivity;

import kg.gtss.alarm.AddReadingAlarm;

import backuprestore.BackupRestoreActivity;

import com.gtss.useraccount.LocalAccountAuthenticatorActivity;
import com.gtss.useraccount.UserAccountActivity;
import com.progress.bookreading.ReadingProgressActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * adapter to activity
 * */
public class GridViewEntryAdapter extends BaseAdapter {
	Context mContext;

	int[] drawables = new int[] { R.drawable.composer_button,
			R.drawable.composer_camera, R.drawable.composer_music,
			R.drawable.composer_music, R.drawable.composer_place,
			R.drawable.composer_sleep, R.drawable.composer_thought,
			R.drawable.composer_with, R.drawable.favorite,
			R.drawable.favorite_not, R.drawable.composer_music,
			R.drawable.composer_place };
	int mSize = drawables.length;
	ArrayList<GridItem> mGridItems = new ArrayList<GridItem>();
	Class[] classes = new Class[] { AddReadingAlarm.class,
			ReadingProgressActivity.class, FavoriteBooksActivity.class,
			AllBooksActivity.class, ReadingNotesActivity.class,
			BookcaseActivity.class, CommunityActivity.class,
			LastReadBookActivity.class,
			LocalAccountAuthenticatorActivity.class,
			BackupRestoreActivity.class, SyncDataP2pActivity.class,
			OnlineBookShopActivity.class };

	/**
	 * a container
	 * */
	class GridItem {
		Class clazz;
		Drawable image;
		String text;

		public GridItem(Class c, Drawable d, String s) {
			clazz = c;
			image = d;
			text = s;
		}
	}

	public GridViewEntryAdapter(Context c) {
		mContext = c;
		String[] strings = c.getResources().getStringArray(
				R.array.personal_items);
		mSize = strings.length > drawables.length ? drawables.length
				: strings.length;

		mGridItems.clear();
		for (int i = 0; i < drawables.length; i++) {
			mGridItems.add(new GridItem(classes[i], c.getResources()
					.getDrawable(drawables[i]), strings[i]));
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mSize;
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
	public View getView(int pos, View v, ViewGroup root) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;

		if (null == v) {

			holder = new ViewHolder();
			v = LayoutInflater.from(mContext).inflate(
					R.layout.gridview_entry_item, null);
			holder.image = (ImageView) v
					.findViewById(R.id.gridview_entry_item_image);
			holder.text = (TextView) v
					.findViewById(R.id.gridview_entry_item_text);

			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		if (pos < mGridItems.size()) {
			holder.image.setImageDrawable(mGridItems.get(pos).image);
			holder.text.setText(mGridItems.get(pos).text);
		}

		final int index = pos;
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(mContext, mGridItems.get(index).clazz);
				mContext.startActivity(i);
			}
		});
		return v;
	}

	class ViewHolder {
		ImageView image;
		TextView text;
	}

}
