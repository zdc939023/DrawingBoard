package com.tst.drawbroad.comm;

import android.graphics.Canvas;

import com.tst.drawbroad.PaintView;

import java.util.ArrayList;

public class CommUndoStack {

    private int stackSize=0;
    private PaintView paintView=null;
    private ArrayList<CommHandwriting> undoStack=new ArrayList<>();
    private ArrayList<CommHandwriting> redoStack=new ArrayList<>();
    private ArrayList<CommHandwriting> oldActionStack=new ArrayList<>();

    public CommUndoStack(PaintView paintView,int stackSize){
        this.paintView=paintView;
        this.stackSize=stackSize;
    }

    public void push(CommHandwriting handwriting){
        if (null!=handwriting){
            if (undoStack.size()==stackSize&&stackSize>0){
                CommHandwriting removeHand=undoStack.get(0);
                oldActionStack.add(removeHand);
                undoStack.remove(removeHand);
            }
            undoStack.add(handwriting);
        }
    }

    public void clearAll(){
        redoStack.clear();
        undoStack.clear();
        oldActionStack.clear();
    }

    public void undo(){
        if (canUndo()&&null!=paintView){
            CommHandwriting handwriting=undoStack.get(undoStack.size()-1);
            redoStack.add(handwriting);
            undoStack.remove(undoStack.size()-1);
            redRaw();
        }
    }

    public void redo(){
        if (canRedo()&&null!=paintView){
            CommHandwriting handwriting=redoStack.get(redoStack.size()-1);
            undoStack.add(handwriting);
            redoStack.remove(redoStack.size()-1);
            redRaw();
        }
    }

    public boolean canUndo(){
        return undoStack.size()>0;
    }

    public boolean canRedo(){
        return redoStack.size()>0;
    }

    private void redRaw(){
        if (null!=paintView.getOriginalBitmap()&&!paintView.getOriginalBitmap().isRecycled()){
            paintView.setTempForeBitmap(paintView.getOriginalBitmap());
        }else{
            paintView.createCanvasBitmap(paintView.getDrawingBroadWidth(),paintView.getDrawingBroadHeight());
        }
        Canvas canvas=paintView.getCommCanvas();
        for (CommHandwriting handwriting : oldActionStack) {
            handwriting.draw(canvas);
        }
        for (CommHandwriting handwriting : undoStack) {
            handwriting.draw(canvas);
        }
        paintView.invalidate();
    }
}
