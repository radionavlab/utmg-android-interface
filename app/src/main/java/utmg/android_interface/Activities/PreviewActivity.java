package utmg.android_interface;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import java.lang.reflect.Array;
import java.util.ArrayList;

import utmg.android_interface.Canvases.PreviewCanvas;

public class PreviewActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    private PreviewCanvas previewCanvas;
    private LinearLayout canvasSize;
    private RelativeLayout seekbarRelative;
    private int screenHeight;
    private int screenWidth;



    private SeekBar quadAllSeek;
    private ToggleButton toggle;
    private int togglei = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setupActionBar();

        // Button to terminate app so it doesn't have to be done manually
        com.getbase.floatingactionbutton.FloatingActionButton compressed_points = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.compressed_points);
        com.getbase.floatingactionbutton.FloatingActionButton original_points = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.orignal_points);
        com.getbase.floatingactionbutton.FloatingActionButton terminate_preview = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.terminate_preview);
        com.getbase.floatingactionbutton.FloatingActionButton sendAirSim = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendToAirSim);
        com.getbase.floatingactionbutton.FloatingActionButton scalePath = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.scalePath);
        terminate_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.exit(0);
                finish();
            }
        });

        sendAirSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO in order to get a simulation in AirSim we need to send the x,y,z and time arrays of the quad to AirSim
                // right now only one quad works in AirSim
            }
        });

        scalePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scaling(1);
                //scaling(2);
                //scaling(3);
            }
        });

        // compressed_points/original_points used to visualize/compare points between user input, compressed, and minimum snap
        compressed_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // plot points of compressed array on canvas

            }
        });
        original_points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // plot path of original user input
            }
        });
        //Log.i("PreviewActivity", "PreviewActivity started.");



        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        // intializing canvas
        canvasSize = (LinearLayout) findViewById(R.id.linLay);

        // getting screen size
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        // set current state of quad at 0
        DataShare.setSeekLoc(1, 0);
        DataShare.setSeekLoc(2, 0);
        DataShare.setSeekLoc(3, 0);


        // boolean playBackState keeps track whether or not PreviewActivity is ready to start
        // playback. It prevents the play/pause button from replaying from the beginning
        // concurrently if the user only paused the playback.
        DataShare.setPlayBackState(true);

        // initializing quadAllSeek
        seekbarRelative = (RelativeLayout) findViewById(R.id.seekbar_relative);
        quadAllSeek = (SeekBar) findViewById(R.id.quadAllSeek);

        // seekbarRelative is the container view for the bottom seekbar for playback.
        // The listener is implemented such that PreviewActivity first waits for the seekbarRelative
        // to render first, then adjust the size of the canvas to fit the screen properly.
        // The listener is then attempted to be removed, as it is not needed anymore.
        seekbarRelative.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {

                // read height with myLinearLayout.getHeight() etc.
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) canvasSize.getLayoutParams();
                params.height = (int) (screenHeight - seekbarRelative.getHeight() - (getSupportActionBar().getHeight() * 1.5));
                params.width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5) / pref.getFloat("newHeight", 3)));
                canvasSize.setLayoutParams(params);

                // remember to remove the listener if possible
                ViewTreeObserver viewTreeObserver = seekbarRelative.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });

        // The "serviceToggle" from the preferences stores whether or not the app should attempt to
        // interface with the service.
        // true -> contact service; false -> don't contact service
        // convertROSPathToPixelVec is only called if "serviceToggle" is true
        // TODO if path planner servicing is enabled, redo the path
        if (pref.getBoolean("serviceToggle", false)) {
            // TODO
            // the checks are incorrect; they prevent convertROSPathToPixelVec from being called
            // reenable checks once they are correctly figured out

            // calls convertROSPathToPixel - self explanatory
//            if(xPixelVec1.size() != 0) {
            Log.i("PreviewActivity", "Calling convertROSPathToPixelVec for quad 111");
            convertROSPathToPixelVec(1, DataShare.getServicedPath(1));
            path1 = DataShare.getPath(1);
//            }
//            if(xPixelVec2.size() != 0) {
            Log.i("PreviewActivity", "Calling convertROSPathToPixelVec for quad 222");
            convertROSPathToPixelVec(2, DataShare.getServicedPath(2));
            path2 = DataShare.getPath(2);
//            }
//            if(xPixelVec3.size() != 0) {
//                convertROSPathToPixelVec(3, DataShare.getServicedPath(3));
//            }
            Log.i("PreviewActivity", "Calling convertROSPathToPixelVec for quad 333");
            convertROSPathToPixelVec(3, DataShare.getServicedPath(3));
            path3 = DataShare.getPath(3);
        }

        float mainWidth = DataShare.getMainCanvasWidth();
        float mainHeight = DataShare.getMainCanvasHeight();
        float previewHeight = canvasSize.getLayoutParams().height;
        float previewWidth = canvasSize.getLayoutParams().width;
        float scaledWidth = mainWidth / previewWidth - .147482f;
        float scaledHeight = mainHeight / previewHeight - .147482f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaledWidth,scaledHeight);

        // TODO enable scaling once conflict of Paths is figured out
        if(path1 != null){
            path1.transform(scaleMatrix);
        }
        if(path2 != null){
            path2.transform(scaleMatrix);
        }
        if(path3 != null){
            path3.transform(scaleMatrix);
        }
        // transforming arrays for quad1
        if (xPixelVec1 != null) {

            // transforming x's
            float tempx1 = 0;
            for (int i = 0; i < xPixelVec1.size() - 1; i++) {
                tempx1 = xPixelVec1.get(i);
                tempx1 = tempx1 * scaledWidth;
                xPixelVec1Scaled.add(i, tempx1);
            }
            xPixelVec1 = xPixelVec1Scaled;
            DataShare.setXPixelVec(1, xPixelVec1Scaled);

            // transforming y's
            float tempy1 = 0;
            for (int i = 0; i < yPixelVec1.size() - 1; i++) {
                tempy1 = yPixelVec1.get(i);
                tempy1 = tempy1 * scaledHeight;
                yPixelVec1Scaled.add(i, tempy1);
            }
            yPixelVec1 = yPixelVec1Scaled;
            DataShare.setYPixelVec(1, yPixelVec1Scaled);
        }

        // TODO
        // PreviewCanvas should be initialised only AFTER convertROSPathToPixelVec is called
        // because the Android graphics path objects are generated in there! Otherwise, it will only
        // render the original paths.
        previewCanvas = (PreviewCanvas) findViewById(R.id.preview_canvas);
        previewCanvas.callOnDraw();
        
        final Handler handlerQuad = new Handler();
        Runnable runnableQuad = new Runnable() {
            @Override
            public void run() {
                // quad 1
                previewCanvas.update();
                handlerQuad.postDelayed(this, 20);
            }
        };
        runnableQuad.run();

        // TODO: 7/27/2017 Implement this in PreviewCanvas 
        // Touch listener for quad1 - for dragging quad along path
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

        

        // play/pause toggle button
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        Runnable quadToggle = new Runnable() {
            @Override
            public void run() {
                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked && DataShare.getPlayBackState()) {
                            togglei = 1; // will allow it to change rather than stay at starting position
                            if (xPixelVec1 != null) {
                                runQuad1();
                                seekAll();
                            }

                            if (xPixelVec2 != null) {
                                runQuad2();
                                seekAll();
                            }

                            if (xPixelVec3 != null) {
                                runQuad3();
                                seekAll();
                            }

                        } else if (!isChecked && DataShare.getPlayBackState()) {
                            // SIDDHARTH HELP! - does this listener need to be inside the toggle listener?
                            quadAllSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {
                                }

                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    // TODO fix logic
                                    int index = quadAllSeek.getProgress();
                                    if (toggle.isChecked() == false) {
                                        if (xPixelVec1.isEmpty() == false) {

                                            if (index == xPixelVec1.size()) {
                                                // This will handle the error come back to this

                                            } else if (index > xPixelVec1.size()) {
                                                quad1.setX(xPixelVec1.get(xPixelVec1.size()-1) - quad1.getWidth() / 2+ canvasSize.getLeft());
                                                quad1.setY(yPixelVec1.get(yPixelVec1.size()-1) - quad1.getHeight() / 2);

                                            } else {
                                                quad1.setX(xPixelVec1.get(index) - quad1.getWidth() / 2 + canvasSize.getLeft());
                                                quad1.setY(yPixelVec1.get(index) - quad1.getHeight() / 2);
                                            }
                                        }

                                        if (xPixelVec2.isEmpty() == false) {
                                            if (index == xPixelVec2.size()) {
                                                // This will handle the error

                                            } else if (index > xPixelVec2.size()) {
                                                quad2.setX(xPixelVec2.get(xPixelVec2.size()-1) - quad2.getWidth() / 2+ canvasSize.getLeft());
                                                quad2.setY(yPixelVec2.get(yPixelVec2.size()-1) - quad2.getHeight() / 2);

                                            } else {
                                                quad2.setX(xPixelVec2.get(index) - quad2.getWidth() / 2 + canvasSize.getLeft());
                                                quad2.setY(yPixelVec2.get(index) - quad2.getHeight() / 2);
                                            }
                                        }

                                        if (xPixelVec3.isEmpty() == false) {
                                            if (index == xPixelVec3.size()) {
                                                // This will handle the error

                                            } else if (index > xPixelVec3.size()) {
                                                quad3.setX(xPixelVec3.get(xPixelVec3.size()-1) - quad3.getWidth() / 2+ canvasSize.getLeft());
                                                quad3.setY(yPixelVec3.get(yPixelVec3.size()-1) - quad3.getHeight() / 2);

                                            } else {
                                                quad3.setX(xPixelVec3.get(index) - quad3.getWidth() / 2 + canvasSize.getLeft());
                                                quad3.setY(yPixelVec3.get(index) - quad3.getHeight() / 2);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        };
        quadToggle.run();
    }

    // run quad1 on play along the path
    private void runQuad1() {
        final Handler updateH1 = new Handler();
        Runnable updateR1 = new Runnable() {
            @Override
            public void run() {

                while (DataShare.getSeekLoc(1) < xPixelVec1.size() - 1) {
                    // NOTE: CHANGE THIS WAIT TIME IN MS AS DESIRED BY PLAYBACK SPEED
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateH1.post(new Runnable() {
                        @Override
                        public void run() {

                            int value = DataShare.getSeekLoc(1);

                            quad1.setX(xPixelVec1.get(value) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad1.setY(yPixelVec1.get(value) - quad1.getHeight() / 2);

                            if (toggle.isChecked()) {
                                DataShare.setSeekLoc(1, value + 1);
                            } else {
                                DataShare.setPlayBackState(false);
                            }

                            if (value >= xPixelVec1.size() - 1) {
                                DataShare.setSeekLoc(1, 0);
                                DataShare.setPlayBackState(true);
                            }
                        }
                    });
                }
            }
        };
        new Thread(updateR1).start();
    }

    private void seekAll() {
        // set seekbar max equal to the longest quad length - should equal longest time
        quadAllSeek.setMax(Math.max(Math.max(xPixelVec1.size(), xPixelVec2.size()), xPixelVec3.size()));
        //quadAllSeek.setMax(xPixelVec1.size());


        //looks like it constantly updates the quadAllSeek value by looping and checking the value from getSeekLoc
        final Handler seekH = new Handler();
        Runnable seekR = new Runnable() {
            @Override
            public void run() {

                // get longest quad and store it into quadMax
                if (xPixelVec1.size() == quadAllSeek.getMax()) {
                    quadMax = 1;
                    while (quadMax < quadAllSeek.getMax()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        int value = DataShare.getSeekLoc(quadMax);
                        if (value != 0) {
                            quadAllSeek.setProgress(value);
                        }
                        if (value == quadAllSeek.getMax() - 1) {
                            quadAllSeek.setProgress(quadAllSeek.getMax());
                            toggle.setChecked(false);
                        }

                    }
                }
            }
        };
        new Thread(seekR).start();
    }

    // convert nav_msgs/Path to ArrayList of pixels
    private void convertROSPathToPixelVec(int k, nav_msgs.Path p) {

        if (p == null) {
            Log.i("PreviewActivity", "Received null nav_msgs/Path.");
        } else {
            Log.i("PreviewActivity", "Received valid nav_msgs/Path. Size: " + Double.toString(p.getPoses().size()));

            android.graphics.Path mPath1 = new android.graphics.Path();

            for (int i = 0; i < p.getPoses().size(); i++) {

                double xMeter = p.getPoses().get(i).getPose().getPosition().getX();
                double yMeter = p.getPoses().get(i).getPose().getPosition().getY();

                xMeterVec1.add(xMeter);
                yMeterVec1.add(yMeter);

                //Log.i("PreviewActivity","xMeter: " + Double.toString(xMeter) + "\t yMeter: " + Double.toString(yMeter) + " # " + Integer.toString(i + 1));

                float xPixel = (float) xMeterToPixel(xMeter, yMeter);
                float yPixel = (float) yMeterToPixel(xMeter, yMeter);

                xPixelVec1.add(xPixel);
                yPixelVec1.add(yPixel);

                if (i == 0) {
                    mPath1.moveTo(xPixel, yPixel);
                } else {
                    //mPath1.quadTo((xPixelVec1.get(i) + xPixelVec1.get(i - 1)) / 2, (yPixelVec1.get(i) + yPixelVec1.get(i - 1)) / 2, xPixelVec1.get(i), yPixelVec1.get(i));
                    //mPath1.quadTo(xPixelVec1.get(i - 1), yPixelVec1.get(i - 1), (xPixelVec1.get(i) + xPixelVec1.get(i - 1)) / 2, (yPixelVec1.get(i) + yPixelVec1.get(i - 1)) / 2);
                    mPath1.lineTo(xPixel, yPixel);
                }

            }
            DataShare.setPath(1, mPath1);
            scalePath(mPath1);
            scaleDot1();

        }
    }

    public void scalePath (Path p) {

        float mainWidth = DataShare.getMainCanvasWidth();
        float mainHeight = DataShare.getMainCanvasHeight();
        float previewHeight = canvasSize.getLayoutParams().height;
        float previewWidth = canvasSize.getLayoutParams().width;
        float scaledWidth = mainWidth / previewWidth;
        float scaledHeight = mainHeight / previewHeight;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaledWidth,scaledHeight);

        // TODO enable scaling once conflict of Paths is figured out
        p.transform(scaleMatrix);
    }

    public void scaleDot1 () {
        float mainWidth = DataShare.getMainCanvasWidth();
        float mainHeight = DataShare.getMainCanvasHeight();
        float previewHeight = canvasSize.getLayoutParams().height;
        float previewWidth = canvasSize.getLayoutParams().width;
        float scaledWidth = mainWidth / previewWidth;
        float scaledHeight = mainHeight / previewHeight;

        // transforming arrays for quad1
        if (xPixelVec1 != null) {

            // transforming x's
            float tempx1 = 0;
            for (int i = 0; i < xPixelVec1.size() - 1; i++) {
                tempx1 = xPixelVec1.get(i);
                tempx1 = tempx1 * scaledWidth;
                xPixelVec1Scaled.add(i, tempx1);
            }
            xPixelVec1 = xPixelVec1Scaled;
            DataShare.setXPixelVec(1, xPixelVec1Scaled);
            xPixelVec1Scaled = null;

            // transforming y's
            float tempy1 = 0;
            for (int i = 0; i < yPixelVec1.size() - 1; i++) {
                tempy1 = yPixelVec1.get(i);
                tempy1 = tempy1 * scaledHeight;
                yPixelVec1Scaled.add(i, tempy1);
            }
            yPixelVec1 = yPixelVec1Scaled;
            DataShare.setYPixelVec(1, yPixelVec1Scaled);
            yPixelVec1Scaled = null;
        }
    }
    // transform specified object's x position to pixels
    public double xMeterToPixel(double x, double y) {

        double normX = y / -pref.getFloat("newWidth", 5);
        double transX = normX * canvasSize.getLayoutParams().width;
        double xCoord = canvasSize.getX() + canvasSize.getLayoutParams().width / 2 + transX;

        return xCoord;
    }

    // transform specified object's y position to pixels
    public double yMeterToPixel(double x, double y) {

        double normY = x / pref.getFloat("newHeight", 3);
        double transY = normY * canvasSize.getLayoutParams().height;
        double yCoord = canvasSize.getY() + canvasSize.getLayoutParams().height / 2 - transY;

        return yCoord;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
/*
    private void scaling(int quad){

        int index = quadAllSeek.getProgress();
        float time1 = 0;// varaible that is merely used to take the calculated time and store it into the timesVec array
        float distance1 = 0;// total distance between start and moved dot
        float rate1= 0;// calculated rate such that the dot arrives to new point at time
        float distance = 0;// calculated distance from beginning of path to moved dot
        float time = 0;// is the time of at the x,y coordinates of the moved dot
        float distance2 = 0;// total distance between moved dot and end
        float rate2 = 0;// calculated rate for second half
        float time2 = 0;// is the last time
        int i = 0;// counting varaible
        float temp1 = 0;// stores distance for points in between all points from start to moved dot
        float temp2 = 0;// stores distance for points in between all points from moved dot to end
        float x0;// variable used in distance formula
        float y0;// variable used in distance formula
        float x1;// variable used in distance formula
        float y1;// variable used in distance formula
        float distance3;// calculated distance from moved dot of path to end
        int count = 0;// counting variable
        float temp3 = 0;

        // setting time
        if(index >= xPixelVec1.size()){
            index = xPixelVec1.size()-1;
        }else {
            time = (float) DataShare.getCurrentTime(1).get(index).toSeconds();
        }


        while(i != index-1){
            x0 = xPixelVec1.get(i);
            x1 = xPixelVec1.get(i+1);
            y0 = yPixelVec1.get(i);
            y1 = yPixelVec1.get(i +1);
            temp1 = (float) Math.sqrt(Math.pow(x1 - x0, 2.0) + Math.pow(y1 - y0, 2.0)); // converts from double to float then does the square root of it
            distance1 = distance1 + temp1;
            i++;
        }
        i = 0;
        rate1 = distance1/time;

        while(i != index){
            x0 = xPixelVec1.get(i);
            x1 = xPixelVec1.get(i+1);
            y0 = yPixelVec1.get(i);
            y1 = yPixelVec1.get(i +1);
            distance = (float) Math.sqrt(Math.pow(x1 - x0, 2.0) + Math.pow(y1 - y0, 2.0));
            time1 = distance/rate1;
            temp3 = time1 + temp3;
            timesVec1.add(temp3);
            i++;
        }

        count = index;
        while(count <= xPixelVec1.size()-2){
            x0 = xPixelVec1.get(count);
            x1 = xPixelVec1.get(count+1);
            y0 = yPixelVec1.get(count);
            y1 = yPixelVec1.get(count +1);
            temp2 = (float) Math.sqrt(Math.pow(x1 - x0, 2.0) + Math.pow(y1 - y0, 2.0)); // converts from double to float then does the square root of it
            distance2 = distance2 + temp2;
            count++;
        }
        count = 0;
        time2 = timesVec1.get(timesVec1.size()-1) - time;
        rate2 = distance2/time2;

        while(count != xPixelVec1.size()-2){
            x0 = xPixelVec1.get(count);
            x1 = xPixelVec1.get(count+1);
            y0 = yPixelVec1.get(count);
            y1 = yPixelVec1.get(count +1);
            distance3 = (float) Math.sqrt(Math.pow(x1 - x0, 2.0) + Math.pow(y1 - y0, 2.0));
            time1 = distance3/rate2;
            temp3 = time1 + temp3;
            timesVec1.add(temp3);
            count++;
        }
        Log.i("PreviewTimesVec1", "" + timesVec1);

        
    }*/
}