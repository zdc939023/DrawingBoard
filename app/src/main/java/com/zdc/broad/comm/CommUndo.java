package com.zdc.broad.comm;

public interface CommUndo {
     void undo();
     void redo();
     boolean canUndo();
     boolean canRedo();
}
