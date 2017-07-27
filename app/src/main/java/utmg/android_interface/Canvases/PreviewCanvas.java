package utmg.android_interface.Canvases;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Date;

import android.content.SharedPreferences;

import org.ros.message.Time;

import utmg.android_interface.DataShare;
import utmg.android_interface.QuadUtils.Quad;


public class PreviewCanvas extends CanvasView {



//    private Path mPath1;
//    private Path mPath2;
//    private Path mPath3;
//
//    private Paint mPaint1;
//    private Paint mPaint2;
//    private Paint mPaint3;
    private static final float TOLERANCE = 5;



    SharedPreferences pref;
    int mode;


    public PreviewCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        pref = c.getSharedPreferences("Pref", 0);
        mode = pref.getInt("mode", 0);

        // instantiate x, y arrays

        // TODO: 7/27/2017 Convert optimized arrays to pixels, then create paths out of them
    }

    // override onSizeChanged



    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath1 with the mPaint1 on the canvas when onDraw
        if (mode == 0) {
            if (DataShare.getPath(1) != null) {
                canvas.drawPath(DataShare.getPath(1), DataShare.getPaint(1));
                Log.i("PreviewCanvas", "Graphics Path 1 drawn.");
            }
            if (DataShare.getPath(2) != null) {
                canvas.drawPath(DataShare.getPath(2), DataShare.getPaint(2));
                Log.i("PreviewCanvas", "Graphics Path 2 drawn.");
            }
            if (DataShare.getPath(3) != null) {
                canvas.drawPath(DataShare.getPath(3), DataShare.getPaint(3));
                Log.i("PreviewCanvas", "Graphics Path 3 drawn.");
            }
        }
//        else if (mode == 1) {
//
//
//            for (int i = 0; i < xWaypoint1.size(); i++) { canvas.drawText(Integer.toString(i+1), xWaypoint1.get(i), yWaypoint1.get(i), mPaintWP1); }
//            for (int i = 0; i < xWaypoint2.size(); i++) { canvas.drawText(Integer.toString(i+1), xWaypoint2.get(i), yWaypoint2.get(i), mPaintWP2); }
//            for (int i = 0; i < xWaypoint3.size(); i++) { canvas.drawText(Integer.toString(i+1), xWaypoint3.get(i), yWaypoint3.get(i), mPaintWP3); }
//
//        }
    }

    public void callOnDraw() {
        super.draw(mCanvas);
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {

    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {

    }

    // when ACTION_UP stop touch
    private void upTouch() { //mPath1.lineTo(mX, mY);
    }

    public void clearCanvas() {
        xCoordVec1 = new ArrayList<>();
        yCoordVec1 = new ArrayList<>();
        zCoordVec1 = new ArrayList<>();
        xWaypoint1 = new ArrayList<>();
        yWaypoint1 = new ArrayList<>();

        xCoordVec2 = new ArrayList<>();
        yCoordVec2 = new ArrayList<>();
        zCoordVec2 = new ArrayList<>();
        xWaypoint2 = new ArrayList<>();
        yWaypoint2 = new ArrayList<>();

        xCoordVec3 = new ArrayList<>();
        yCoordVec3 = new ArrayList<>();
        zCoordVec3 = new ArrayList<>();
        xWaypoint3 = new ArrayList<>();
        yWaypoint3 = new ArrayList<>();

        timesVec1 = new ArrayList<>();
        timesVec2 = new ArrayList<>();
        timesVec3 = new ArrayList<>();

//        mPath1.reset();
//        mPath2.reset();
//        mPath3.reset();
        invalidate();
    }

    public void clearCanvasQuad1() {
        xCoordVec1 = new ArrayList<>();
        yCoordVec1 = new ArrayList<>();
        zCoordVec1 = new ArrayList<>();
        xWaypoint1 = new ArrayList<>();
        yWaypoint1 = new ArrayList<>();
        timesVec1 = new ArrayList<>();
//        mPath1.reset();
        invalidate();
    }

    public void clearCanvasQuad2() {
        xCoordVec2 = new ArrayList<>();
        yCoordVec2 = new ArrayList<>();
        zCoordVec2 = new ArrayList<>();
        xWaypoint2 = new ArrayList<>();
        yWaypoint2 = new ArrayList<>();
        timesVec2 = new ArrayList<>();
//        mPath2.reset();
        invalidate();
    }

    public void clearCanvasQuad3() {
        xCoordVec3 = new ArrayList<>();
        yCoordVec3 = new ArrayList<>();
        zCoordVec3 = new ArrayList<>();
        xWaypoint3 = new ArrayList<>();
        yWaypoint3 = new ArrayList<>();
        timesVec3 = new ArrayList<>();
//        mPath3.reset();
        invalidate();
    }


    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    // current ArrayList of coordinate vectors for quad1
    public ArrayList<Float> getxCoordVec1() { return xCoordVec1; }
    public ArrayList<Float> getyCoordVec1() { return yCoordVec1; }
    public ArrayList<Float> getzCoordVec1() { return zCoordVec1; }

    // current ArrayList of coordinate vectors for quad2
    public ArrayList<Float> getxCoordVec2() { return xCoordVec2; }
    public ArrayList<Float> getyCoordVec2() { return yCoordVec2; }
    public ArrayList<Float> getzCoordVec2() { return zCoordVec2; }

    // current ArrayList of coordinate vectors for quad3
    public ArrayList<Float> getxCoordVec3() { return xCoordVec3; }
    public ArrayList<Float> getyCoordVec3() { return yCoordVec3; }
    public ArrayList<Float> getzCoordVec3() { return zCoordVec3; }

    // current ArrayList of pixel vectors for quad1
    public ArrayList<Float> getxPixelVec1() { return xPixelVec1; }
    public ArrayList<Float> getyPixelVec1() { return yPixelVec2; }
    public ArrayList<Float> getzPixelVec1() { return zPixelVec1; }

    // current ArrayList of pixel vectors for quad2
    public ArrayList<Float> getxPixelVec2() { return xPixelVec2; }
    public ArrayList<Float> getyPixelVec2() { return yPixelVec2; }
    public ArrayList<Float> getzPixelVec2() { return zPixelVec2; }

    // current ArrayList of pixel vectors for quad3
    public ArrayList<Float> getxPixelVec3() { return xPixelVec3; }
    public ArrayList<Float> getyPixelVec3() { return yPixelVec3; }
    public ArrayList<Float> getzPixelVec3() { return zPixelVec3; }

    // current ArrayList of time vectors
    public ArrayList<Time> getTimesVec1() { return timesVec1; }
    public ArrayList<Time> getTimesVec2() { return timesVec2; }
    public ArrayList<Time> getTimesVec3() { return timesVec3; }






}
