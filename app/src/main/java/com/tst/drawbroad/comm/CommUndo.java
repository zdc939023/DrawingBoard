package com.tst.drawbroad.comm;

public interface CommUndo {
     void undo();
     void redo();
     boolean canUndo();
     boolean canRedo();
}
