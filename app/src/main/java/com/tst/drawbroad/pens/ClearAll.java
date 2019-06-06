package com.tst.drawbroad.pens;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.tst.drawbroad.comm.CommHandwriting;

public class ClearAll implements CommHandwriting {
	private Paint mPaint = new Paint();
	private boolean mHasDraw = false;

	public ClearAll() {
	}

	@Override
	public void draw(Canvas canvas) {
		if (null != canvas) {
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			canvas.drawPaint(mPaint);
		}
	}

	@Override
	public void touchDown(float x, float y) {

	}

	@Override
	public void touchMove(float x, float y) {

	}

	@Override
	public void touchUp(float x, float y) {

	}

	@Override
	public boolean hasDraw() {
		return mHasDraw;
	}
}
