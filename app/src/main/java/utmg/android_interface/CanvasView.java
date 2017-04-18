package utmg.android_interface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class CanvasView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;
    ArrayList<Float> xCoordVec;
    ArrayList<Float> yCoordVec;


    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        // we set a new Path
        mPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public int bWidth() {
        return mBitmap.getWidth();
    }

    public int bHeight() {
        return mBitmap.getHeight();
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        canvas.drawPath(mPath, mPaint);
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {

        mPath.reset();

        // instantiate x and y arrays
        xCoordVec = new ArrayList<>();
        yCoordVec = new ArrayList<>();

        mPath.moveTo(x, y);
        // rotate trajectory -90 degrees
        mX = y;
        mY = -x;

        xCoordVec.add(mX);
        yCoordVec.add(mY);
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            xCoordVec.add(xMeters());
            yCoordVec.add(yMeters());

            Log.i("touch_input", Float.toString(xMeters()) + "\t" + Float.toString(yMeters()));

        }
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        //mPath.lineTo(mX, mY);
        // TODO DO SOMETHING WITH ARRAYS
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                //upTouch();
                invalidate();
                break;
        }
        return true;
    }

    // current ArrayList of x coordinate vectors
    public ArrayList<Float> getxCoordVec() {

        return xCoordVec;
    }

    // current ArrayList of y coordinate vectors
    public ArrayList<Float> getyCoordVec() {

        return yCoordVec;
    }

    // current x coordinate
    public float getxCoord() {

        return mX;
    }

    // current y coordinate
    public float getyCoord() {

        return mY;
    }

    // center x coordinate of bitmap
    public int centerX() {
        if(mBitmap != null) {
            return mBitmap.getWidth() / 2;
        }
        return 0;
    }

    // center y coordinate of bitmap
    public int centerY() {
        if (mBitmap != null) {
            return mBitmap.getHeight()/2;
        }
        return 0;
    }

    // transform, normalize and scale x to meters
    public float xMeters() {
        float transX = mX - centerX();
        if(mBitmap != null) {
            float normX = transX/mBitmap.getWidth();
            return normX * 3;
        }
        return 0;

    }

    // transform, normalize and scale y to meters
    public float yMeters() {
        // transY = -mY + centerY
        float transY = -(mY - centerY());
        if(mBitmap != null) {
            float normY = transY/mBitmap.getHeight();
            return normY * 5;
        }
        return 0;

    }

}
