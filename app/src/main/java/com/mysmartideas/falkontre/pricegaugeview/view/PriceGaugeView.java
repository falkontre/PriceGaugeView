package com.mysmartideas.falkontre.pricegaugeview.view;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.BounceInterpolator;

import com.mysmartideas.falkontre.pricegaugeview.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public class PriceGaugeView extends View {

	private static final int GREEN_COLOR = 0xFF06C494;
	private static final int GRAY_COLOR = 0xFFBEBBB4;
	private static final int CYAN_COLOR = 0xFF00BCD4;
	private static final float mStartAngle = 115f;
	private static final float mEndAngle = 230f;
	private static float[] prices = {
		1.00f, 1.00f,
		1.00f, 1.00f,
		1.00f, 1.00f,
		1.00f, 1.00f,
		1.00f, 1.00f,
		1.00f
	};
	private final int[] mColors = new int[]{
		0xFF00FF00,
		0xFFFFD600,
		0xFFFF0000
	};
	private int width;
	private int height;
	private int radius;
	private Bitmap mBitmap;
	private int mBitmapWidth;
	private int mBitmapHeight;
	private Paint mGradientRingPaint;
	private Paint mSmallCalibrationPaint;
	private Paint mBigCalibrationPaint;
	private Paint mMiddleRingPaint;
	private Paint mTextPaint;
	private Paint mMiddleProgressPaint;
	private Paint mPointerBitmapPaint;
	private Paint mCenterTextPaint;
	private RectF mOuterArc;
	private RectF mMiddleArc;
	private float oval4;
	private RectF mMiddleProgressArc;
	private float mTotalAngle = 230f;
	private float mCurrentAngle = 0f;
	private int defaultSize;
	private float mMinNum = 0f;
	private float mMaxNum = 0f;
	private String centralText = "Prezzo attuale";
	private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
	private DecimalFormat df = new DecimalFormat("0.00");
	private DecimalFormatSymbols symbols = new DecimalFormatSymbols();
	private Context context;

	public PriceGaugeView(Context context) {
		this(context, null);
		this.context = context;
	}

	public PriceGaugeView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.context = context;
	}

	public PriceGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init();
	}

	public void setPrices(float minPrice, float maxPrice) {
		mMinNum = minPrice;
		mMaxNum = maxPrice;
		float delta = (maxPrice - minPrice) / 10;
		prices = new float[11];
		for (int i = 0; i < prices.length; i++) {
			if (i == 0)
				prices[i] = maxPrice;
			if (i == prices.length)
				prices[i] = minPrice;
			prices[i] = maxPrice - (delta * i);
		}
	}

	private void init() {
		symbols.setDecimalSeparator(',');
		df.setDecimalFormatSymbols(symbols);
		df.setRoundingMode(RoundingMode.HALF_DOWN);

		defaultSize = dp2px(300);

		mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

		mGradientRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mGradientRingPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
		float position[] = {0.1f, 0.3f, 0.8f};
		Shader mShader = new SweepGradient(width / 2, radius, mColors, position);
		mGradientRingPaint.setShader(mShader);
		mGradientRingPaint.setStrokeCap(Paint.Cap.ROUND);
		mGradientRingPaint.setStyle(Paint.Style.STROKE);
		mGradientRingPaint.setStrokeWidth(40);

		mSmallCalibrationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSmallCalibrationPaint.setColor(Color.WHITE);
		mSmallCalibrationPaint.setStyle(Paint.Style.STROKE);
		mSmallCalibrationPaint.setStrokeWidth(1);

		mBigCalibrationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBigCalibrationPaint.setColor(Color.WHITE);
		mBigCalibrationPaint.setStyle(Paint.Style.STROKE);
		mBigCalibrationPaint.setStrokeWidth(4);

		mMiddleRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMiddleRingPaint.setStyle(Paint.Style.STROKE);
		mMiddleRingPaint.setStrokeCap(Paint.Cap.ROUND);
		mMiddleRingPaint.setStrokeWidth(5);
		mMiddleRingPaint.setColor(GRAY_COLOR);

		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(GRAY_COLOR);
		mTextPaint.setTextSize(55);

		mCenterTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCenterTextPaint.setTextAlign(Paint.Align.CENTER);
		mCenterTextPaint.setColor(mColors[0]);

		mMiddleProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mMiddleProgressPaint.setColor(CYAN_COLOR);
		mMiddleProgressPaint.setStrokeCap(Paint.Cap.ROUND);
		mMiddleProgressPaint.setStrokeWidth(5);
		mMiddleProgressPaint.setStyle(Paint.Style.STROKE);

		mPointerBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointerBitmapPaint.setColor(GREEN_COLOR);

		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pointer2);
		mBitmapHeight = mBitmap.getHeight();
		mBitmapWidth = mBitmap.getWidth();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(resolveMeasure(widthMeasureSpec, defaultSize),
			resolveMeasure(heightMeasureSpec, defaultSize));
	}

	public int resolveMeasure(int measureSpec, int defaultSize) {
		int result = 0;
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (MeasureSpec.getMode(measureSpec)) {
			case MeasureSpec.UNSPECIFIED:
				result = defaultSize;
				break;
			case MeasureSpec.AT_MOST:
				result = Math.min(specSize, defaultSize);
				break;
			case MeasureSpec.EXACTLY:
				break;
			default:
				result = defaultSize;
		}
		return result;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		radius = width / 2;
		float oval1 = radius - mGradientRingPaint.getStrokeWidth() * 0.5f;
		mOuterArc = new RectF(-oval1, -oval1, oval1, oval1);
		float oval3 = radius * 3 / 4;
		mMiddleArc = new RectF(-oval3, -oval3, oval3, oval3);
		oval4 = radius * 6 / 8;
		mMiddleProgressArc = new RectF(-oval4, -oval4, oval4, oval4);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.setDrawFilter(mPaintFlagsDrawFilter);

		drawArc(canvas);
		drawCalibration(canvas);
		drawArcText(canvas);
		drawCenterText(canvas);
		drawBitmapProgress(canvas);
	}

	private void drawBitmapProgress(Canvas canvas) {
		canvas.save();
		canvas.translate(radius, radius);
		canvas.rotate(270);
		canvas.drawArc(mMiddleProgressArc, -mStartAngle, mCurrentAngle, false, mMiddleProgressPaint);
		canvas.rotate(68 + mCurrentAngle);
		Matrix matrix = new Matrix();
		matrix.preTranslate(-oval4 - (mBitmapWidth + 20) * 3 / 8, -mBitmapHeight / 2);
		canvas.drawBitmap(mBitmap, matrix, mPointerBitmapPaint);
		canvas.restore();
	}

	private void drawCenterText(Canvas canvas) {
		mCenterTextPaint.setTextSize(200);
		mCenterTextPaint.setStyle(Paint.Style.STROKE);
		canvas.drawText("€ " + String.valueOf(df.format(mMinNum)), radius, radius, mCenterTextPaint);

		mCenterTextPaint.setTextSize(80);
		canvas.drawText(centralText, radius, radius + 120, mCenterTextPaint);
	}

	private void drawArcText(Canvas canvas) {
		for (int i = 0; i <= 10; i++) {
			canvas.save();
			canvas.rotate(-(-10 + 20 * i - 88), radius, radius);
			canvas.drawText(String.format("%.2f", prices[i]), radius - 10, radius * 3 / 16, mTextPaint);
			canvas.restore();
		}
	}

	private void drawCalibration(Canvas canvas) {
		int dst = (int) (2 * radius - mGradientRingPaint.getStrokeWidth());
		for (int i = 0; i <= 50; i++) {
			canvas.save();
			canvas.rotate(-(-13 + 4 * i), radius, radius);
			if (i % 5 == 0) {
				canvas.drawLine(dst, radius, 2 * radius, radius, mBigCalibrationPaint);
			} else {
				canvas.drawLine(dst, radius, 2 * radius, radius, mSmallCalibrationPaint);
			}
			canvas.restore();
		}
	}

	private void drawArc(Canvas canvas) {

		canvas.save();
		canvas.translate(width / 2, height / 2);

		canvas.rotate(140);
		canvas.drawArc(mOuterArc, -mStartAngle, -mEndAngle, false, mGradientRingPaint);
		canvas.drawArc(mMiddleArc, -mStartAngle, -mEndAngle, false, mMiddleRingPaint);
		canvas.restore();
	}

	public void setSesameValues(float num) {
		float incrementer;
		if (num < prices[10]) {
			mTotalAngle = 4f;
			incrementer = 0;
			mMaxNum = num;
		} else if (num > prices[0]) {
			mTotalAngle = 230f;
			incrementer = 100;
			mMaxNum = num;
		} else {
			float start = 10f;
			float angleIncrement = 2.11f; // maxAngle - start / 100
			float percentageSpace = mMaxNum - mMinNum; // 0,84
			float target = mMaxNum - num; // 0,02
			float divisor = percentageSpace / target; // 42
			float normalizer = 100 / divisor;
			incrementer = 100 - normalizer; // 97,62
			mTotalAngle = start + (angleIncrement * incrementer);
			mMaxNum = num;
		}
		startRotateAnim();
		animatePriceColor((int) incrementer);
	}

	public void startRotateAnim() {
		ValueAnimator mAngleAnim = ValueAnimator.ofFloat(mCurrentAngle, mTotalAngle);
		mAngleAnim.setInterpolator(new BounceInterpolator());
		mAngleAnim.setDuration(2000);
		mAngleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {

				mCurrentAngle = (float) valueAnimator.getAnimatedValue();
				postInvalidate();
			}
		});
		mAngleAnim.start();


		ValueAnimator mNumAnim = ValueAnimator.ofFloat(mMinNum, mMaxNum);
		mNumAnim.setDuration(2000);
		mNumAnim.setInterpolator(new BounceInterpolator());
		mNumAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				mMinNum = (float) valueAnimator.getAnimatedValue();
				postInvalidate();
			}
		});
		mNumAnim.start();
	}

	private void animatePriceColor(int combination) {
		ObjectAnimator animator;
		if (combination < 30) {
			animator = ObjectAnimator.ofObject(mCenterTextPaint, "color", new ArgbEvaluator(), mColors[0]);
		} else if (combination > 30 && combination < 70) {
			animator = ObjectAnimator.ofObject(mCenterTextPaint, "color", new ArgbEvaluator(), mColors[0], mColors[1]);
		} else {
			animator = ObjectAnimator.ofObject(mCenterTextPaint, "color", new ArgbEvaluator(), mColors[0], mColors[1], mColors[2]);
		}
		animator.setDuration(2000);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// TODO Auto-generated method stub
				invalidate();
			}

		});
		animator.start();
	}

	public int dp2px(int values) {
		float density = getResources().getDisplayMetrics().density;
		return (int) (values * density + 0.5f);
	}
}
