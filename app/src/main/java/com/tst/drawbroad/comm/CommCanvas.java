package com.tst.drawbroad.comm;

import android.graphics.Canvas;

public class CommCanvas extends Canvas implements Cloneable {
    public CommCanvas clone() {
        CommCanvas o = null;
        try {
            o = (CommCanvas) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
