package com.zdc.broad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zdc.broad.comm.CommCanvas;
import com.zdc.broad.comm.CommHandwriting;
import com.zdc.broad.comm.CommUndo;
import com.zdc.broad.comm.PaintConstants;
import com.zdc.broad.comm.PaintViewCallBack;
import com.zdc.broad.pens.BlurPen;
import com.zdc.broad.pens.ClearAll;
import com.zdc.broad.pens.EmbossPen;
import com.zdc.broad.pens.Eraser;
import com.zdc.broad.pens.PlainPen;
import com.zdc.broad.pens.Shapable;
import com.zdc.broad.pens.ShapesInterface;
import com.zdc.broad.shapes.Circle;
import com.zdc.broad.shapes.Curv;
import com.zdc.broad.shapes.Line;
import com.zdc.broad.shapes.Oval;
import com.zdc.broad.shapes.Rectangle;
import com.zdc.broad.shapes.Square;
import com.zdc.broad.tools.BitmapUtils;

import java.util.ArrayList;

import static com.zdc.broad.comm.PaintConstants.UNDO_STACK_SIZE;

public class Drawing extends View implements CommUndo,Cloneable {

    private static String TAG="Drawing";

    private CommCanvas commCanvas=null;
    /**
     * 橡皮擦
     */
    private Paint eraserPaint=null;
    private PaintPadUndoStack padUndoStack=null;
    /**
     * 画图
     */
    private Paint drawPaint=null;

    private CommHandwriting handwriting=null;
    /**
     * 画笔类型
     */
    private ShapesInterface mCurrentShape=null;

    /**
     * 绘制操作的bitmap
     */
    private Bitmap drawBitmap=null;
    /**
     * 原图
     */
    private Bitmap originalBitmap=null;

    /**
     * 画板的大小
     */
    private int drawingBroadWidth=-1,drawingBroadHeight=-1;

    private PaintViewCallBack mCallBack = null;

    /**
     * 保存当前的X,Y坐标
     */
    private float currentX = 300,currentY = 500;
    /**
     * 需要绘制的形状
     */
    private int shapeType=0;
    /**
     * 画笔类型
     */
    private int paintType= PaintConstants.PEN_TYPE.PLAIN_PEN;
    /**
     * 画笔颜色
     */
    private int mPenColor = PaintConstants.DEFAULT.PEN_COLOR;;
    /**
     * 笔触大小
     */
    private int mPenSize = PaintConstants.PEN_SIZE.SIZE_1 ;
    /**
     * 橡皮擦的大小
     */
    private int mEraserSize = PaintConstants.ERASER_SIZE.SIZE_1;

    /**
     * 保存撤销与反撤销的次数
     */
    private int mStackedSize = UNDO_STACK_SIZE;
    private Paint.Style mStyle = Paint.Style.STROKE;

    private boolean isTouchUp = false;

    public void setCallBack(PaintViewCallBack mCallBack){
        this.mCallBack=mCallBack;
    }


    public Drawing(Context context) {
        super(context,null);
    }

