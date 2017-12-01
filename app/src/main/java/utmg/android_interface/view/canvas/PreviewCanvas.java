package utmg.android_interface.view.canvas;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Unknown author. Modified by Tucker Haydon on 10/15/2017.
 */

public class PreviewCanvas extends AbstractCanvas {

//    private List<QuadView> quadViews;

    public PreviewCanvas(
            final Context context) {
        super(context);
    }

    @Override
    public void onDraw(
            final Canvas canvas) {
//        for(QuadView quadView: quadViews) {
//            quadView.drawOptimizedTrajectory(canvas);
//        }
    }

//
//    //sets location of quads to a percentage of the seekbar
//    public void setProgress(int index){
//        for(Quad q: quads){
//            // TODO: 7/31/2017 Wrong, need to scale by times not array length, duh
//            q.setLocation(q.getOptimizedTrajectory().getPoints().get((int)(index/100.0*q.size())));
//        }
//    }
//
//
//
//    //override the onTouchEvent
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // TODO: 7/31/2017 Implement this (came from preview activity)
//        // Touch listener for quad1 - for dragging quad along path
//        // Dragging along path at a certain time will scale the rest of the path as well.
////        quad1.setOnTouchListener(new View.OnTouchListener() {
////            Point DownPT = new Point(); // Record mouse position when pressed down
////            Point StartPT = new Point(); // Record start position of quad1
////
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                int eid = event.getAction();
////                switch (eid) {
////
////                    // when you first press down
////                    case MotionEvent.ACTION_DOWN:
////                        DownPT.x = (int) event.x;
////                        DownPT.y = (int) event.y;
////                        StartPT = new Point((int) quad1.x, (int) quad1.y);
////                        break;
////
////                    // when you move dot
////                    case MotionEvent.ACTION_MOVE:
////                        PointF mv = new PointF(event.x - DownPT.x, event.y - DownPT.y);
////                        quad1.setX((int) (StartPT.x + mv.x));
////                        quad1.setY((int) (StartPT.y + mv.y));
////                        StartPT = new Point((int) quad1.x, (int) quad1.y);
////                        break;
////
////                    // when you pick up pen
////                    case MotionEvent.ACTION_UP:
////                        int t = 0;
////                        double distance = 0;
////                        double leastDistance = 100000000;// needs to be large in order for distance to enter into the loop
////                        int leastDistanceIndex = 0;
////                        double d = 0.0;
////                        float x1 = quad1.x - quad1.getWidth() / 2 - canvasSize.getLeft();
////                        float x2 = x1;
////                        float y2 = 0;
////                        float y1 = quad1.y - quad1.getHeight() / 2;
////                        int p = 0;
////
////                        while (t < xPixelVec1.size() - 1) {
////
////                            x2 = xPixelVec1.get(t);
////                            y2 = yPixelVec1.get(t);
////
////                            d = Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0);// part 1 of the distance formula (x2 - x1)^2 + (y2 - y1)^2
////                            distance = Math.sqrt((float) d);// converts from double to float then does the square root of it
////
////                            if (distance < leastDistance) {// if the distance found is smaller than the greatest
////                                leastDistance = distance;// then it becomes the leastDistance
////                                leastDistanceIndex = t;
////                            }
////                            t++;
////                        }
////                        quad1.setX(xPixelVec1.get(leastDistanceIndex) - quad1.getWidth() / 2 + canvasSize.getLeft());
////                        quad1.setY(yPixelVec1.get(leastDistanceIndex) - quad1.getHeight() / 2);
////                        scaledx1 = xPixelVec1.get(leastDistanceIndex) - quad1.getWidth() / 2 + canvasSize.getLeft();
////                        scaledy1 = yPixelVec1.get(leastDistanceIndex) - quad1.getHeight() / 2;
////                        break;
////                    default:
////                        break;
////                }
////                return true;
////            }
////        });
//
//        //this is how they moved them along the lines
//      //  private void runQuad1() {
////        final Handler updateH1 = new Handler();
////        Runnable updateR1 = new Runnable() {
////            @Override
////            public void run() {
////
////                while (DataShare.getSeekLoc(1) < xPixelVec1.size() - 1) {
////                    // NOTE: CHANGE THIS WAIT TIME IN MS AS DESIRED BY PLAYBACK SPEED
////                    try {
////                        Thread.sleep(100);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                    updateH1.post(new Runnable() {
////                        @Override
////                        public void run() {
////
////                            int value = DataShare.getSeekLoc(1);
////
////                            quad1.setX(xPixelVec1.get(value) - quad1.getWidth() / 2 + canvasSize.getLeft());
////                            quad1.setY(yPixelVec1.get(value) - quad1.getHeight() / 2);
////
////                            if (toggle.isChecked()) {
////                                DataShare.setSeekLoc(1, value + 1);
////                            } else {
////                                DataShare.setPlayBackState(false);
////                            }
////
////                            if (value >= xPixelVec1.size() - 1) {
////                                DataShare.setSeekLoc(1, 0);
////                                DataShare.setPlayBackState(true);
////                            }
////                        }
////                    });
////                }
////            }
////        };
////        new Thread(updateR1).start();
////    }
//        return false;
//    }

}
