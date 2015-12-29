package com.progress.bookreading;

import kg.gtss.utils.Log;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;

import android.widget.ImageButton;

/**
 * Unused! 拥有圆圆的圆周的image button
 * */
public class CircleImageButton extends ImageButton {

	public CircleImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int w = getWidth(), h = getHeight();
		float x = this.getX(), y = this.getY();
		// Log.v(this, "w:" + w + ",h:" + h + ",x:" + x + ",y:" + y);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int speSize = MeasureSpec.getSize(heightMeasureSpec);
		int speMode = MeasureSpec.getMode(heightMeasureSpec);
		if (speMode == MeasureSpec.AT_MOST) {
			// Log.v(this, "AT_MOST");
		} else if (speMode == MeasureSpec.EXACTLY) {
			// Log.v(this, "EXACTLY");
		}
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), speSize);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}

}
