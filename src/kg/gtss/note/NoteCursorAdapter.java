package kg.gtss.note;

import android.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class NoteCursorAdapter extends CursorAdapter {

	public NoteCursorAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public View newView(Context context, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		// return LayoutInflater.from(context).inflate(R.layout.note_item,
		// null);
		return null;
	}
}
