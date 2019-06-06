package com.tst.drawbroad.tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class BitmapUtils {

    public static Bitmap duplicateBitmap(Bitmap bmpSrc) {
        if (null == bmpSrc) {
            return null;
        }

        int bmpSrcWidth = bmpSrc.getWidth();
        int bmpSrcHeight = bmpSrc.getHeight();

        Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight,
                Bitmap.Config.ARGB_8888);
        if (null != bmpDest) {
            Canvas canvas = new Canvas(bmpDest);
            final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
            canvas.drawBitmap(bmpSrc, rect, rect, null);
        }
        return bmpDest;
    }

    public static void destroyBitmap(Bitmap bitmap){
        if (bitmap!=null&&!bitmap.isRecycled()){
            bitmap.recycle();
            bitmap=null;
        }
    }
}
