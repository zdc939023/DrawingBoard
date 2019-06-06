package com.tst.drawbroad.comm;

import android.graphics.Color;

public class PaintConstants {

	private PaintConstants() {

	}

	public static final class ERASER_SIZE {
		public static final int SIZE_1 = 60;
	}

	public static final class PEN_SIZE {
		public static final int SIZE_1 = 2;
		public static final int SIZE_2 = 5;
	}

	public static final class SHAP {
		public static final int CURV = 1;
		public static final int LINE = 2;
		public static final int RECT = 3;
		public static final int CIRCLE = 4;
		public static final int OVAL = 5;
		public static final int SQUARE = 6;
	}

	public static final class PATH {
		public static final String SAVE_PATH = "/sdcard/paintPad";
	}

	public static final class PEN_TYPE {
		public static final int PLAIN_PEN = 1;
		public static final int ERASER = 2;
		public static final int BLUR = 3;
		public static final int EMBOSS = 4;
	}


	public static final class DEFAULT{
		public static final int PEN_COLOR = Color.BLACK;
		public static final int BACKGROUND_COLOR = Color.parseColor("#AEDEE4");
	}


	public static final int UNDO_STACK_SIZE = 10;

	public static final int COLOR_VIEW_SIZE = 80;
	
	public static final int LOAD_ACTIVITY = 1;

	public static final int COLOR1 = Color.argb(255, 44, 152, 140);
	public static final int COLOR2 = Color.argb(255, 48, 115, 170);
	public static final int COLOR3 = Color.argb(255, 139, 26, 99);
	public static final int COLOR4 = Color.argb(255, 112, 101, 89);
	public static final int COLOR5 = Color.argb(255, 40, 36, 37);
	public static final int COLOR6 = Color.argb(255, 226, 226, 226);
	public static final int COLOR7 = Color.argb(255, 219, 88, 50);
	public static final int COLOR8 = Color.argb(255, 129, 184, 69);
}
