package com.tst.drawbroad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tst.drawbroad.comm.CommCanvas;
import com.tst.drawbroad.comm.CommHandwriting;
import com.tst.drawbroad.comm.CommUndo;
import com.tst.drawbroad.comm.CommUndoStack;
import com.tst.drawbroad.comm.PaintConstants;
import com.tst.drawbroad.comm.PaintViewCallBack;
import com.tst.drawbroad.pens.BlurPen;
import com.tst.drawbroad.pens.ClearAll;
import com.tst.drawbroad.pens.EmbossPen;
import com.tst.drawbroad.pens.Eraser;
import com.tst.drawbroad.pens.PlainPen;
import com.tst.drawbroad.pens.Shapable;
import com.tst.drawbroad.pens.ShapesInterface;
import com.tst.drawbroad.shapes.Circle;
import com.tst.drawbroad.shapes.Curv;
import com.tst.drawbroad.shapes.Line;
import com.tst.drawbroad.shapes.Oval;
import com.tst.drawbroad.shapes.Rectangle;
import com.tst.drawbroad.shapes.Square;
import com.tst.drawbroad.tools.BitmapUtils;

import static com.tst.drawbroad.comm.PaintConstants.UNDO_STACK_SIZE;

public class PaintView extends View implements CommUndo,Cloneable {

    private String TAG="PaintView";
    private CommCanvas commCanvas=null;
    /**
     * 橡皮擦的画笔
     */
    private Paint eraserPaint=null;
    /**
     * 画图的画笔
     */
    private Paint editPaint=null;
    private CommHandwriting handwriting;
    private ShapesInterface mCurrentShape = null;
    /**
     * editBitmap 用户当前绘制编辑使用
     * originalBitmap 为初始化加载时的bitmap，主要用户撤销与反撤销时重新绘制使用
     */
    private Bitmap editBitmap=null,originalBitmap=null;
    private CommUndoStack undoStack=null;
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

    public CommCanvas getCommCanvas(){
        return commCanvas;
    }

    public Bitmap getOriginalBitmap(){
        return originalBitmap;
    }
    public int getDrawingBroadWidth(){
        return drawingBroadWidth;
    }
    public int getDrawingBroadHeight(){
        return drawingBroadHeight;
    }

    public void setCallBack(PaintViewCallBack mCallBack){
        this.mCallBack=mCallBack;
    }

    public PaintView(Context context) {
        this(context,null);
    }
    public PaintView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }
    public PaintView(Context context,AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        init();
    }

    private void init(){
        commCanvas=new CommCanvas();
        editPaint=new Paint(Paint.DITHER_FLAG);
        undoStack=new CommUndoStack(this,mStackedSize);
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
            BitmapUtils.destroyBitmap(editBitmap);
            editBitmap=BitmapUtils.duplicateBitmap(tempForeBitmap);
            if (editBitmap!=null&&commCanvas!=null){
                commCanvas.setBitmap(editBitmap);
                invalidate();
            }
        }
    }

    /**
     * 创建bitMap同时获得其canvas
     */
    public void createCanvasBitmap(int w, int h) {
        if(w>0 || h>0){
            editBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            commCanvas.setBitmap(editBitmap);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (editBitmap!=null&&!editBitmap.isRecycled())
            canvas.drawBitmap(editBitmap,0,0,editPaint);
            if (paintType!= PaintConstants.PEN_TYPE.ERASER){
                handwriting.draw(canvas);
            }else{
                canvas.drawCircle(currentX,currentY,mEraserSize/2,eraserPaint);
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
                    undoStack.push(handwriting);
                    if (mCallBack!=null){
                        mCallBack.onHasDraw();
                    }
                }
                up(event.getX(),event.getY());
                invalidate();
                break;
        }
        return true;
    }

    private void down(float x,float y){
        commCanvas.setBitmap(editBitmap);
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

    /**
     * 添加新笔记
     */
    public void add(){
        clearAll(false);
        handwriting.touchDown(currentX,currentY);
        resetHandwriting();
    }

    public void onHasDraw(){
        if (mCallBack!=null){
            mCallBack.onHasDraw();
        }
    }

    /**
     * 清除所有笔记
     * @param isUndo
     */
    public void clearAll(boolean isUndo){
        BitmapUtils.destroyBitmap(editBitmap);
        BitmapUtils.destroyBitmap(originalBitmap);
        if (isUndo){
            undoStack.push(new ClearAll());
        }else{
            undoStack.clearAll();
        }
        createCanvasBitmap(drawingBroadWidth,drawingBroadHeight);
        invalidate();
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
        editBitmap=BitmapUtils.duplicateBitmap(bitmap);
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
     * 清除笔迹
     */
    private void resetHandwriting() {
        undoStack.clearAll();
    }

    @Override
    public void undo() {
        if (undoStack!=null) undoStack.undo();
    }

    @Override
    public void redo() {
        if (undoStack!=null) undoStack.redo();
    }

    @Override
    public boolean canUndo() {
        return undoStack!=null&&undoStack.canUndo();
    }

    @Override
    public boolean canRedo() {
        return undoStack!=null&&undoStack.canRedo();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BitmapUtils.destroyBitmap(editBitmap);
        BitmapUtils.destroyBitmap(originalBitmap);
    }
}
