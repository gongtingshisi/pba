package com.progress.bookreading;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;

/**
 * 不建议转屏. the view of reading.View可以相应手势动作,为了便于移植,而不是activity
 * progress.http://www.cnblogs.com/purediy/p/3799534.html. 1.自定义控件遵循原则:
 * 暴露您view中所有影响可见外观的属性或者行为。 通过XML添加和设置样式 通过元素的属性来控制其外观和行为，支持和重要事件交流的事件监听器
 * .1.View本身大小多少，这由onMeasure()决定；2.View在ViewGroup中的位置如何
 * ，这由onLayout()决定；3.绘制View，onDraw()定义了如何绘制这个View。.
 * http://blog.csdn.net/pi9nc/article/details/18764863.
 * 日历的周视图绘制原理,要绘制的日期参数是adapter使用set方法重新设置后,通知刷新绘制的....
 * 分为按日期查看；按单本书查看.....支持多本书同时阅读,以及此起彼伏的情况
 * */
public class CopyOfReadingProgressGridView extends View /* implements OnTouchListener */{

	/**
	 * 按每个单本书,但是是累计图视图显示.比如一本书已经读了多少页.
	 * 这个也可联合BasedPerBookViewMode或BasedPerDateViewMode使用.比如 (
	 * BasedPerBookViewMode & BasedAccumulateViewMode == )
	 * */
	int BasedAccumulateViewMode = 1 << 0;
	/**
	 * 按单本书查看:一本书的阅读历程,可能某天阅读为0,但是却读了其他书.这个视图只提供一条折线趋势图和信息
	 * */
	int BasedPerBookViewMode = 1 << 1;
	/**
	 * 按日期查看多本书的阅读情况:每天读了多少页,可能是多本不同的书之和.这种模式下,绘制出每本书每天的阅读情况.比如今天读了2本书,明天读了4本书,
	 * 后天读了0本书.
	 * 不同书使用不同颜色标注折线信息,最好还可以显示出每本书的阅读进度(当前读了这本书的百分比),如果一本书读完了,那么这本书的这种颜色折线旧终止了.
	 * 
	 * */
	int BasedPerDateViewMode = 1 << 2;

	// 1.(X & BasedAccumulateViewMode==0)and(X & BasedPerBookViewMode!=0), means
	// X=BasedPerBookViewMode;
	// 2.(X&BasedAccumulateViewMode!=0)and(X&BasedPerDateViewMode!=0),means
	// X= BasedPerDateViewMode | BasedAccumulateViewMode;
	/**
	 * 当前所处视图模式
	 * */
	int CurrentViewMod = BasedPerDateViewMode;

	GestureDetector mGestureDetector;
	Paint mGridPaint;// 绘制网格的画笔
	Paint mRulerPaint;// 绘制刻度的 画笔
	Paint mTrendPaint;// 绘制折线的画笔
	Paint mPointPaint;// 绘制折点的画笔
	Paint mPointDataPaint;// 绘制折点上方阅读页数的画笔
	Rect mRect;
	Context mContext;

	/**
	 * 使用一条单链表存储来自cursor的数据
	 * */
	ArrayList<ReadingRecord> mData = new ArrayList<ReadingRecord>();
	/**
	 * 按照书名整理之后的二维可变大小数组
	 * */
	Vector<Vector<ReadingRecord>> m2DimensionVector = new Vector<Vector<ReadingRecord>>();

	// 折点圆点半径大小
	float mReadingPointRadius;
	// 折点圆点颜色
	int mReadingPointColor;
	// 折点提示数字,表示读书页数
	int mReadingNumberColor;
	// 两侧刻度文本颜色
	int mRulerColor;
	// 折线宽度
	float mTrendWidth;
	// 折线颜色
	int mTrendColor;
	// 背景色
	int mReadingProgressGridColor;
	// 行数,默认50行
	int mLineNum;
	// 列,以当月天数为准,动态获取
	// int VerticalNum;
	// 字体大小
	float mTextSize;
	float mSmallTextSize;
	final static float Margin = 5;// 用来预留padding的,主要是用来分离字符和图表直线的
	// gap.考虑到一本书最多2000页.也就是说四个字符长度.mPaint.measureText("1000")/2
	// 大不了边界空白为0,还得考虑字体大小..这就决定了2*gap宽度已经可以包含最大字符串长度
	float mGap;

