package com.example.framework.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.framework.R;

public class TouchPictureV extends View {

    private Bitmap bgbitmap;
    private Paint mPaintbg;
    private Bitmap mNullBitmap;
    private Paint mPaintnull;

    private Bitmap mMoveBitmap;
    private Paint mPaintMove;


    private int mWidth;
    private int mHeight;


    private int CARD_SIZE = 200;
    private int LINE_W,LINE_H = 0;

    private int moveX = 200;
    private int errorValues = 10;

    private OnviewResultListener onviewResultListener;

    public void setViewRestultListener(OnviewResultListener viewRestultListener){
        onviewResultListener = viewRestultListener;
    }

    public TouchPictureV(Context context) {
        super(context);
        init();
    }

    public TouchPictureV(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPictureV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintbg = new Paint();
        mPaintMove = new Paint();
        mPaintnull = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawNullCard(canvas);
        drawMoveCard(canvas);
    }

    private void drawMoveCard(Canvas canvas) {
        mMoveBitmap = Bitmap.createBitmap(bgbitmap, LINE_W, LINE_H, CARD_SIZE, CARD_SIZE);

        canvas.drawBitmap(mMoveBitmap,moveX,LINE_H,mPaintMove);

    }

    private void drawNullCard(Canvas canvas) {
        mNullBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.img_null_card);
        CARD_SIZE = mNullBitmap.getWidth();
        LINE_W = mWidth / 3 * 2 ;
        LINE_H = mHeight / 2 - (CARD_SIZE/2);

        canvas.drawBitmap(mNullBitmap,LINE_W,LINE_H,mPaintnull);
    }

    private void drawBg(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_bg);
        bgbitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);

        Canvas bgcanvas = new Canvas(bgbitmap);
        bgcanvas.drawBitmap(bitmap,null,new Rect(0,0,mWidth,mHeight),mPaintbg);

        canvas.drawBitmap(bgbitmap,null,new Rect(0,0,mWidth,mHeight),mPaintbg);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(event.getX() > 0 && event.getX() < (mWidth - CARD_SIZE)){
                    moveX = (int) event.getX();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(moveX > (LINE_W - errorValues) && moveX < (LINE_W + errorValues)){
                    if(onviewResultListener != null){
                        onviewResultListener.onResult();
                        moveX = 200;
                    }
                }
                break;
        }

        return true;
    }

    public interface OnviewResultListener{
        void onResult();
    }
}
