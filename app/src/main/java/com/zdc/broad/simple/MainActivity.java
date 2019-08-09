package com.zdc.broad.simple;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

//import com.tst.drawbroad.R;
import com.zdc.broad.Drawing;
import com.zdc.broad.comm.PaintConstants;
import com.zdc.broad.comm.PaintViewCallBack;
import com.zdc.broad.widget.ColorPickerDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG=this.getClass().getSimpleName();

    private final int PEN=PaintConstants.PEN_SIZE.SIZE_2;
    private final int PENCIL=PaintConstants.PEN_SIZE.SIZE_1 ;
    Drawing paintView;
    ImageButton ib_pencil,ib_pen,ib_rudder,ib_color,ib_left,ib_right,ib_clear;
    ColorPickerDialog colorPickerDialog;
    FloatingActionButton fabAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
//        paintView=findViewById(R.id.paintView);
//        ib_pencil=findViewById(R.id.ib_pencil);
//        ib_pencil.setOnClickListener(this);
//        ib_pen=findViewById(R.id.ib_pen);
//        ib_pen.setOnClickListener(this);
//        ib_rudder=findViewById(R.id.ib_rudder);
//        ib_rudder.setOnClickListener(this);
//        ib_color=findViewById(R.id.ib_color);
//        ib_color.setOnClickListener(this);
//        ib_left=findViewById(R.id.ib_left);
//        ib_left.setOnClickListener(this);
//        ib_right=findViewById(R.id.ib_right);
//        ib_right.setOnClickListener(this);
//        ib_clear=findViewById(R.id.ib_clear);
//        ib_clear.setOnClickListener(this);
//        fabAdd=findViewById(R.id.fab_add);
//        fabAdd.setOnClickListener(this);
//        paintView.setCallBack(new PaintViewCallBack() {
//            @Override
//            public void onHasDraw() {
//
//            }
//
//            @Override
//            public void onTouchDown() {
//
//            }
//        });
        paintView.addNewDrawing();
        paintView.setBackground(null);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
//        switch (id){
//            case R.id.ib_pencil:
//                selectPenType(PENCIL);
//                break;
//            case R.id.ib_pen:
//                selectPenType(PEN);
//                break;
//            case R.id.ib_rudder:
//                selectRudder();
//                break;
//            case R.id.ib_color:
//                selectColors();
//                break;
//            case R.id.ib_left:
//                revokeLeft();
//                break;
//            case R.id.ib_right:
//                revokeRight();
//                break;
//            case R.id.ib_clear:
//                clear();
//                break;
//            case R.id.fab_add:
//                addDraft();
//                break;
//        }
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

    private void addDraft(){
        paintView.addNewDrawing();
        paintView.setBackground(null);
        paintView.onHasDraw();

    }
}
