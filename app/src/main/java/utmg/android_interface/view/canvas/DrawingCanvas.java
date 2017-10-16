package utmg.android_interface.view.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.util.ArrayList;

import utmg.android_interface.DataShare;
import utmg.android_interface.controller.OnTouchEventDispatcher;
import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.model.util.Point3;

/**
 * Unknown creator. Modified by Tucker Haydon on 10/15/2017
 */


public class DrawingCanvas extends AbstractCanvas {

    private int qIndex;
    private ArrayList<Obstacle> obstacles;

    private static final float TOLERANCE = 5;
    
    private long mRef1;

    private Point3 last = Point3.origin();

    public DrawingCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);

        // TODO change colours to holo colours

        //get variables from memory
        quads=(ArrayList<Quad>)DataShare.retrieve("quads");
        obstacles=(ArrayList<Obstacle>)DataShare.retrieve("obstacles");
        //sword=(Sword)DataShare.retrieve("sword");
        qIndex = 0;//// TODO: 7/25/2017 Have this be set to whatever the current spinner says

        this.setOnTouchListener(new OnTouchEventDispatcher(this));
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }


    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {// TODO: 7/27/2017 Set this up so that it doesn't redraw everything each time.
        super.onDraw(canvas);
        for(Obstacle obs: obstacles) {
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

        last.setPoint(x, y, 0);

        super.draw(canvas);
    }

    // when ACTION_MOVE move touch according to the x,y values
    @Override
    void moveTouch(float x1, float y1) {
        if (mode == 0) {
            Point3 p = new Point3(x1, y1, 0, getCurrentTime());

            //int lastIndex = quads.get(qIndex).size() - 1;
            //if (lastIndex < 0) { lastIndex = 0; }
            //Point3 last = quads.get(qIndex).getTrajectory().getPoints().get(lastIndex);

            float delta = p.distanceTo(last);
            if (delta >= TOLERANCE) {
                paths.get(qIndex).quadTo(last.getX(), last.getY(), (x1 + last.getX()) / 2, (y1 + last.getY()) / 2);
                quads.get(qIndex).addPoint(toMeters(p));

                last.setPoint(x1, y1, 0);
                //Log.i("canvasView_touch_input", Float.toString(xMeters()) + "\t" + Float.toString(yMeters()));
            }
        }
    }

    // when ACTION_UP stop touch
    @Override
    void upTouch() {
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
