package utmg.android_interface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import android.content.SharedPreferences;


public class CanvasView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;
    private Paint mPaint;
    private Paint mPaintWP;
    private float mX, mY;
    private static final float TOLERANCE = 5;
    ArrayList<Float> xCoordVec;
    ArrayList<Float> yCoordVec;
    ArrayList<Float> zCoordVec;
    ArrayList<Float> xWaypoint;
    ArrayList<Float> yWaypoint;
    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    int mode;


    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        pref = c.getSharedPreferences("Pref", 0);
        mode = pref.getInt("mode", 0);

        // we set a new Path
        mPath = new Path();

        // and we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);

        // paint for waypoint
        mPaintWP = new Paint();
        mPaintWP.setAntiAlias(true);
        mPaintWP.setStyle(Paint.Style.STROKE);
        mPaintWP.setStrokeWidth(1);
        mPaintWP.setStyle(Paint.Style.FILL);
        mPaintWP.setColor(Color.BLACK);
        mPaintWP.setTextSize(25);

        // instantiate x, y arrays
        xCoordVec = new ArrayList<>();
        yCoordVec = new ArrayList<>();
        zCoordVec = new ArrayList<>();

        xWaypoint = new ArrayList<>();
        yWaypoint = new ArrayList<>();
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }


    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        if (mode == 0) {
            canvas.drawPath(mPath, mPaint);
        }
        else if (mode == 1) {
            for (int i = 0; i < xWaypoint.size(); i++) {

                canvas.drawText(Integer.toString(i+1), xWaypoint.get(i),yWaypoint.get(i), mPaintWP);
            }
        }
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        if (mode == 0) {
            mPath.reset();
            // instantiate x, y arrays
            xCoordVec = new ArrayList<>();
            yCoordVec = new ArrayList<>();
            zCoordVec = new ArrayList<>();
        }

        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        if (mode == 1) {
            xWaypoint.add(mX);
            yWaypoint.add(mY);
            xCoordVec.add(yMeters()); // x-y swap due to screen rotation... or something. I dunno.
            yCoordVec.add(xMeters());
            zCoordVec.add(zObject.getInstance().getZ());
            super.onDraw(mCanvas);
        }

    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        if (mode == 0) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOLERANCE || dy >= TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
                xCoordVec.add(yMeters());
                yCoordVec.add(xMeters());
                zCoordVec.add(zObject.getInstance().getZ());
//                mPaint.setAlpha(  (int)((zObject.getInstance().getZ()/pref.getFloat("newAltitude",2))*255.0)  );
                mPaint.setStrokeWidth(4f);

                Log.i("canvasView_touch_input", Float.toString(xMeters()) + "\t" + Float.toString(yMeters()));
            }
        }
    }

    // when ACTION_UP stop touch
    private void upTouch() { //mPath.lineTo(mX, mY);
    }

    public void clearCanvas() {
        xCoordVec = new ArrayList<>();
        yCoordVec = new ArrayList<>();
        zCoordVec = new ArrayList<>();
        xWaypoint = new ArrayList<>();
        yWaypoint = new ArrayList<>();

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

    // current ArrayList of z coordinate vectors
    public ArrayList<Float> getzCoordVec() {

        return zCoordVec;
    }

    // center x coordinate of bitmap
    public int getCenterX() {
        if(mBitmap != null) {
            return mBitmap.getWidth() / 2;
        }
        return 0;
    }

    // center y coordinate of bitmap
    public int getCenterY() {
        if (mBitmap != null) {
            return mBitmap.getHeight() / 2;
        }
        return 0;
    }

    // transform, normalize and scale x to meters
    public float xMeters() {
        float transX = -(mX - getCenterX());
        if(mBitmap != null) {
            float normX = transX/mBitmap.getWidth();
            return normX * 5;
        }
        return 0;

    }

    // transform, normalize and scale y to meters
    public float yMeters() {
        // transY = -mY + getCenterY
        float transY = -(mY - getCenterY());
        if(mBitmap != null) {
            float normY = transY/mBitmap.getHeight();
            return normY * 3;
        }
        return 0;

    }

}