    public Drawing(Context context,@Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Drawing(Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        commCanvas=new CommCanvas();
        drawPaint=new Paint(Paint.DITHER_FLAG);
        padUndoStack = new PaintPadUndoStack(this, mStackedSize);
        paintType= PaintConstants.PEN_TYPE.PLAIN_PEN;
        shapeType= PaintConstants.SHAP.CURV;
        createNewPen();
        initEraserPaint();
    }
    /**
     * 初始化橡皮擦画笔大小
     */
    private void initEraserPaint(){
        eraserPaint=new Paint();
        eraserPaint.setColor(Color.parseColor("#595957"));
        eraserPaint.setDither(true);
        eraserPaint.setAntiAlias(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);
    }
    /**
     * 创建一个新的画笔
     */
    void createNewPen() {
        CommHandwriting tool = null;
        switch (paintType) {
            case PaintConstants.PEN_TYPE.PLAIN_PEN:
                tool = new PlainPen(mPenSize, mPenColor, mStyle);
                break;
            case PaintConstants.PEN_TYPE.ERASER:
                tool = new Eraser(mEraserSize);
                break;
            case PaintConstants.PEN_TYPE.BLUR:
                tool = new BlurPen(mPenSize, mPenColor, mStyle);
                break;
            case PaintConstants.PEN_TYPE.EMBOSS:
                tool = new EmbossPen(mPenSize, mPenColor, mStyle);
                break;
            default:
                break;
        }
        handwriting = tool;
        setShape();
    }
    /**
     * 设置具体形状，需要注意的是构造函数中的Painter必须是新鲜出炉的
     */
    private void setShape() {
        if (handwriting instanceof Shapable) {
            switch (shapeType) {
                case PaintConstants.SHAP.CURV:
                    mCurrentShape = new Curv((Shapable) handwriting);
                    break;
                case PaintConstants.SHAP.LINE:
                    mCurrentShape = new Line((Shapable) handwriting);
                    break;
                case PaintConstants.SHAP.SQUARE:
                    mCurrentShape = new Square((Shapable) handwriting);
                    break;
                case PaintConstants.SHAP.RECT:
                    mCurrentShape = new Rectangle((Shapable) handwriting);
                    break;
                case PaintConstants.SHAP.CIRCLE:
                    mCurrentShape = new Circle((Shapable) handwriting);
                    break;
                case PaintConstants.SHAP.OVAL:
                    mCurrentShape = new Oval((Shapable) handwriting);
                    break;
                default:
                    break;
            }
            ((Shapable) handwriting).setShap(mCurrentShape);
        }
    }

    public void setTempForeBitmap(Bitmap tempForeBitmap){
        if (tempForeBitmap!=null){
            BitmapUtils.destroyBitmap(drawBitmap);
            drawBitmap=BitmapUtils.duplicateBitmap(tempForeBitmap);
            if (drawBitmap!=null&&commCanvas!=null){
                commCanvas.setBitmap(drawBitmap);
                invalidate();
            }
        }
    }

    /**
     * 创建bitMap同时获得其canvas
     */
    public void createCanvasBitmap(int w, int h) {
        if(w>0 || h>0){
            drawBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            commCanvas.setBitmap(drawBitmap);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (drawBitmap!=null&&!drawBitmap.isRecycled()){
            canvas.drawBitmap(drawBitmap,0,0,drawPaint);
        }
        if (!isTouchUp){
            if (paintType!= PaintConstants.PEN_TYPE.ERASER){
                handwriting.draw(canvas);
            }else{
                canvas.drawCircle(currentX,currentY,mEraserSize/2,eraserPaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i(TAG,"onSizeChanged w:"+w+"--h:"+h+"-oldw:"+oldw+"-oldh:"+oldh);
        drawingBroadHeight=h;
        drawingBroadWidth=w;
        createCanvasBitmap(w,h);
    }

    private float oldx = 0,oldy=0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        currentX=x;
        currentY=y;
        isTouchUp=false;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                oldx=x;oldy=y;
                down(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                move(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            case MotionEvent.ACTION_UP:
                if (handwriting.hasDraw()){
                    padUndoStack.push(handwriting);
                    if (mCallBack!=null)mCallBack.onHasDraw();
                }
                up(event.getX(),event.getY());
                isTouchUp = true;
                invalidate();
                break;
        }
        return true;
    }

    private void down(float x,float y){
        commCanvas.setBitmap(drawBitmap);
        createNewPen();
        handwriting.touchDown(x,y);
        mCallBack.onTouchDown();
    }

    private void move(float x,float y){
        handwriting.touchMove(x,y);
        if (paintType== PaintConstants.PEN_TYPE.ERASER){
            handwriting.draw(commCanvas);
        }
    }

    private void up(float x,float y){
        handwriting.touchUp(x,y);
        handwriting.draw(commCanvas);
        final float dx=Math.abs(oldx-x);
        final float dy=Math.abs(oldy-y);
        if (dx==0&&dy==0){
            handwriting.touchUp(x+1,y);
            handwriting.draw(commCanvas);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    public void addNewDrawing(){
        clearAll(false);
        resetHandwriting();
    }
    /**
     * 设置画笔的颜色
     * @param color
     */
    public void setPenColor(int color){
        mPenColor=color;
    }

    /**
     * 改变当前画笔的类型
     */
    public void setPenType(int type) {
        switch (type) {
            case PaintConstants.PEN_TYPE.BLUR:
            case PaintConstants.PEN_TYPE.PLAIN_PEN:
            case PaintConstants.PEN_TYPE.EMBOSS:
            case PaintConstants.PEN_TYPE.ERASER:
                paintType = type;
                break;
            default:
                paintType = PaintConstants.PEN_TYPE.PLAIN_PEN;
                break;
        }
    }
    /**
     * 设置画笔大小
     * @param size
     */
    public void setPenSize(int size){
        mPenSize=size;
    }
    /**
     * 加载图到画板中
     * @param bitmap
     */
    public void loadImg(Bitmap bitmap){
        if (bitmap==null)return;
        clearAll(false);
        drawBitmap=BitmapUtils.duplicateBitmap(bitmap);
        originalBitmap=BitmapUtils.duplicateBitmap(bitmap);
        invalidate();
    }

    public Bitmap getSnapShoot() {
        // 获得当前的view的图片
        setDrawingCacheEnabled(true);
        buildDrawingCache(true);
        Bitmap bitmap = getDrawingCache(true);
        Bitmap bmp = BitmapUtils.duplicateBitmap(bitmap);
        BitmapUtils.destroyBitmap(bitmap);
        // 将缓存清理掉
        setDrawingCacheEnabled(false);
        return bmp;
    }

    /**
     * 清除所有笔记
     * @param isUndo
     */
    public void clearAll(boolean isUndo){
        BitmapUtils.destroyBitmap(drawBitmap);
        BitmapUtils.destroyBitmap(originalBitmap);
        if (isUndo){
            padUndoStack.push(new ClearAll());
        }else{
            padUndoStack.clearAll();
        }
        createCanvasBitmap(drawingBroadWidth,drawingBroadHeight);
        invalidate();
    }
    public void onHasDraw() {
        if (mCallBack != null) {
            // 控制undo\redo的现实
            mCallBack.onHasDraw();
        }
    }
    /**
     * 清除笔迹
     */
    private void resetHandwriting() {
        if (null!=padUndoStack)padUndoStack.clearAll();
    }

    @Override
    public void undo() {
        if (null!=padUndoStack)padUndoStack.undo();
    }

    @Override
    public void redo() {
        if (null!=padUndoStack)padUndoStack.redo();
    }

    @Override
    public boolean canUndo() {
        if (null!=padUndoStack){
           return padUndoStack.canUndo();
        }else{
            return false;
        }
    }

    @Override
    public boolean canRedo() {
        if (null!=padUndoStack){
            return padUndoStack.canRedo();
        }else{
            return false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BitmapUtils.destroyBitmap(drawBitmap);
        BitmapUtils.destroyBitmap(originalBitmap);
    }

    private class PaintPadUndoStack {
        private int m_stackSize = 0;
        private Drawing mDrawing = null;
        private ArrayList<CommHandwriting> mUndoStack = new ArrayList<>();
        private ArrayList<CommHandwriting> mRedoStack = new ArrayList<>();
        private ArrayList<CommHandwriting> mOldActionStack = new ArrayList<>();

        public PaintPadUndoStack(Drawing drawing, int stackSize) {
            mDrawing = drawing;
            m_stackSize = stackSize;
        }
        /**
         * 将painter存入栈中
         */
        public void push(CommHandwriting penTool) {
            if (null != penTool) {
                // 如果undo已经存满
                if (mUndoStack.size() == m_stackSize && m_stackSize > 0) {
                    // 得到最远的画笔
                    CommHandwriting removedTool = mUndoStack.get(0);
                    // 所有的笔迹增加
                    mOldActionStack.add(removedTool);
                    mUndoStack.remove(0);
                }
                mUndoStack.add(penTool);
            }
        }
        /**
         * 清空所有
         */
        public void clearAll() {
            mRedoStack.clear();
            mUndoStack.clear();
            mOldActionStack.clear();
        }
        /**
         * undo
         */
        public void undo() {
            if (canUndo() && null != mDrawing) {
                CommHandwriting removedTool = mUndoStack
                        .get(mUndoStack.size() - 1);
                mRedoStack.add(removedTool);
                mUndoStack.remove(mUndoStack.size() - 1);
                renewDraw();
            }
        }
        /**
         * redo
         */
        public void redo() {
            if (canRedo() && null != mDrawing) {
                CommHandwriting removedTool = mRedoStack
                        .get(mRedoStack.size() - 1);
                mUndoStack.add(removedTool);
                mRedoStack.remove(mRedoStack.size() - 1);
                renewDraw();
            }
        }

        private void renewDraw(){
            if (null != originalBitmap&&!originalBitmap.isRecycled()) {
                // Set the temporary fore bitmap to canvas.
                // 当载入文件时保存了一份,现在要重新绘制出来
                mDrawing.setTempForeBitmap(mDrawing.originalBitmap);
            } else {
                // 如果背景不存在，则重新创建一份背景
                mDrawing.createCanvasBitmap(mDrawing.drawingBroadWidth,
                        mDrawing.drawingBroadHeight);
            }
            Canvas canvas = mDrawing.commCanvas;
            // First draw the removed tools from undo stack.
            for (CommHandwriting paintTool : mOldActionStack) {
                paintTool.draw(canvas);
            }
            for (CommHandwriting paintTool : mUndoStack) {
                paintTool.draw(canvas);
            }
            mDrawing.invalidate();
        }


        public boolean canUndo() {
            return (mUndoStack.size() > 0);
        }

        public boolean canRedo() {
            return (mRedoStack.size() > 0);
        }

        @Override
        public String toString() {
            return "canUndo" + canUndo();
        }
    }
}
