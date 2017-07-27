package utmg.android_interface.Canvases;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import org.ros.message.Time;

import java.util.ArrayList;

import utmg.android_interface.DataShare;
import utmg.android_interface.QuadUtils.Point3;
import utmg.android_interface.QuadUtils.Quad;
import utmg.android_interface.QuadUtils.Trajectory;

/**
 * Created by James on 7/27/2017.
 */

public class CanvasView extends View {
    Bitmap mBitmap;
    Canvas mCanvas;
    Context context;
    SharedPreferences pref;
    ArrayList<Quad> quads;
    Paint paint;

    ArrayList<Path> paths;
    int mode;
    long tRef;
    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        pref = c.getSharedPreferences("Pref", 0);
        mode = pref.getInt("mode", 0);

        // Paint initialization for trajectories ///////////////////////////////////////////////////
        paint = new Paint();
        if(mode==0) {
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(4f);
        }else{
            // Paint initialization for waypoints //////////////////////////////////////////////////////
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(25);
        }

        quads=(ArrayList<Quad>) DataShare.retrieve("quads");
    }
    // when ACTION_DOWN start touch according to the x,y values
    void startTouch(float x, float y) {

    }

    // when ACTION_MOVE move touch according to the x,y values
    void moveTouch(float x, float y) {

    }

    // when ACTION_UP stop touch
    void upTouch() { //mPath1.lineTo(mX, mY);
    }

    protected void onDraw(Canvas canvas){
        for(int i=0;i<quads.size();i++){
            paint.setColor(quads.get(i).getColor());
            if (mode == 0) {
                canvas.drawPath(paths.get(i), paint);
            }
            else if (mode == 1) {
                //for (int i = 0; i < waypoints.size(); i++) { canvas.drawText(Integer.toString(i+1), waypoints.get(i).getX(), waypoints.get(i).getY(), paint); }
            }
            // TODO: 7/25/2017 draw quad here
        }
    }
    public void clearCanvas() {
        for(int i =0;i<quads.size();i++){
            quads.get(i).clearTrajectory();
            paths.get(i).reset();
        }
        invalidate();
    }
    public void clearQuad(String name){//// TODO: 7/25/2017 implement this

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
    public float getXMeters(float x) {
        float transX = -(x - getCenterX());
        if(mBitmap != null) {
            float normX = transX/mBitmap.getWidth();
            return normX * pref.getFloat("width",5);
        }
        return 0;

    }

    // transform, normalize and scale y to meters
    public float getYMeters(float y) {
        // transY = -mY + getCenterY
        float transY = -(y - getCenterY());
        if(mBitmap != null) {
            float normY = transY/mBitmap.getHeight();
            return normY * pref.getFloat("height",3);
        }
        return 0;

    }
    public Trajectory getMeters(Trajectory old){
        Trajectory inMeters=new Trajectory();
        for(Point3 p: old.getPoints()){
            inMeters.addPoint(new Point3(getXMeters(p.getX()),getYMeters(p.getY()),p.getZ(),p.getTime()));
        }
        return inMeters;
    }

    Time getCurrentTime() {
        long msecs = System.currentTimeMillis();
        //// TODO: 7/25/2017 Use time specific to each quad
        msecs = msecs - tRef;


        Time time = new Time();
        time.secs = (int) (msecs / 1000);
        time.nsecs = (int)((msecs % 1000) * 1000000);

        return time;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }
    public void update(){
        this.draw(mCanvas);
    }

}
