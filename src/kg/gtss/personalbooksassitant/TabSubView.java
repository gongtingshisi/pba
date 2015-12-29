package kg.gtss.personalbooksassitant;

import kg.gtss.image.ImageUtils;
import kg.gtss.utils.Log;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class TabSubView extends View {

	Context mContext;
	String mTitle;
	Drawable mDrawable;
	Paint mPaint;
	int mPicHeight = 80, mPicWidth = 80, mGap = 20;

	public TabSubView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;

		TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.reading_alarm_button, 0, 0);
		mDrawable = array
				.getDrawable(R.styleable.reading_alarm_button_reading_alarm_button_image);
		mTitle = array
				.getString(R.styleable.reading_alarm_button_reading_alarm_button_text);
		try {
		} finally {
			array.recycle();
		}
		// get mPicHeight, mPicWidth , mGap ;
		init();
	}

	public TabSubView(Context context, String title, Drawable drawable) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mTitle = title;
		mDrawable = drawable;

		init();
	}

	void setText(String text) {
		mTitle = text;
	}

	void init() {
		mPaint = new Paint();
		mPaint.setTextSize(30);
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Bitmap pic = Bitmap.createScaledBitmap(
				ImageUtils.drawable2Bitmap(mDrawable), mPicWidth, mPicHeight,
				true);

		float x = (getWidth() - pic.getWidth()) / 2;
		float y = (getHeight() - pic.getHeight() - mPaint.getTextSize()) / 2;

		canvas.drawBitmap(pic, x, y, mPaint);
		canvas.drawText(mTitle, (getWidth() - mPaint.measureText(mTitle)) / 2,
				y + pic.getHeight() + mGap, mPaint);
	}
}
