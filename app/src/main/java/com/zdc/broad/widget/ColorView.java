package com.zdc.broad.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class ColorView extends android.support.v7.widget.AppCompatRadioButton {

	private static final int STROKE_WIDTH_GRAY = 5;
	private static final int STROKE_WIDTH_WITE = 10;
	private static final int VIEW_WIDTH = 75;
	private static final int VIEW_HEIGHT = 75;
	
	
	private int color = 0;
	private Paint mPaint = null;
	private Paint mBitmapPaint = null;

	private Rect mRect = null;
	private Bitmap mBitmap = null;
	private Canvas mCanvas = null;

	
	public ColorView(Context context, int color) {
		super(context);
		this.color = color;
		this.setMinimumHeight(VIEW_HEIGHT);
		this.setMinimumWidth(VIEW_WIDTH);
		mPaint = new Paint();
		setmPaint();

		mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		mBitmap = Bitmap.createBitmap(VIEW_HEIGHT, VIEW_WIDTH, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas();
		mCanvas.setBitmap(mBitmap);
		mRect = new Rect(0, 0, VIEW_HEIGHT, VIEW_WIDTH);
	}


	private void setmPaint() {
		mPaint.setDither(true);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
	}


	public void setColor(int color) {
		this.color = color;
		drawCheckedBitmap();
		invalidate();
	}
	
	public int getColor() {
		return color;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isChecked()) {
			drawCheckedBitmap();
		} else if (!isChecked()) {
			drawUnCheckedBitmap();
		}
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
	}

	private void drawUnCheckedBitmap() {
		mCanvas.drawColor(color);
	}

	private void drawCheckedBitmap() {
		mCanvas.drawColor(color);
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(STROKE_WIDTH_WITE);
		drawRect();
		mPaint.setColor(Color.GRAY);
		mPaint.setStrokeWidth(STROKE_WIDTH_GRAY);
		drawRect();
	}


	private void drawRect() {
		mCanvas.drawRect(mRect, mPaint);
	}
}
