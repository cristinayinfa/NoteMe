package com.uoit.noteme.activites;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CanvasDraw extends View {
    private Bitmap bmp;
    private Canvas cvs;
    private int colour = Color.BLACK;
    private float strokeWidth = 8f;
    private Path path = new Path();
    private Paint brush = new Paint();
    private Paint bBrush = new Paint(Paint.DITHER_FLAG);

    float X, Y;

    public Rect rect;

    public CanvasDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        brush.setAntiAlias(true);
        brush.setColor(colour);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(strokeWidth);
        brush.setAlpha(0xff);
    }

    public void init(int height, int width) {
        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        cvs = new Canvas(bmp);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                X = pointX;
                Y = pointY;
                path.moveTo(pointX, pointY);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                break;
            default:
                return false;
        }
        postInvalidate();
        return false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        cvs.drawColor(Color.WHITE);

        brush.setColor(colour);
        brush.setStrokeWidth(strokeWidth);
        cvs.drawPath(path, brush);

        canvas.drawBitmap(bmp, 0, 0, bBrush);
        canvas.restore();
    }

    public Bitmap save() {
        return bmp;
    }

    public void drawRectangle(){
        rect = new Rect();
    }


}
