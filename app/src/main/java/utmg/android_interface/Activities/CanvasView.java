package utmg.android_interface.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import android.content.SharedPreferences;
import org.ros.message.Time;

import utmg.android_interface.DataShare;
import utmg.android_interface.QuadUtils.Obstacle;
import utmg.android_interface.QuadUtils.Point3;
import utmg.android_interface.QuadUtils.Quad;
import utmg.android_interface.QuadUtils.Sword;

public class CanvasView extends View {//// TODO: 7/25/2017 add multiquad, add obstacles 

    private Bitmap mBitmap;
    private Canvas mCanvas;
    Context context;

    private Path path;
    private Paint paint;

    private int qIndex;

    private ArrayList<Quad> quads;
    private ArrayList<Obstacle> obstacles;
    private Sword sword;

    private static final float TOLERANCE = 5;

    ArrayList<Point3> waypoints, coordVec, pixelVec;
    
    private long mRef1;

    SharedPreferences pref;
    int mode;


    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        pref = c.getSharedPreferences("Pref", 0);
        mode = pref.getInt("mode", 0);

        // Instantiate variables
        path=new Path();
        waypoints=new ArrayList<>();
        coordVec=new ArrayList<>();
        pixelVec=new ArrayList<>();

        // TODO change colours to holo colours

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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(quads.get(qIndex).getColor());
        if (mode == 0) {
            canvas.drawPath(path, paint);
        }
        else if (mode == 1) {
            for (int i = 0; i < waypoints.size(); i++) { canvas.drawText(Integer.toString(i+1), waypoints.get(i).getX(), waypoints.get(i).getY(), paint); }
        }
        for(Quad q: quads){
            // TODO: 7/25/2017 draw quad here
        }
        for(Obstacle obs: obstacles){
            // TODO: 7/25/2017 draw obstacles here
        }
        // TODO: 7/25/2017 draw sword here
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        if (mode == 0) {

            //TODO: check which quad is the active one and edit that
            path.reset();
            coordVec.clear();
            waypoints.clear();
            pixelVec.clear();
            mRef1 = System.currentTimeMillis();
            path.moveTo(x, y);
        }

        if (mode == 1) {
            waypoints.add(new Point3(x,y,0));
            coordVec.add(new Point3(getYMeters(y),getXMeters(x),0));
        }
        super.draw(mCanvas);
    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x1, float y1) {
        if (mode == 0) {
            float x0=pixelVec.get(pixelVec.size()-1).getX();
            float y0=pixelVec.get(pixelVec.size()-1).getY();
            float dx = Math.abs(x1 - x0);
            float dy = Math.abs(y1 - y0);
            if (dx >= TOLERANCE || dy >= TOLERANCE) {
                
                path.quadTo(x0, y0, (x1 + x0) / 2, (y1 + y0) / 2);
                coordVec.add(new Point3(getYMeters(y1),getXMeters(x1),0));
                pixelVec.add(new Point3(x1,y1,0,getCurrentTime()));
                //Log.i("canvasView_touch_input", Float.toString(xMeters()) + "\t" + Float.toString(yMeters()));
            }
        }
    }

    // when ACTION_UP stop touch
    private void upTouch() { //mPath1.lineTo(mX, mY);
    }

    public void clearCanvas() {//// TODO: 7/25/2017 should clear individual quads too, perhaps 
        coordVec.clear();
        pixelVec.clear();
        waypoints.clear();

        path.reset();
        invalidate();
    }
    public void clearQuad(String name){//// TODO: 7/25/2017 implement this 
        
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

    Time getCurrentTime() {
        long msecs = System.currentTimeMillis();
        //// TODO: 7/25/2017 Use time specific to each quad 
        msecs = msecs - mRef1;
        

        Time time = new Time();
        time.secs = (int) (msecs / 1000);
        time.nsecs = (int)((msecs % 1000) * 1000000);

        return time;
    }
    public void setQuad(String name){
        for(int i=0;i<quads.size();i++){
            if(quads.get(i).getName().equals(name)){
                qIndex=i;
                break;
            }
        }
    }
    public void update(){
        this.draw(mCanvas);
    }


}
