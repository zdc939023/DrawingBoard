package com.zdc.broad.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.zdc.broad.pens.Shapable;


public class Circle extends ShapeAbstract {

	public Circle(Shapable paintTool) {
		super(paintTool);
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (canvas==null || paint == null) {
			return;
		}
		super.draw(canvas, paint);
		float cx = (x1 + x2)/2;
		float cy = (y1+y2)/2;
		float radius = (float) Math.sqrt(Math.pow(x1 - x2, 2)
				+ Math.pow(y1 - y2, 2))/2;
		canvas.drawCircle(cx, cy, radius, paint);
	}

	@Override
	public String toString() {
		return " circle";
	}
}
