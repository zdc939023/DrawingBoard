package com.tst.drawbroad.pens;

import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;

public class EmbossPen extends PenAbstract {
	private MaskFilter mEmboss;

	public EmbossPen(int penSize, int penColor) {
		this(penSize, penColor, Paint.Style.STROKE);
	}

	public EmbossPen(int size, int penColor, Paint.Style style) {
		super(size, penColor, style);
		mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);
		mPenPaint.setMaskFilter(mEmboss);
	}

	@Override
	public String toString() {
		return "\tEmboss: " + "\tshap: " + currentShape + "\thasDraw: "
				+ hasDraw() + "\tsize: " + penSize + "\tstyle:" + style;
	}
}