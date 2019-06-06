package com.tst.drawbroad.pens;

import android.graphics.BlurMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;

public class BlurPen extends PenAbstract {

	private MaskFilter mBlur;

	public BlurPen(int penSize, int penColor) {
		this(penSize, penColor, Paint.Style.STROKE);
	}

	public BlurPen(int size, int penColor, Paint.Style style) {
		super(size, penColor, style);
		mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
		mPenPaint.setMaskFilter(mBlur);
	}

	@Override
	public String toString() {
		return "type:blurPen: " + "\tshap: " + currentShape + "\thasDraw: "
				+ hasDraw() + "\tsize: " + penSize + "\tstyle:" + style;
	}
}
