package kg.gtss.note;

import kg.gtss.alarm.AddReadingAlarm;
import kg.gtss.personalbooksassitant.R;
import kg.gtss.personalbooksassitant.TabSubView;
import kg.gtss.utils.Common;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ReadingNotesActivity extends ListActivity implements
		LoaderCallbacks<Cursor> {
	ListView mListView;
	int LOADER_ID = Common.LOADER_ID_ReadingNote;
	Uri URI = ReadingNoteProvider.CONTENT_URI;
	String[] PROJECTION = new String[] {
			ReadingNoteSQLiteOpenHelper.Columns._ID,
			ReadingNoteSQLiteOpenHelper.Columns.CONTENT,
			ReadingNoteSQLiteOpenHelper.Columns.TIME };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setTitle("阅读笔记");
		this.setContentView(R.layout.reading_note);

		mListView = this.getListView();

		this.getLoaderManager().initLoader(LOADER_ID, null, this);

		TabSubView add = (TabSubView) this.findViewById(R.id.add_note_btn);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ReadingNotesActivity.this,
						NewNoteActivity.class);
				ReadingNotesActivity.this.startActivity(intent);
			}
		});
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
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(this, URI, PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		// TODO Auto-generated method stub
		mListView.setAdapter(new NoteCursorAdapter(this, c));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
