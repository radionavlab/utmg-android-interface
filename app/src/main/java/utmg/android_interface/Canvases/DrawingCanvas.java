package utmg.android_interface.Canvases;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import java.util.ArrayList;

import utmg.android_interface.DataShare;
import utmg.android_interface.QuadUtils.Obstacle;
import utmg.android_interface.QuadUtils.Point3;
import utmg.android_interface.QuadUtils.Quad;
import utmg.android_interface.QuadUtils.Sword;

public class DrawingCanvas extends CanvasView {//// TODO: 7/25/2017 add multiquad, add obstacles

    private int qIndex;
    private ArrayList<Obstacle> obstacles;
    private Sword sword;

    private static final float TOLERANCE = 5;
    
    private long mRef1;




    public DrawingCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);

        // TODO change colours to holo colours




        
        //get variables from memory
        quads=(ArrayList<Quad>)DataShare.retrieve("quads");
        obstacles=(ArrayList<Obstacle>)DataShare.retrieve("obstacles");
        sword=(Sword)DataShare.retrieve("sword");
        qIndex = 0;//// TODO: 7/25/2017 Have this be set to whatever the current spinner says
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
    protected void onDraw(Canvas canvas) {// TODO: 7/27/2017 Set this up so that it doesn't redraw everything each time.
        super.onDraw(canvas);
        for(Obstacle obs: obstacles){
            // TODO: 7/25/2017 draw obstacles here
        }
        // TODO: 7/25/2017 draw sword here
    }

    // when ACTION_DOWN start touch according to the x,y values
    @Override
    void startTouch(float x, float y) {
        if (mode == 0) {

            //TODO: check which quad is the active one and edit that
            paths.get(qIndex).reset();
            quads.get(qIndex).clearTrajectory();
            mRef1 = System.currentTimeMillis();
            paths.get(qIndex).moveTo(x, y);
        }

        if (mode == 1) {
            //waypoints.add(new Point3(x,y,0));
        }
        super.draw(mCanvas);
    }

    // when ACTION_MOVE move touch according to the x,y values
    @Override
    void moveTouch(float x1, float y1) {
        if (mode == 0) {
            Point3 p = new Point3(x1,y1,0,getCurrentTime());
            Point3 last = quads.get(qIndex).getTrajectory().getPoints().get(quads.get(qIndex).size()-1);
            float delta=p.distanceTo(last);
            if (delta >= TOLERANCE) {
                paths.get(qIndex).quadTo(last.getX(), last.getY(), (x1 + last.getX()) / 2, (y1 + last.getY()) / 2);
                quads.get(qIndex).addPoint(toMeters(p));
                //Log.i("canvasView_touch_input", Float.toString(xMeters()) + "\t" + Float.toString(yMeters()));
            }
        }
    }

    // when ACTION_UP stop touch
    @Override
    void upTouch() {
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

    public void setQuad(String name){
        for(int i=0;i<quads.size();i++){
            if(quads.get(i).getName().equals(name)){
                qIndex=i;
                break;
            }
        }
    }



}
