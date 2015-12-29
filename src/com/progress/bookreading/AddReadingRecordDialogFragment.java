package com.progress.bookreading;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.zxing.common.StringUtils;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * 创建一个用于设置阅读数目的seekbar dialogfragment
 * */
public class AddReadingRecordDialogFragment extends DialogFragment {

	// ReadingRecordDatabase mReadingRecordDatabase;
	EditText mBookName, mBookAuthor, mPage;
	CalendarView mCalendarView;

	String mTitle, mAuthor;
	long mDate, mFrom, mTo;
	int mRead;
	Context mContext;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.v(this, "onattach ");
		mContext = activity;

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setPositiveButton("OK", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

						mTitle = mBookName.getText().toString();
						mAuthor = mBookAuthor.getText().toString();
						if (mTitle == null || mTitle.length() == 0
								|| mAuthor == null || mAuthor.length() == 0) {
							Toast.makeText(
									getActivity(),
									getActivity().getResources().getString(
											R.string.input_error),
									Toast.LENGTH_SHORT).show();
							return;
						}
						try {
							mRead = Integer.valueOf(mPage.getText().toString())
									.intValue();
						} catch (NumberFormatException e) {
							Toast.makeText(
									getActivity(),
									getActivity().getResources().getString(
											R.string.input_error),
									Toast.LENGTH_SHORT).show();
							return;
						}

						// 时间上时分秒归为零
						long time = mCalendarView.getDate();
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(time);

						Calendar c1 = Calendar.getInstance();
						c1.set(Calendar.YEAR, c.get(Calendar.YEAR));
						c1.set(Calendar.MONTH, c.get(Calendar.MONTH));
						c1.set(Calendar.DAY_OF_MONTH,
								c.get(Calendar.DAY_OF_MONTH));
						c1.set(Calendar.HOUR_OF_DAY, 0);
						c1.set(Calendar.MINUTE, 0);
						c1.set(Calendar.SECOND, 0);

						long mFrom = c1.getTimeInMillis();
						long mTo = c1.getTimeInMillis() + 24 * 60 * 60 * 1000;

						mDate = time;

						if (duplicateChange(mTitle, mFrom, mTo)) {
							showDialog();
						} else {
							insertReadRecord(mTitle, mAuthor, mDate, mRead);
						}
					}
				}).setNegativeButton("Cancel", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});

		return builder.setView(setupView()).create();
	}

	void showDialog() {
		new AlertDialog.Builder(getActivity())
				.setPositiveButton("OK", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						updateReadRecord(mTitle, mAuthor, mDate, mRead, mFrom,
								mTo);
					}
				}).setNegativeButton("NO", new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				}).setTitle(R.string.warning)
				.setMessage(R.string.duplicate_record).show();
	}

	boolean duplicateChange(String title, long from, long to) {
		String where = ReadingRecord.TYPE_title + " = ? and "
				+ ReadingRecord.TYPE_date + " >= ? and "
				+ ReadingRecord.TYPE_date + " <= ? ";
		String[] selectionArgs = new String[] { "" + title, "" + from, "" + to };

		Cursor cursor = this
				.getActivity()
				.getContentResolver()
				.query(ReadingRecordProvider.CONTENT_URI,
						new String[] { ReadingRecord.TYPE_title }, where,
						selectionArgs, null);
		if (cursor != null && cursor.getCount() > 0) {
			return true;
		}
		cursor.close();
		return false;
	}

	void insertReadRecord(String title, String author, long date, int read) {

		ContentValues value = new ContentValues();
		value.put(ReadingRecord.TYPE_author, author);
		value.put(ReadingRecord.TYPE_title, title);
		value.put(ReadingRecord.TYPE_date, date);
		value.put(ReadingRecord.TYPE_color, 0);
		value.put(ReadingRecord.TYPE_read, read);
		value.put(ReadingRecord.TYPE_base, 0);// fetch from db,based on last
												// once
		value.put(ReadingRecord.TYPE_page, 0);// from dou ban
		mContext.getContentResolver().insert(ReadingRecordProvider.CONTENT_URI,
				value);
		// ReadingRecord r = new ReadingRecord();
		// r.title = name;
		// r.date = date;
		// r.author = author;
		// r.read = read;
		// mReadingRecordDatabase.insert(r);
		// mReadingRecordDatabase.dump();
	}

	void updateReadRecord(String title, String author, long date, int read,
			long from, long to) {
		String where = ReadingRecord.TYPE_title + " = ? and "
				+ ReadingRecord.TYPE_date + " >= ? and "
				+ ReadingRecord.TYPE_date + " <= ? ";
		String[] selectionArgs = new String[] { "" + title, "" + from, "" + to };

		ContentValues value = new ContentValues();
		value.put(ReadingRecord.TYPE_author, author);
		value.put(ReadingRecord.TYPE_title, title);
		value.put(ReadingRecord.TYPE_date, date);
		value.put(ReadingRecord.TYPE_color, 0);
		value.put(ReadingRecord.TYPE_read, read);
		value.put(ReadingRecord.TYPE_base, 0);// fetch from db,based on last
												// once
		value.put(ReadingRecord.TYPE_page, 0);// from dou ban
		mContext.getContentResolver().update(ReadingRecordProvider.CONTENT_URI,
				value, where, selectionArgs);
		// ReadingRecord r = new ReadingRecord();
		// r.title = name;
		// r.date = date;
		// r.author = author;
		// r.read = read;
		// mReadingRecordDatabase.insert(r);
		// mReadingRecordDatabase.dump();
	}

	View setupView() {
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.add_reading_record_layout, null);
		mBookName = (EditText) view.findViewById(R.id.add_title_name);
		mBookAuthor = (EditText) view.findViewById(R.id.add_author_name);
		mCalendarView = (CalendarView) view.findViewById(R.id.add_calendar);
		mPage = (EditText) view.findViewById(R.id.add_read_page);

		return view;
	}
}