	// 看书页数最小单位,放大系数,可以通过手势动作,比如双指拉伸,双击来放大缩小,
	int mScaleIndex = 0;
	int[] mZoomScale;
	int mScale;

	// 行高度,列宽度
	float HeightPerLine, VerticalW;

	// 一年有12个月
	final static int MONTH_PER_YEAR = 12;
	int mMonth = -1;// index to CURRENT month dragged
	int mYear = -1;// index to CURRENT year dragged
	int mMaxDayOfCurrentMonth = -1;// index to CURRENT month dragged

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	// 第一个按下的手指的点
	private PointF startPoint = new PointF();
	// 两个按下的手指的触摸点的中点
	private PointF midPoint = new PointF();
	// 初始的两个手指按下的触摸点的距离
	private float oriDis = 1f;

	// for test
	// int TEST[] = new int[] { 233, 50, 240, 500, 556, 2, 199 };

	public CopyOfReadingProgressGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Log.v(this, "new ReadingProgressGridView ");
		mContext = context;

		initResource(context, attrs);
		initTime();
		initPainting();
		// initGestureEvent(context);

	}

	void setViewDate(int y, int m) {
		mYear = y;
		mMonth = m;
		// refreshUi();
	}

	void setScale() {
		mScaleIndex++;
		mScaleIndex %= mZoomScale.length;
		mScale = mZoomScale[mScaleIndex];
		Log.v(this, "Zooming scale " + mScale + " times!");
		refreshUi();
	}

	int getYear() {
		return mYear;
	}

	int getMonth() {
		return mMonth;
	}

	/*
	 * void initGestureEvent(Context context) { mGestureDetector = new
	 * GestureDetector(context, new
	 * android.view.GestureDetector.SimpleOnGestureListener() {
	 * 
	 * @Override public boolean onDoubleTap(MotionEvent e) { // TODO
	 * Auto-generated method stub mScaleIndex++; mScaleIndex %=
	 * mZoomScale.length; mScale = mZoomScale[mScaleIndex]; Log.v(this,
	 * "Zooming scale " + mScale + " times!"); refreshUi(); return
	 * super.onDoubleTap(e); }
	 * 
	 * @Override public boolean onDoubleTapEvent(MotionEvent e) { // TODO
	 * Auto-generated method stub return super.onDoubleTapEvent(e); }
	 * 
	 * @Override public boolean onDown(MotionEvent e) { // TODO Auto-generated
	 * method stub return true;// !! }
	 * 
	 * @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float
	 * vx, float vy) { // TODO Auto-generated method stub
	 * 
	 * Log.v(this, "onFling (" + e1.getX() + "," + e1.getY() + "),(" + e2.getX()
	 * + "," + e2.getY() + ")		vx:" + vx + ",vy:" + vy);
	 * 
	 * if (moveLeft(vx)) { // increase month and year increaseMonth();
	 * refreshUi(); } else if (moveRight(vx)) { // decrease month and year
	 * decreaseMonth(); refreshUi(); } return true; }
	 * 
	 * @Override public void onLongPress(MotionEvent e) { // TODO Auto-generated
	 * method stub super.onLongPress(e); }
	 * 
	 * @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float
	 * distanceX, float distanceY) { // TODO Auto-generated method stub return
	 * super.onScroll(e1, e2, distanceX, distanceY); }
	 * 
	 * @Override public void onShowPress(MotionEvent e) { // TODO Auto-generated
	 * method stub super.onShowPress(e); }
	 * 
	 * @Override public boolean onSingleTapConfirmed(MotionEvent e) { // TODO
	 * Auto-generated method stub return super.onSingleTapConfirmed(e); }
	 * 
	 * @Override public boolean onSingleTapUp(MotionEvent e) { // TODO
	 * Auto-generated method stub return super.onSingleTapUp(e); }
	 * 
	 * }); // this.setOnTouchListener(this); }
	 */
	/**
	 * 根据当前年月递增得到月份
	 * */
	/*
	 * void increaseMonth() { if (++mMonth > MONTH_PER_YEAR - 1) { mMonth %=
	 * MONTH_PER_YEAR;// =0 mYear++; } Log.v(this, "increase to " + mYear + "/"
	 * + (mMonth + 1) + ",max days:" + getDaysOfCurrentMonth()); }
	 */

	/**
	 * 根据当前年月递减得到月份
	 * */
	/*
	 * void decreaseMonth() { if (--mMonth < 0) { mMonth = MONTH_PER_YEAR - 1;
	 * mYear--; } Log.v(this, "decrease to " + mYear + "/" + (mMonth + 1) +
	 * ",max days:" + getDaysOfCurrentMonth()); }
	 */
	void initResource(Context context, AttributeSet attrs) {
		// 获取自定义视图属性

		TypedArray array = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.reading_trend_chart, 0, 0);
		try {
			// 读取自定义属性值
			mGap = array.getDimension(R.styleable.reading_trend_chart_gap,
					context.getResources().getDimension(R.dimen.gap));

			mReadingPointRadius = array.getDimension(
					R.styleable.reading_trend_chart_readingPointRadius,
					context.getResources().getDimension(
							R.dimen.radius_reading_point));

			mReadingPointColor = array.getColor(
					R.styleable.reading_trend_chart_readingPointColor, context
							.getResources().getColor(R.color.yellow));

			mTrendColor = array.getColor(
					R.styleable.reading_trend_chart_trendColor, context
							.getResources().getColor(R.color.blue));

			mTextSize = array.getDimension(
					R.styleable.reading_trend_chart_textsize, context
							.getResources().getDimension(R.dimen.textsize));

			mSmallTextSize = array
					.getDimension(
							R.styleable.reading_trend_chart_smalltextsize,
							context.getResources().getDimension(
									R.dimen.smalltextsize));

			mZoomScale = context.getResources().getIntArray(R.array.scale);
			// initiate scale
			mScale = mZoomScale[mScaleIndex];

			mLineNum = array.getInt(R.styleable.reading_trend_chart_linenum,
					context.getResources().getInteger(R.integer.linenum));
			mRulerColor = array.getColor(
					R.styleable.reading_trend_chart_rulerColor, context
							.getResources().getColor(R.color.darkgrey));
			mReadingProgressGridColor = array.getColor(
					R.styleable.reading_trend_chart_readingProgressBgColor,
					context.getResources().getColor(R.color.antiquewhite));
			mReadingNumberColor = array.getColor(
					R.styleable.reading_trend_chart_readingNumberColor, context
							.getResources().getColor(R.color.aquamarine));
			mTrendWidth = array.getDimension(
					R.styleable.reading_trend_chart_trendWidth, context
							.getResources().getDimension(R.dimen.trendWidth));
			// getHeight()在此处,视图还没有衡量出大小.需要在onmesure之后
			Log.v(this, "mGap:" + mGap + ",mReadingPointRadius:"
					+ mReadingPointRadius + ",mTextSize: " + mTextSize
					+ ",mSmallTextSize:" + mSmallTextSize + ",mScale:" + mScale
					+ ", getHorizationalCount():" + mLineNum + ",mTrendWidth:"
					+ mTrendWidth);
		} finally {
			// TypedArray是共享资源.必须释放回收
			array.recycle();
		}
	}

	void initTime() {
		// initiate month and year
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mMaxDayOfCurrentMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		Log.v(this, "Current Date:" + mYear + "/" + (mMonth + 1) + ",max days:"
				+ mMaxDayOfCurrentMonth);
	}

	void initPainting() {
		mGridPaint = new Paint();// 绘制网格的画笔
		mGridPaint.setColor(mReadingProgressGridColor);

		mRulerPaint = new Paint();// 绘制刻度的 画笔
		mRulerPaint.setAntiAlias(true);
		// 考虑到一本书最多2000页.也就是说四个字符长度.这就决定了2*gap宽度已经可以包含最大字符串长度
		mRulerPaint.setTextSize(mTextSize);
		mRulerPaint.setColor(mRulerColor);

		mTrendPaint = new Paint();// 绘制折线的画笔
		mTrendPaint.setAntiAlias(true);
		mTrendPaint.setColor(mTrendColor);
		mTrendPaint.setStrokeWidth(mTrendWidth);

		mPointPaint = new Paint();// 绘制折点和上方阅读页数的画笔
		mPointPaint.setAntiAlias(true);
		mPointPaint.setColor(mReadingPointColor);
		mPointPaint.setStrokeWidth(mReadingPointRadius);

		mPointDataPaint = new Paint();
		mPointDataPaint.setAntiAlias(true);
		mPointDataPaint.setColor(mReadingNumberColor);
		mPointDataPaint.setTextSize(mSmallTextSize);

		mRect = new Rect();

		mGap = mRulerPaint.measureText("1000") / 2;
	}

	/**
	 * requestLayout：当view确定自身已经不再适合现有的区域时，该view本身调用这个方法要求parent
	 * view重新调用他的onMeasure onLayout来对重新设置自己位置。
	 * 特别的当view的layoutparameter发生改变，并且它的值还没能应用到view上，这时候适合调用这个方法。
	 * invalidate：View本身调用迫使view重画。
	 * */
	public void refreshUi() {
		Log.v(this, "refreshUi...size:" + mData.size());
		// 通知刷新界面
		this.invalidate();
		// 请求重新测量布局
		// this.requestLayout();
	}

	/**
	 * 获取某一天同时阅读多本书的情况
	 * */
	ArrayList<ReadingRecord> getMultiBooksOneDay(int dayIndex) {
		ArrayList<ReadingRecord> read = new ArrayList<ReadingRecord>();
		if (null == mData || mData.size() == 0) {
			;
		} else {
			int i = 0;
			for (ReadingRecord r : mData) {
				if ((TimeUtils.getDateDayFromLongTime(mData.get(i).date) - 1) == dayIndex) {
					read.add(mData.get(i));
				}
				i++;
			}
		}
		return read;
	}

	/**
	 * 根据书名获取某天对应的y坐标
	 * */
	float getYAxis(int dayIndex, String bookName) {
		ArrayList<ReadingRecord> read = new ArrayList<ReadingRecord>();

		float yAxis = getHeight() - mGap - HeightPerLine;
		if (null == mData || mData.size() == 0) {
			;
		} else {
			int i = 0;
			for (ReadingRecord r : mData) {
				if ((TimeUtils.getDateDayFromLongTime(mData.get(i).date) - 1) == dayIndex) {
					read.add(mData.get(i));
				}
				i++;
			}
		}
		// 找出这本书有没有在今日读书列表之中
		int j = 0, flag = -1;
		for (ReadingRecord r : read) {
			if (mData.get(j).title.equalsIgnoreCase(bookName)) {
				flag = j;
				break;
			}
			j++;
		}
		if (read.size() > 0 && flag >= 0)
			return getHeight() - mGap
					- ((float) HeightPerLine * read.get(j).read / mScale)
					- HeightPerLine;
		else
			return yAxis;

	}

	/**
	 * 获取某天读的书中第i本的书名
	 * */
	String getBookName(int dayIndex, int indexOfBooks) {
		ArrayList<ReadingRecord> read = new ArrayList<ReadingRecord>();

		String name = "";
		if (null == mData || mData.size() == 0) {
			;
		} else {
			int i = 0;
			for (ReadingRecord r : mData) {
				if ((TimeUtils.getDateDayFromLongTime(mData.get(i).date) - 1) == dayIndex) {
					read.add(mData.get(i));
				}
				i++;
			}
		}
		if (read.size() > 0 && indexOfBooks < read.size())
			return read.get(indexOfBooks).title;
		else
			return name;

	}

	/**
	 * 获取对应书页的y坐标,因为总坐标是从0开始,横坐标是从1开始,所以总坐标需要减去一个格子高度
	 * */
	float getYAxis(int dayIndex, int indexOfBooks) {
		ArrayList<ReadingRecord> read = new ArrayList<ReadingRecord>();

		float yAxis = getHeight() - mGap - HeightPerLine;
		if (null == mData || mData.size() == 0) {
			;
		} else {
			int i = 0;
			for (ReadingRecord r : mData) {
				if ((TimeUtils.getDateDayFromLongTime(mData.get(i).date) - 1) == dayIndex) {
					read.add(mData.get(i));
				}
				i++;
			}
		}
		if (read.size() > 0 && indexOfBooks < read.size())
			return getHeight()
					- mGap
					- ((float) HeightPerLine * read.get(indexOfBooks).read / mScale)
					- HeightPerLine;
		else
			return yAxis;

	}

	/**
	 * 根据读书页数转化成y坐标
	 * */
	float getYAxis(int read) {
		return getHeight() - mGap - ((float) HeightPerLine * read / mScale)
				- HeightPerLine;
	}

	/**
	 * 获取当前读书书名
	 * */
	String getData_BookName(int dayIndex, int indexOfBooks) {
		ArrayList<ReadingRecord> read = new ArrayList<ReadingRecord>();
		String data = "";
		if (null == mData || mData.size() == 0) {
			;
		} else {
			int i = 0;
			for (ReadingRecord r : mData) {
				if ((TimeUtils.getDateDayFromLongTime(mData.get(i).date) - 1) == dayIndex) {
					read.add(mData.get(i));
				}
				i++;
			}
		}
		if (read.size() > 0 && indexOfBooks < read.size())
			return read.get(indexOfBooks).title;
		else
			return data;
	}

	/**
	 * 获取天的索引.如果这一天没有数据,返回false,否则true. // day from 1-31
	 * */
	boolean ifHasDataInThisDayOfIndex(int dayIndex) {

		if (null == mData || mData.size() == 0) {
			return false;
		} else {
			int i = 0;
			for (ReadingRecord r : mData) {
				if ((TimeUtils.getDateDayFromLongTime(mData.get(i).date) - 1) == dayIndex) {
					return true;
				}
				i++;
			}
		}
		return false;
	}

	/**
	 * 获取折线上方要绘制的提示阅读页数数字
	 * */
	String getData_Read(int dayIndex, int indexOfBooks) {
		ArrayList<ReadingRecord> read = new ArrayList<ReadingRecord>();
		String data = "0";
		if (null == mData || mData.size() == 0) {
			;
		} else {
			int i = 0;
			for (ReadingRecord r : mData) {
				if ((TimeUtils.getDateDayFromLongTime(mData.get(i).date) - 1) == dayIndex) {
					read.add(mData.get(i));
				}
				i++;
			}
		}
		if (read.size() > 0 && indexOfBooks < read.size())
			return String.valueOf(String.valueOf(read.get(indexOfBooks).read));
		else
			return data;
	}

	/**
	 * 绘制折线趋势图.分为3部分:折线,圆点,上方提示数字.涉及同一天同时绘制多本书的情况
	 * */
	void drawTrendLines(Canvas canvas) {
		Log.v(this, "drawTrendLines...");

		// 此处计算的是网格两侧空白宽度,最大字符串长度已经包含在2个gap之内了!

		// 有效宽度是出去左右两侧的空隙,高度只需要出去下面的间隙
		float w = getWidth() - 3 * mGap, h = getHeight() - mGap;
		// width of a row
		float rowWidth = (w * 1.0f) / (getVerticalDays() - 1);

		int i = 0;
		for (; i < getVerticalDays(); i++) {
			// Log.v(this, "y" + i + ":" + TEST[i] + "," + getYAxis(i));
			// 如果获取数据为空,那么默认为0
			if (!ifHasDataInThisDayOfIndex(i)) {
				// 这一天 没有阅读数据,后面旧绘制成0
			}

			ArrayList<ReadingRecord> multi = getMultiBooksOneDay(i);
			int indexOfBooks = 0;
			// 如果这天同时阅读了多本书,对每本进行绘制
			if (multi.size() > 0) {
				// 逐个进行绘制
				for (ReadingRecord read : multi) {
					// 绘制折线
					if (i < getVerticalDays() - 1) {
						// 这个地方比较奇特,如果同一天看了多本书.getYAxis(i,
						// indexOfBooks)在前后两天传进去的indexOfBooks是不同的

						// 但是前一天的绘制又出问题了,getBookName(i, indexOfBooks)
						canvas.drawLine(2 * mGap + i * rowWidth,
								getYAxis(i, indexOfBooks), 2 * mGap + (i + 1)
										* rowWidth,
								getYAxis(i + 1, indexOfBooks), mTrendPaint);
					}
					// 绘制圆点
					canvas.drawCircle(2 * mGap + i * rowWidth,
							getYAxis(i, indexOfBooks), mReadingPointRadius,
							mPointPaint);
					// 绘制上方提示数字
					String content = getData_Read(i, indexOfBooks);
					canvas.drawText(content, 2 * mGap + i * rowWidth
							- mPointDataPaint.measureText(content) / 2,
							getYAxis(i, indexOfBooks) - mSmallTextSize / 2,
							mPointDataPaint);

					// 绘制阅读书名
					String bookname = getData_BookName(i, indexOfBooks);
					// 在上方再空出一个文本高度
					canvas.drawText(bookname, 2 * mGap + i * rowWidth
							- mPointDataPaint.measureText(bookname) / 2,
							getYAxis(i, indexOfBooks) - mSmallTextSize / 2
									- mSmallTextSize, mPointDataPaint);

					// 开始绘制 下一本书
					indexOfBooks++;
				}
			}/* 如果这天一本都没有读 */else {
				if (i < getVerticalDays() - 1) {
					// 这个地方比较奇特,如果同一天看了多本书.getYAxis(i,
					// indexOfBooks)在前后两天传进去的indexOfBooks是不同的

					// 但是前一天的绘制又出问题了.getBookName(i, indexOfBooks)
					canvas.drawLine(2 * mGap + i * rowWidth,
							getYAxis(i, indexOfBooks), 2 * mGap + (i + 1)
									* rowWidth, getYAxis(i + 1, indexOfBooks),
							mTrendPaint);
				}
				// 绘制圆点
				canvas.drawCircle(2 * mGap + i * rowWidth,
						getYAxis(i, indexOfBooks), mReadingPointRadius,
						mPointPaint);
				// 绘制上方提示数字
				String content = getData_Read(i, indexOfBooks);
				canvas.drawText(content, 2 * mGap + i * rowWidth
						- mPointDataPaint.measureText(content) / 2,
						getYAxis(i, indexOfBooks) - mSmallTextSize / 2,
						mPointDataPaint);

				// 绘制阅读书名
				String bookname = getData_BookName(i, indexOfBooks);
				// 在上方再空出一个文本高度
				canvas.drawText(bookname, 2 * mGap + i * rowWidth
						- mPointDataPaint.measureText(bookname) / 2,
						getYAxis(i, indexOfBooks) - mSmallTextSize / 2
								- mSmallTextSize, mPointDataPaint);
			}
		}
	}

	/**
	 * 平分(n-1)份就可以有n条线条
	 * */
	int getHorizationalCount() {
		return mLineNum;
	}

	/**
	 * 绘制水平行
	 * */
	void drawHorizontal(Canvas canvas) {
		Log.v(this, "drawHorizontal...");
		float w = getWidth() - 3 * mGap, h = getHeight() - mGap;
		// 绘制mLineNum+1条线.平分mLineNum份
		for (int i = 0; i < getHorizationalCount(); i++) {
			String text = getHorizontalText(i);
			// 此处为了文本垂直居中,添加了+ mTextSize /
			// 2,相当半个字体大小高度.为了文本水平居中,应减去一定误差.为了不使字串穿过左右两侧,还需要考虑最大字串长度.一半大小要大于gap
			if (i % 2 == 0) {
				canvas.drawText(text, mGap - mRulerPaint.measureText(text) / 2,
						i * h / getHorizationalCount() + mTextSize / 2,
						mRulerPaint);
			}
			// 此处要出去两侧空白,为了计算方便,两侧空白,底部空白宽度一样,
			if (i == getHorizationalCount() - 1) {
				mGridPaint.setColor(mContext.getResources().getColor(
						R.color.black));
			} else {
				mGridPaint.setColor(mReadingProgressGridColor);
			}
			canvas.drawLine(mGap * 2, i * h / getHorizationalCount(),
					getWidth() - mGap, i * h / getHorizationalCount(),
					mGridPaint);
		}
	}

	/**
	 * 根据当前所处位置(也就是手指滑动到的年月)确定当前月,得到这个月天数
	 * 
	 * @throws ParseException
	 * */
	int getDaysOfCurrentMonth() {

		Calendar c = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

		try {
			c.setTime(sdf.parse(mYear + "-" + (mMonth + 1)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	/**
	 * 获取要绘制的列数,因为我们31天只需要平分30份即可,画31条线
	 * */
	int getVerticalDays() {
		return getDaysOfCurrentMonth();
	}

	/**
	 * 绘制垂直列
	 * */
	void drawVertical(Canvas canvas) {
		Log.v(this, "drawVertical...");
		// 此处计算的是网格两侧空白宽度,最大字符串长度已经包含在2个gap之内了!

		// 有效宽度是出去左右两侧的空隙,高度只需要出去下面的间隙
		float w = getWidth() - 3 * mGap, h = getHeight() - mGap;
		// width of a row
		float rowWidth = (w * 1.0f) / (getVerticalDays() - 1);
		// 平分30份,31条线条
		for (int i = 0; i < getVerticalDays(); i++) {
			String text = drawVerticalText(i);
			// 此处为了使文本居中,使用减去mPaint.measureText(text) / 2
			if (i % 2 == 0) {
				canvas.drawText(
						text,
						2 * mGap + i * rowWidth
								- (mRulerPaint.measureText(text) / 2),
						getHeight() - mGap + Margin, mRulerPaint);// y+5dp
				// =
				// margin
			}
			// 此处要出去底部空白,相当于减去一行
			if (0 == i) {
				mGridPaint.setColor(mContext.getResources().getColor(
						R.color.black));
			} else {
				mGridPaint.setColor(mReadingProgressGridColor);
			}
			canvas.drawLine(2 * mGap + i * rowWidth, 0,
					2 * mGap + i * rowWidth, getHeight() - 2 * mGap, mGridPaint);
		}
	}

	/**
	 * 根据索引获取水平方向左侧要绘制的文本:页数.drawVerticalText
	 * */
	String drawVerticalText(int i) {

		return /* mYear + "/" + (1 + mMonth) + "/" + */(i + 1) + "";
	}

	/**
	 * 根据索引获取垂直方向要绘制的文本:日期.drawHorizontalText
	 * */
	String getHorizontalText(int i) {
		return String.valueOf(mScale * (getHorizationalCount() - i - 1));
	}

	void drawHeaderDate(Canvas canvas) {
		Log.v(this, "drawHeaderDate...");
		String date = mYear + "/" + (mMonth + 1);
		float x = getWidth() / 2 - mRulerPaint.measureText(date) / 2;
		float y = mRulerPaint.getTextSize() + Margin;
		canvas.drawText(date, x, y, mRulerPaint);
	}

	/**
	 * 这个网格绘制需要出去四边冗余出来的空白.基本上,计算坐标时,都是左侧留出2*mGap,上下右余出1*mGap.我们有31天,需要平分30份,
	 * 使用31条线条
	 * */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Log.v(this, "onDraw...");
		drawHeaderDate(canvas);
		drawHorizontal(canvas);
		drawVertical(canvas);
		drawTrendLines(canvas);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		// Log.v(this, "onLayout ");
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		/*
		 * Log.v(this, "onMeasure widthMeasureSpec:" + widthMeasureSpec +
		 * ",heightMeasureSpec:" + heightMeasureSpec);
		 */

		int speSize = MeasureSpec.getSize(heightMeasureSpec);
		int speMode = MeasureSpec.getMode(heightMeasureSpec);
		/*
		 * Log.d(this, "---speSize = " + speSize + ""); Log.d(this,
		 * "---speMode = " + speMode + "");
		 */
		if (speMode == MeasureSpec.AT_MOST) {
			// Log.d(this, "---AT_MOST---");
		}
		if (speMode == MeasureSpec.EXACTLY) {
			// Log.d(this, "---EXACTLY---");
		}
		if (speMode == MeasureSpec.UNSPECIFIED) {
			// Log.d(this, "---UNSPECIFIED---");
		}
		// 这行决定了这个视图最终的大小,意味着getWidth()是获取这个视图的宽度
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), speSize);

		// 计算行高度,列宽度
		HeightPerLine = (float) (getHeight() - mGap) / getHorizationalCount();
		VerticalW = (float) (getWidth() - 3 * mGap) / (getVerticalDays() - 1);
		// setMeasuredDimension(800, 600);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		/*
		 * if (mGestureDetector.onTouchEvent(ev)) {
		 * ev.setAction(MotionEvent.ACTION_CANCEL); }
		 */

		// 传给activity内的布局和控件
		return super.dispatchTouchEvent(ev);
	}

	boolean moveHorizontal() {
		return false;
	}

	boolean moveVertical() {
		return false;
	}

	boolean moveLeft(float vx) {

		return vx < 0;
	}

	boolean moveRight(float vx) {

		return vx > 0;
	}

	/**
	 * 对于view,在dispatchTouchEvent里面执行分配到手势动作也可以
	 * */
	/*
	 * @Override public boolean onTouch(View arg0, MotionEvent event) { // TODO
	 * Auto-generated method stub
	 * 
	 * // 进行与操作是为了判断多点触摸 switch (event.getAction() & MotionEvent.ACTION_MASK) {
	 * case MotionEvent.ACTION_DOWN: // 第一个手指按下事件
	 * 
	 * startPoint.set(event.getX(), event.getY()); mode = DRAG; break; case
	 * MotionEvent.ACTION_POINTER_DOWN: // 第二个手指按下事件 oriDis = distance(event);
	 * if (oriDis > 10f) { midPoint = middle(event); mode = ZOOM; } break; case
	 * MotionEvent.ACTION_UP: case MotionEvent.ACTION_POINTER_UP: // 手指放开事件 mode
	 * = NONE; break; case MotionEvent.ACTION_MOVE: // 手指滑动事件 if (mode == DRAG)
	 * { // 是一个手指拖动
	 * 
	 * } else if (mode == ZOOM) { // 两个手指滑动 float newDist = distance(event); if
	 * (newDist > 10f) {
	 * 
	 * float scale = newDist / oriDis;
	 * 
	 * } } break; } return mGestureDetector.onTouchEvent(event);// key !
	 * 
	 * }
	 */
	// 计算两个触摸点之间的距离
	private float distance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 计算两个触摸点的中点
	private PointF middle(MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		return new PointF(x / 2, y / 2);
	}

	// http://blog.csdn.net/cyp331203/article/details/45071069

	public void setMonthIndex(int month) {
		mMonth = month;
	}

	public int getMonthIndex() {
		return mMonth;
	}

	/**
	 * reload data & refresh ui
	 * */
	public void refresh(Cursor c) {
		// TODO Auto-generated method stub
		if (null == c)
			return;

		reloadData(c);

		refreshUi();
	}

	/**
	 * reloading data.后面的数据默认为空
	 * */
	void reloadData(Cursor c) {
		Log.v(this, "reloading data...");
		// first ,clear original data
		mData.clear();
		m2DimensionVector.clear();

		if (0 == c.getCount()) {
			c.close();
			return;
		}
		// cursor reset to 0.

		c.moveToFirst();
		do {
			ReadingRecord r = new ReadingRecord(c.getString(c
					.getColumnIndex(ReadingRecord.TYPE_title)), c.getString(c
					.getColumnIndex(ReadingRecord.TYPE_author)), c.getInt(c
					.getColumnIndex(ReadingRecord.TYPE_read)), c.getLong(c
					.getColumnIndex(ReadingRecord.TYPE_date)));
			mData.add(r);

			// 为了不使他为空,预先加入一个.
			if (m2DimensionVector.size() == 0) {
				Vector<ReadingRecord> original = new Vector<ReadingRecord>();
				original.add(r);
				m2DimensionVector.add(original);
			} else {
				for (int i = 0; i < m2DimensionVector.size(); i++) {
					Vector<ReadingRecord> new_v = null;
					// 书名相同,日期不同
					if (m2DimensionVector.get(i).contains(r)) {
						m2DimensionVector.get(i).add(r);
					} else {
						new_v = new Vector<ReadingRecord>();
						new_v.add(r);
					}
					if (null != new_v) {
						m2DimensionVector.add(new_v);
					}
					break;// !IMPORTANT
				}
			}
		} while (c.moveToNext());
		c.close();
		dump2DimensionVector();
	}

	void dump2DimensionVector() {
		Log.v(this,
				"########################dump2DimensionVector#################################");
		for (int i = 0; i < m2DimensionVector.size(); i++) {
			for (int j = 0; j < m2DimensionVector.get(i).size(); j++) {
				Log.v(this,
						i + "		" + j + "		" + m2DimensionVector.get(i).get(j));
			}
		}
		Log.v(this,
				"########################dump2DimensionVector#################################");
	}
}
