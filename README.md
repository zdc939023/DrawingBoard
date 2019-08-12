# Android画板

画板中支持 画笔类型，橡皮擦，颜色，撤销与反撤销，以及添加图片等功能

效果如下:

<p>
<img src="https://github.com/zdc212133/DrawingBoard/blob/master/screenshot/one.png"/>
<img src="https://github.com/zdc212133/DrawingBoard/blob/master/screenshot/two.png"/></p>

使用方法：

第一步

	maven { url 'https://jitpack.io' }
    
第二步

        implementation 'com.gitee.zdcUser:DrawingBoard:1.0.0'

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
