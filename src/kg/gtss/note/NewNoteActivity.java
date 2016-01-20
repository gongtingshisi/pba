package kg.gtss.note;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.EditText;
import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;

public class NewNoteActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.new_note);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * A custom EditText that draws lines between each line of text that is
	 * displayed.
	 */
	public static class LinedEditText extends EditText {
		private Rect mRect;
		private Paint mPaint;
		private static final String TAG = "NoteEditText";
		private static final String SCHEME_TEL = "tel:";
		private static final String SCHEME_HTTP = "http:";
		private static final String SCHEME_HTTPS = "https:";
		private static final String SCHEME_EMAIL = "mailto:";

		// Offset from the beginning of the EditText
		private int mOffset;

		// private static final Map<String, Integer> sSchemaActionResMap = new
		// HashMap<String, Integer>();
		// static {
		// sSchemaActionResMap.put(SCHEME_TEL, R.string.tel);
		// sSchemaActionResMap.put(SCHEME_HTTP, R.string.web);
		// sSchemaActionResMap.put(SCHEME_HTTPS, R.string.web);
		// sSchemaActionResMap.put(SCHEME_EMAIL, R.string.email);
		// }
		// we need this constructor for LayoutInflater
		public LinedEditText(Context context, AttributeSet attrs) {
			super(context, attrs);
			setWillNotDraw(false);// 设置view是否更改，如果用自定义的view，重写ondraw()应该将调用此方法设置为false，这样程序会调用自定义的布局。
			setLinkTextColor(0xFF0069FF);// 文字链接的颜色.
			setLinksClickable(false);// 设置链接是否点击连接，即使设置了autoLink。
			mRect = new Rect();
			mPaint = new Paint();
			PathEffect effects = new DashPathEffect(new float[] { 1, 2, 4, 8 },
					1);
			mPaint.setPathEffect(effects);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(1);
			mPaint.setColor(Color.parseColor("#dbdbdb"));

		}

		@Override
		protected void onDraw(Canvas canvas) {
			int count = getLineCount();
			Rect r = mRect;
			Paint paint = mPaint;

			for (int i = 0; i < count; i++) {
				int baseline = getLineBounds(i, r);
				Log.d(this, "LinedEditText onDraw baseline :" + baseline);
				// canvas.drawLine(0, baseline + 5, r.right, baseline + 5,
				// paint);
			}
			super.onDraw(canvas);
		}
	}
}
