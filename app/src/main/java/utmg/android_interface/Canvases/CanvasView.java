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
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.Trajectory;

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

        quads = (ArrayList<Quad>) DataShare.retrieve("quads");

        paths = new ArrayList<>();

        for (Quad qd : quads)
        {
            paths.add(new Path());
        }

        // Paint initialization for trajectories ///////////////////////////////////////////////////
        paint = new Paint();
        if (mode == 0) {
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(4f);
        } else {
            // Paint initialization for waypoints //////////////////////////////////////////////////////
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(25);
        }
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
    public float toMetersX(float x) {// TODO: 7/31/2017 Test these methods for accuracy 
        float transX = -(x - getCenterX());
        if(mBitmap != null) {
            float normX = transX/mBitmap.getWidth();
            return normX * pref.getFloat("width",5);
        }
        return 0;

    }

    // transform, normalize and scale y to meters
    public float toMetersY(float y) {
        // transY = -mY + getCenterY
        float transY = -(y - getCenterY());
        if(mBitmap != null) {
            float normY = transY/mBitmap.getHeight();
            return normY * pref.getFloat("height",3);
        }
        return 0;

    }
    public Point3 toMeters(Point3 p){
        return new Point3(toMetersX(p.getX()),toMetersY(p.getY()),p.getZ(),p.getTime());
    }
    public Trajectory toMeters(Trajectory old){
        Trajectory inMeters=new Trajectory();
        for(Point3 p: old.getPoints()){
            inMeters.addPoint(new Point3(toMetersX(p.getX()),toMetersY(p.getY()),p.getZ(),p.getTime()));
        }
        return inMeters;
    }

    public float toPixelsX(float x) {// TODO: 7/31/2017 Test these methods for accuracy 

        double normX = x / -pref.getFloat("newWidth", 5);
        double transX = normX * getLayoutParams().width;
        return (float)(getX() + getLayoutParams().width / 2 + transX);
    }

    // transform specified object's y position to pixels
    public float toPixelsY(float y) {

        double normY = y / pref.getFloat("newHeight", 3);
        double transY = normY * getLayoutParams().height;
        return (float)(getY() + getLayoutParams().height / 2 - transY);
    }
    //convert from meters to pixels, hopefully
    public Point3 toPixels(Point3 p){
        return new Point3(toPixelsX(p.getX()),toPixelsY(p.getY()),p.getZ(),p.getTime());
    }
    public Trajectory toPixels(Trajectory old){
        Trajectory inPixels = new Trajectory();
        for(Point3 p: old.getPoints()){
            inPixels.addPoint(new Point3(toPixelsX(p.getX()),toPixelsY(p.getY()),p.getZ(),p.getTime()));
        }
        return inPixels;
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
        //this.draw(mCanvas);
    }

}
