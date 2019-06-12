# Android画板

画板中支持 画笔类型，橡皮擦，颜色，撤销与反撤销，以及添加图片等功能

效果如下:

<img src="https://github.com/zdc212133/DrawingBoard/blob/master/screenshot/screen.png"/>

核心代码如下：

```
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

```
使用步骤：

由于本例子比较简单，所以就没有打包成一个库，仅提供demo，供需要的自行添加或修改完善，实现自己的需求

+ 1 在布局文件中添加引用
```
    <com.tst.drawbroad.PaintView
        android:id="@+id/paintView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior"
        tools:context=".MainActivity"
        tools:showIn="@layout/activity_main" />
```        

+ 2 在Activity中进行初始化,并添加撤销与反撤销的回调
```
        paintView=findViewById(R.id.paintView);

        paintView.setCallBack(new PaintViewCallBack() {
            @Override
            public void onHasDraw() {

            }

            @Override
            public void onTouchDown() {

            }
        });
        paintView.add();
```
+ 3 功能的实现

```
    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.ib_pencil:
                // 设置为铅笔
                selectPenType(PENCIL);
                break;
            case R.id.ib_pen:
                //设置为钢笔
                selectPenType(PEN);
                break;
            case R.id.ib_rudder:
                //选择橡皮后进行擦除
                selectRudder();
                break;
            case R.id.ib_color:
                selectColors();
                break;
            case R.id.ib_left:
                revokeLeft();
                break;
            case R.id.ib_right:
                revokeRight();
                break;
            case R.id.ib_clear:
                clear();
                break;
        }
    }

    private void selectPenType(int penType){
        paintView.setPenSize(penType);
        paintView.setPenType(PaintConstants.PEN_TYPE.PLAIN_PEN);
    }

    private void selectRudder(){
        paintView.setPenType(PaintConstants.PEN_TYPE.ERASER);
    }

    private void selectColors(){
        colorPickerDialog=new ColorPickerDialog(this, new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void colorChanged(int color) {
                paintView.setPenColor(color);
            }
        }, Color.BLACK);
        colorPickerDialog.show();
    }

    private void revokeLeft(){
        paintView.undo();
    }

    private void revokeRight(){
        paintView.redo();
    }

    private void clear(){
        if (paintView.canRedo()||paintView.canUndo()){
            paintView.clearAll(true);
            paintView.onHasDraw();
        }
    }
```
简单的使用过程如上，如有不了解可以自己下载demo查看源码。
