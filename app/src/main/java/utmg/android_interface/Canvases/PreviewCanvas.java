package utmg.android_interface.Canvases;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

import android.content.SharedPreferences;

import org.ros.message.Time;

import utmg.android_interface.model.Point3;


public class PreviewCanvas extends CanvasView {
    private static final float TOLERANCE = 5;
    SharedPreferences pref;
    int mode;
    //used for when we don't want to draw the quad's true position, which is being constantly updated
    // TODO: 7/31/2017 this is a trash solution


    public PreviewCanvas(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        ArrayList<Quad> temps = new ArrayList<>();
        for(Quad q: quads){
            Quad tempQuad=new Quad(q.getName(),q.getTrajectory(),q.getColor());
            tempQuad.setOptimizedTrajectory(q.getOptimizedTrajectory());
            tempQuad.setLocation(tempQuad.getOptimizedTrajectory().getPoints().get(0));
        }
        quads=temps;//this ensures that changes to the preview quads do not affect the real quads,
        // thereby removing the need to override the ondraw method and add an arraylist of locations
        pref = c.getSharedPreferences("Pref", 0);
        mode = pref.getInt("mode", 0);
        for(Quad q: quads){
            Path path=new Path();
            ArrayList<Point3> points = q.getOptimizedTrajectory().getPoints();
            if(points.size()==0)
                continue;
            Point3 startPixels = toPixels(points.get(0));
            path.moveTo(startPixels.getX(),startPixels.getY());
            for(int i=1;i<points.size();i++){
                Point3 lp=toPixels(new Point3(points.get(i-1).getX(),points.get(i-1).getY(),0,new Time()));
                Point3 p =toPixels(points.get(i));
                path.quadTo(lp.getX(),lp.getY(),(p.getX()+lp.getX())/2,(p.getY()+lp.getY())/2);
            }
            paths.add(path);


        }
    }

    // override onSizeChanged



    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void callOnDraw() {
        super.draw(mCanvas);
    }

    //sets location of quads to a percentage of the seekbar
    public void setProgress(int index){
        for(Quad q: quads){
            // TODO: 7/31/2017 Wrong, need to scale by times not array length, duh
            q.setLocation(q.getOptimizedTrajectory().getPoints().get((int)(index/100.0*q.size())));
        }
    }



    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO: 7/31/2017 Implement this (came from preview activity)
        // Touch listener for quad1 - for dragging quad along path
        // Dragging along path at a certain time will scale the rest of the path as well.
//        quad1.setOnTouchListener(new View.OnTouchListener() {
//            Point DownPT = new Point(); // Record mouse position when pressed down
//            Point StartPT = new Point(); // Record start position of quad1
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int eid = event.getAction();
//                switch (eid) {
//
//                    // when you first press down
//                    case MotionEvent.ACTION_DOWN:
//                        DownPT.x = (int) event.getX();
//                        DownPT.y = (int) event.getY();
//                        StartPT = new Point((int) quad1.getX(), (int) quad1.getY());
//                        break;
//
//                    // when you move dot
//                    case MotionEvent.ACTION_MOVE:
//                        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
//                        quad1.setX((int) (StartPT.x + mv.x));
//                        quad1.setY((int) (StartPT.y + mv.y));
//                        StartPT = new Point((int) quad1.getX(), (int) quad1.getY());
//                        break;
//
//                    // when you pick up pen
//                    case MotionEvent.ACTION_UP:
//                        int t = 0;
//                        double distance = 0;
//                        double leastDistance = 100000000;// needs to be large in order for distance to enter into the loop
//                        int leastDistanceIndex = 0;
//                        double d = 0.0;
//                        float x1 = quad1.getX() - quad1.getWidth() / 2 - canvasSize.getLeft();
//                        float x2 = x1;
//                        float y2 = 0;
//                        float y1 = quad1.getY() - quad1.getHeight() / 2;
//                        int p = 0;
//
//                        while (t < xPixelVec1.size() - 1) {
//
//                            x2 = xPixelVec1.get(t);
//                            y2 = yPixelVec1.get(t);
//
//                            d = Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0);// part 1 of the distance formula (x2 - x1)^2 + (y2 - y1)^2
//                            distance = Math.sqrt((float) d);// converts from double to float then does the square root of it
//
//                            if (distance < leastDistance) {// if the distance found is smaller than the greatest
//                                leastDistance = distance;// then it becomes the leastDistance
//                                leastDistanceIndex = t;
//                            }
//                            t++;
//                        }
//                        quad1.setX(xPixelVec1.get(leastDistanceIndex) - quad1.getWidth() / 2 + canvasSize.getLeft());
//                        quad1.setY(yPixelVec1.get(leastDistanceIndex) - quad1.getHeight() / 2);
//                        scaledx1 = xPixelVec1.get(leastDistanceIndex) - quad1.getWidth() / 2 + canvasSize.getLeft();
//                        scaledy1 = yPixelVec1.get(leastDistanceIndex) - quad1.getHeight() / 2;
//                        break;
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });

        //this is how they moved them along the lines
      //  private void runQuad1() {
//        final Handler updateH1 = new Handler();
//        Runnable updateR1 = new Runnable() {
//            @Override
//            public void run() {
//
//                while (DataShare.getSeekLoc(1) < xPixelVec1.size() - 1) {
//                    // NOTE: CHANGE THIS WAIT TIME IN MS AS DESIRED BY PLAYBACK SPEED
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    updateH1.post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            int value = DataShare.getSeekLoc(1);
//
//                            quad1.setX(xPixelVec1.get(value) - quad1.getWidth() / 2 + canvasSize.getLeft());
//                            quad1.setY(yPixelVec1.get(value) - quad1.getHeight() / 2);
//
//                            if (toggle.isChecked()) {
//                                DataShare.setSeekLoc(1, value + 1);
//                            } else {
//                                DataShare.setPlayBackState(false);
//                            }
//
//                            if (value >= xPixelVec1.size() - 1) {
//                                DataShare.setSeekLoc(1, 0);
//                                DataShare.setPlayBackState(true);
//                            }
//                        }
//                    });
//                }
//            }
//        };
//        new Thread(updateR1).start();
//    }
        return false;
    }








}
