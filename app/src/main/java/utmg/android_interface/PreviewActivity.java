package utmg.android_interface;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    private PreviewCanvas previewCanvas;
    private LinearLayout canvasSize;
    private RelativeLayout seekbarRelative;
    private int screenHeight;
    private int screenWidth;

    private ImageView quad1;
    private ArrayList<Float> xPixelVec1;
    private ArrayList<Float> yPixelVec1;
    private ArrayList<Double> xMeterVec1;
    private ArrayList<Double> yMeterVec1;
    private ArrayList<Float> xPixelVec1Scaled = new ArrayList<>();
    private ArrayList<Float> yPixelVec1Scaled = new ArrayList<>();

    private ImageView quad2;
    private ArrayList<Float> xPixelVec2;
    private ArrayList<Float> yPixelVec2;
    private ArrayList<Double> xMeterVec2;
    private ArrayList<Double> yMeterVec2;
    private ArrayList<Float> xPixelVec2Scaled = new ArrayList<>();
    private ArrayList<Float> yPixelVec2Scaled = new ArrayList<>();

    private ImageView quad3;
    private ArrayList<Float> xPixelVec3;
    private ArrayList<Float> yPixelVec3;
    private ArrayList<Double> xMeterVec3;
    private ArrayList<Double> yMeterVec3;
    private ArrayList<Float> xPixelVec3Scaled = new ArrayList<>();
    private ArrayList<Float> yPixelVec3Scaled = new ArrayList<>();

    private SeekBar quadAllSeek;
    private ToggleButton toggle;
    private int togglei = 0;
    private int quadMax = 0;
    private int layoutHeight = 0;
    private int layoutWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setupActionBar();

        //Log.i("PreviewActivity", "PreviewActivity started.");

        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        // intializing canvas
        canvasSize = (LinearLayout) findViewById(R.id.linLay);
        previewCanvas = (PreviewCanvas) findViewById(R.id.preview_canvas);

        // CALLING ONDRAW
        previewCanvas.callOnDraw();

        // getting screen size
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        // set current state of quad at 0
        DataShare.setSeekLoc(1, 0);
        DataShare.setSeekLoc(2, 0);
        DataShare.setSeekLoc(3, 0);

        // getting arrays from DataShare
        xPixelVec1 = DataShare.getXPixelVec(1);
        yPixelVec1 = DataShare.getYPixelVec(1);
        xPixelVec2 = DataShare.getXPixelVec(2);
        yPixelVec2 = DataShare.getYPixelVec(2);
        xPixelVec3 = DataShare.getXPixelVec(3);
        yPixelVec3 = DataShare.getYPixelVec(3);

        // DON'T KNOW WHAT THIS IS! SIDDARTH HELP! - what does this do?
        // SIDDARTH'S COMMENTS
        // boolean playBackState keeps track whether or not PreviewActivity is ready to start
        // playback. It prevents the play/pause button from replaying from the beginning
        // concurrently if the user only paused the playback.
        DataShare.setPlayBackState(true);

        // initializing quadAllSeek
        seekbarRelative = (RelativeLayout) findViewById(R.id.seekbar_relative);
        quadAllSeek = (SeekBar) findViewById(R.id.quadAllSeek);

        float mainWidth = canvasSize.getWidth();
        float mainHeight = canvasSize.getHeight();

        // DON'T KNOW WHAT THIS IS! SIDDARTH HELP! - what is this listener?
        // SIDDARTH'S COMMENTS
        // seekbarRelative is the container view for the bottom seekbar for playback.
        // The listener is implemented such that PreviewActivity first waits for the seekbarRelative
        // to render first, then adjust the size of the canvas to fit the screen properly.
        // The listener is then attempted to be removed, as it is not needed anymore.
        seekbarRelative.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {

                // read height with myLinearLayout.getHeight() etc.
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) canvasSize.getLayoutParams();
                params.height = (int) (screenHeight - seekbarRelative.getHeight() - (getSupportActionBar().getHeight() * 1.5));
                layoutHeight = params.height;
                params.width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5) / pref.getFloat("newHeight", 3)));
                layoutWidth = params.width;
                canvasSize.setLayoutParams(params);

                // remember to remove the listener if possible
                ViewTreeObserver viewTreeObserver = seekbarRelative.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });

        // DON'T KNOW WHAT THIS IS! SIDDARTH HELP! - does this need to be inside this listener?
        // SIDDARTH'S COMMENTS
        // This is definitely NOT the place for the transformations... Why are they here?
        // This listener is only to adjust view sizes to fit the screen; nothing else should
        // be here.

        // defining variables for transformation matrix
        Matrix transform = new Matrix();

        float previewHeight = layoutHeight;
        float previewWidth = layoutWidth;
        float scaledWidth = previewWidth / mainWidth;
        float scaledHeight = previewHeight / mainHeight;
        Log.i("mainWidth", Float.toString(mainWidth));
        Log.i("mainHeight", Float.toString(mainHeight));
        Log.i("previewWidth", Float.toString(previewWidth));
        Log.i("previewHeight", Float.toString(previewHeight));
        Log.i("scaledWidth", Float.toString(scaledWidth));
        Log.i("scaledHeight", Float.toString(scaledHeight));

        // initializing paths
        Path path1 = DataShare.getPath(1);
        Path path2 = DataShare.getPath(2);
        Path path3 = DataShare.getPath(3);

        // setting the matrix
        transform.postScale(scaledWidth, scaledHeight);
        Log.i("Matrix", transform.toString());

        // transforming paths
        path1.transform(transform);
        path2.transform(transform);
        path3.transform(transform);

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
            xPixelVec1Scaled = null;

            // transforming y's
            float tempy1 = 0;
            for (int i = 0; i < yPixelVec1.size() - 1; i++) {
                tempy1 = yPixelVec1.get(i);
                tempy1 = tempy1 * scaledWidth;
                yPixelVec1Scaled.add(i, tempy1);
            }
            yPixelVec1 = yPixelVec1Scaled;
            yPixelVec1Scaled = null;
        }

        // transforming arrays for quad2
        if (xPixelVec2 != null) {

            // transforming x's
            float tempx2 = 0;
            for (int i = 0; i < xPixelVec2.size() - 1; i++) {
                tempx2 = xPixelVec2.get(i);
                tempx2 = tempx2 * scaledWidth;
                xPixelVec2Scaled.add(i, tempx2);
            }
            xPixelVec2 = xPixelVec2Scaled;
            xPixelVec2Scaled = null;

            // transforming y's
            float tempy2 = 0;
            for (int i = 0; i < yPixelVec2.size() - 1; i++) {
                tempy2 = yPixelVec2.get(i);
                tempy2 = tempy2 * scaledWidth;
                yPixelVec2Scaled.add(i, tempy2);
            }
            yPixelVec2 = yPixelVec2Scaled;
            yPixelVec2Scaled = null;
        }

        // transforming arrays for quad3
        if (xPixelVec3 != null) {

            // transforming x's
            float tempx3 = 0;
            for (int i = 0; i < xPixelVec3.size() - 1; i++) {
                tempx3 = xPixelVec3.get(i);
                tempx3 = tempx3 * scaledWidth;
                xPixelVec3Scaled.add(i, tempx3);
            }
            xPixelVec3 = xPixelVec3Scaled;
            xPixelVec3Scaled = null;

            // transforming y's
            float tempy3 = 0;
            for (int i = 0; i < yPixelVec3.size() - 1; i++) {
                tempy3 = yPixelVec3.get(i);
                tempy3 = tempy3 * scaledWidth;
                yPixelVec3Scaled.add(i, tempy3);
            }
            yPixelVec3 = yPixelVec3Scaled;
            yPixelVec3Scaled = null;
        }


        // DON'T KNOW WHAT THIS IS! SIDDARTH HELP! - what does this mean?
        // SIDDARTH'S COMMENTS
        // The "serviceToggle" from the preferences stores whether or not the app should attempt to
        // interface with the service.
        // true -> contact service; false -> don't contact service
        // convertROSPathToPixelVec is only called if "serviceToggle" is true
        // TODO if path planner servicing is enabled, redo the path
        if (pref.getBoolean("serviceToggle", false)) {

            // initializing arrays for quad1
            xPixelVec1 = new ArrayList<>();
            yPixelVec1 = new ArrayList<>();
            xMeterVec1 = new ArrayList<>();
            yMeterVec1 = new ArrayList<>();

            // initializing arrays for quad2
            xPixelVec2 = new ArrayList<>();
            yPixelVec2 = new ArrayList<>();
            xMeterVec2 = new ArrayList<>();
            yMeterVec2 = new ArrayList<>();

            // initializing arrays for quad3
            xPixelVec3 = new ArrayList<>();
            yPixelVec3 = new ArrayList<>();
            xMeterVec3 = new ArrayList<>();
            yMeterVec3 = new ArrayList<>();

            // calls convertROSPathToPixel - self explanatory
            if(xPixelVec1.size() != 0) {
                convertROSPathToPixelVec(1, DataShare.getServicedPath(1));
            }
            if(xPixelVec2.size() != 0) {
                convertROSPathToPixelVec(2, DataShare.getServicedPath(2));
            }
            if(xPixelVec3.size() != 0) {
                convertROSPathToPixelVec(3, DataShare.getServicedPath(3));
            }

            Log.i(" ppp111ppp111 ", "" + xPixelVec1.size());
            Log.i(" ppp222ppp222 ", "" + xPixelVec2.size());
            Log.i(" ppp333ppp333 ", "" + xPixelVec3.size());
        }

        // initializing imageViews
        quad1 = (ImageView) findViewById(R.id.demo_quad1);
        quad2 = (ImageView) findViewById(R.id.demo_quad2);
        quad3 = (ImageView) findViewById(R.id.demo_quad3);

        // set size of dots in Preview Activity
        quad1.getLayoutParams().height = (int) (screenHeight * 0.02);
        quad1.getLayoutParams().width = (int) (screenWidth * 0.02);
        quad2.getLayoutParams().height = (int) (screenHeight * 0.02);
        quad2.getLayoutParams().width = (int) (screenWidth * 0.02);
        quad3.getLayoutParams().height = (int) (screenHeight * 0.02);
        quad3.getLayoutParams().width = (int) (screenWidth * 0.02);

        xPixelVec1 = DataShare.getXPixelVec(1);
        yPixelVec1 = DataShare.getYPixelVec(1);
        xPixelVec2 = DataShare.getXPixelVec(2);
        yPixelVec2 = DataShare.getYPixelVec(2);
        xPixelVec3 = DataShare.getXPixelVec(3);
        yPixelVec3 = DataShare.getYPixelVec(3);

        // QUAD RUNNABLE: sets visibility of quad and places it at first point
        final Handler handlerQuad = new Handler();
        Runnable runnableQuad = new Runnable() {
            @Override
            public void run() {

                // quad 1
                if (pref.getBoolean("quad1", false)) {
                    //Log.i(" ppp111ppp111 ", "" + xPixelVec1.size());
                    if (xPixelVec1.size() == 0 || yPixelVec1.size() == 0) {
                        quad1.setVisibility(View.INVISIBLE);
                    } else {
                        // place imageView at start of path on launch of PreviewActivity
                        if (togglei == 0) {
                            quad1.setX(xPixelVec1.get(0) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad1.setY(yPixelVec1.get(0) - quad1.getHeight() / 2);
                            quad1.setVisibility(View.VISIBLE);
                        }
                    }
                }

                // quad 2
                if (pref.getBoolean("quad2", false)) {
//                    Log.i(" ppp222ppp222 ", "" + xPixelVec2.size());
                    if (xPixelVec2.size() == 0 || yPixelVec2.size() == 0) {
                        quad2.setVisibility(View.INVISIBLE);
                    } else {
                        // place imageView at start of path on launch of PreviewActivity
                        if (togglei == 0) {
                            quad2.setX(xPixelVec2.get(0) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad2.setY(yPixelVec2.get(0) - quad1.getHeight() / 2);
                            quad2.setVisibility(View.VISIBLE);
                        }
                    }
                }

                // quad 3
                if (pref.getBoolean("quad3", false)) {
//                    Log.i(" ppp333ppp333 ", "" + xPixelVec3.size());
                    if (xPixelVec3.size() == 0 || yPixelVec3.size() == 0) {
                        quad3.setVisibility(View.INVISIBLE);
                    } else {
                        // place imageView at start of path on launch of PreviewActivity
                        if (togglei == 0) {
                            quad3.setX(xPixelVec3.get(0) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad3.setY(yPixelVec3.get(0) - quad1.getHeight() / 2);
                            quad3.setVisibility(View.VISIBLE);
                        }
                    }
                }
                handlerQuad.postDelayed(this, 10);
            }
        };
        runnableQuad.run();


        // ONLY WORKS AFTER RUNNING THROUGH ONCE
        // Touch listener for quad1 - for dragging quad along path
        quad1.setOnTouchListener(new View.OnTouchListener() {
            Point DownPT = new Point(); // Record mouse position when pressed down
            Point StartPT = new Point(); // Record start position of quad1

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eid = event.getAction();
                switch (eid) {

                    // when you first press down
                    case MotionEvent.ACTION_DOWN:
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new Point((int) quad1.getX(), (int) quad1.getY());
                        break;

                    // when you move dot
                    case MotionEvent.ACTION_MOVE:
                        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                        quad1.setX((int) (StartPT.x + mv.x));
                        quad1.setY((int) (StartPT.y + mv.y));
                        StartPT = new Point((int) quad1.getX(), (int) quad1.getY());
                        break;

                    // when you pick up pen
                    case MotionEvent.ACTION_UP:
                        int t = 0;
                        double distance = 0;
                        double leastDistance = 100000000;// needs to be large in order for distance to enter into the loop
                        int leastDistanceIndex = 0;
                        double d = 0.0;
                        float x1 = quad1.getX() - quad1.getWidth() / 2 - canvasSize.getLeft();
                        float x2 = x1;
                        float y2 = 0;
                        float y1 = quad1.getY() - quad1.getHeight() / 2;
                        int p = 0;

                        while (t < xPixelVec1.size() - 1) {
                            
                            x2 = xPixelVec1.get(t);
                            y2 = yPixelVec1.get(t);

                            d = Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0);// part 1 of the distance formula (x2 - x1)^2 + (y2 - y1)^2
                            distance = Math.sqrt((float) d);// converts from double to float then does the square root of it

                            if (distance < leastDistance) {// if the distance found is smaller than the greatest
                                leastDistance = distance;// then it becomes the leastDistance
                                leastDistanceIndex = t;
                            }
                            t++;
                        }
                        quad1.setX(xPixelVec1.get(leastDistanceIndex) - quad1.getWidth() / 2 + canvasSize.getLeft());
                        quad1.setY(yPixelVec1.get(leastDistanceIndex) - quad1.getHeight() / 2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // Touch listener for quad2 - for dragging quad along path
        quad2.setOnTouchListener(new View.OnTouchListener() {
            Point DownPT = new Point(); // Record mouse position when pressed down
            Point StartPT = new Point(); // Record start position of quad1

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eid = event.getAction();
                switch (eid) {

                    // when you first press down
                    case MotionEvent.ACTION_DOWN:
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new Point((int) quad2.getX(), (int) quad2.getY());
                        break;

                    // when you move dot
                    case MotionEvent.ACTION_MOVE:
                        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                        quad2.setX((int) (StartPT.x + mv.x));
                        quad2.setY((int) (StartPT.y + mv.y));
                        StartPT = new Point((int) quad2.getX(), (int) quad2.getY());
                        break;

                    // when you pick up pen
                    case MotionEvent.ACTION_UP:
                        int t = 0;
                        double distance = 0;
                        double leastDistance = 100000000;// needs to be large in order for distance to enter into the loop
                        int leastDistanceIndex = 0;
                        double d = 0.0;
                        float x1 = quad2.getX() - quad2.getWidth() / 2 - canvasSize.getLeft();
                        float x2 = x1;
                        float y2 = 0;
                        float y1 = quad2.getY() - quad2.getHeight() / 2;
                        int p = 0;

                        while (t < xPixelVec2.size() - 1) {
                            x2 = xPixelVec2.get(t);
                            y2 = yPixelVec2.get(t);

                            d = Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0);// part 1 of the distance formula (x2 - x1)^2 + (y2 - y1)^2
                            distance = Math.sqrt((float) d);// converts from double to float then does the square root of it

                            if (distance < leastDistance) {// if the distance found is smaller than the greatest
                                leastDistance = distance;// then it becomes the leastDistance
                                leastDistanceIndex = t;
                            }
                            t++;
                        }
                        quad2.setX(xPixelVec2.get(leastDistanceIndex) - quad2.getWidth() / 2 + canvasSize.getLeft());
                        quad2.setY(yPixelVec2.get(leastDistanceIndex) - quad2.getHeight() / 2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // Touch listener for quad3 - for dragging quad along path
        quad3.setOnTouchListener(new View.OnTouchListener() {
            Point DownPT = new Point(); // Record mouse position when pressed down
            Point StartPT = new Point(); // Record start position of quad1

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eid = event.getAction();
                switch (eid) {

                    // when you first press down
                    case MotionEvent.ACTION_DOWN:
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new Point((int) quad3.getX(), (int) quad3.getY());
                        break;

                    // when you move dot
                    case MotionEvent.ACTION_MOVE:
                        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                        quad3.setX((int) (StartPT.x + mv.x));
                        quad3.setY((int) (StartPT.y + mv.y));
                        StartPT = new Point((int) quad3.getX(), (int) quad3.getY());
                        break;

                    // when you pick up pen
                    case MotionEvent.ACTION_UP:
                        int t = 0;
                        double distance = 0;
                        double leastDistance = 100000000;// needs to be large in order for distance to enter into the loop
                        int leastDistanceIndex = 0;
                        double d = 0.0;
                        float x1 = quad3.getX() - quad3.getWidth() / 2 - canvasSize.getLeft();
                        float x2 = x1;
                        float y2 = 0;
                        float y1 = quad3.getY() - quad3.getHeight() / 2;
                        int p = 0;

                        while (t < xPixelVec3.size() - 1) {
                            x2 = xPixelVec3.get(t);
                            y2 = yPixelVec3.get(t);

                            d = Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0); // part 1 of the distance formula (x2 - x1)^2 + (y2 - y1)^2
                            distance = Math.sqrt((float) d); // converts from double to float then does the square root of it

                            if (distance < leastDistance) { // if the distance found is smaller than the greatest
                                leastDistance = distance; // then it becomes the leastDistance
                                leastDistanceIndex = t;
                            }
                            t++;
                        }
                        quad3.setX(xPixelVec3.get(leastDistanceIndex) - quad3.getWidth() / 2 + canvasSize.getLeft());
                        quad3.setY(yPixelVec3.get(leastDistanceIndex) - quad3.getHeight() / 2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

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
                                    int index = quadAllSeek.getProgress();
                                    if (toggle.isChecked() == false) {
                                        if (xPixelVec1 != null) {

                                            if (index == xPixelVec1.size()) {
                                                // This will handle the error come back to this

                                            } else if (index > xPixelVec1.size()) {
                                                quad1.setX(xPixelVec1.size() - quad1.getWidth() / 2+ canvasSize.getLeft());
                                                quad1.setY(yPixelVec1.size() - quad1.getHeight() / 2);

                                            } else {
                                                quad1.setX(xPixelVec1.get(index) - quad1.getWidth() / 2 + canvasSize.getLeft());
                                                quad1.setY(yPixelVec1.get(index) - quad1.getHeight() / 2);
                                            }
                                        }
                                        
                                        if (xPixelVec2 != null) {
                                            if (index == xPixelVec2.size()) {
                                                // This will handle the error

                                            } else if (index > xPixelVec2.size()) {
                                                quad2.setX(xPixelVec2.size() - quad2.getWidth() / 2 + canvasSize.getLeft());
                                                quad2.setY(yPixelVec2.size() - quad2.getHeight() / 2);

                                            } else {
                                                quad2.setX(xPixelVec2.get(index) - quad2.getWidth() / 2 + canvasSize.getLeft());
                                                quad2.setY(yPixelVec2.get(index) - quad2.getHeight() / 2);
                                            }
                                        }
                                        
                                        if (xPixelVec3 != null) {
                                            if (index == xPixelVec3.size()) {
                                                // This will handle the error

                                            } else if (index > xPixelVec3.size()) {
                                                quad3.setX(xPixelVec3.size() - quad3.getWidth() / 2 + canvasSize.getLeft());
                                                quad3.setY(yPixelVec3.size() - quad3.getHeight() / 2);

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

    // run quad2 on play along the path
    private void runQuad2() {
        final Handler updateH2 = new Handler();
        Runnable updateR2 = new Runnable() {
            @Override
            public void run() {

                while (DataShare.getSeekLoc(2) < xPixelVec2.size() - 1) {
                    // NOTE: CHANGE THIS WAIT TIME IN MS AS DESIRED BY PLAYBACK SPEED
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateH2.post(new Runnable() {
                        @Override
                        public void run() {

                            int value = DataShare.getSeekLoc(2);

                            quad2.setX(xPixelVec2.get(value) - quad2.getWidth() / 2 + canvasSize.getLeft());
                            quad2.setY(yPixelVec2.get(value) - quad2.getHeight() / 2);

                            if (toggle.isChecked()) {
                                DataShare.setSeekLoc(2, value + 1);
                            } else {
                                DataShare.setPlayBackState(false);
                            }

                            if (value >= xPixelVec2.size() - 1) {
                                DataShare.setSeekLoc(2, 0);
                                DataShare.setPlayBackState(true);
                            }
                        }
                    });
                }
            }
        };
        new Thread(updateR2).start();
    }

    // run quad3 on play along the path
    private void runQuad3() {
        final Handler updateH3 = new Handler();
        Runnable updateR3 = new Runnable() {
            @Override
            public void run() {

                while (DataShare.getSeekLoc(3) < xPixelVec3.size() - 1) {
                    // NOTE: CHANGE THIS WAIT TIME IN MS AS DESIRED BY PLAYBACK SPEED
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateH3.post(new Runnable() {
                        @Override
                        public void run() {

                            int value = DataShare.getSeekLoc(3);

                            quad3.setX(xPixelVec3.get(value) - quad3.getWidth() / 2 + canvasSize.getLeft());
                            quad3.setY(yPixelVec3.get(value) - quad3.getHeight() / 2);

                            if (toggle.isChecked()) {
                                DataShare.setSeekLoc(3, value + 1);
                            } else {
                                DataShare.setPlayBackState(false);
                            }

                            if (value >= xPixelVec3.size() - 1) {
                                DataShare.setSeekLoc(3, 0);
                                DataShare.setPlayBackState(true);
                            }
                        }
                    });
                }
            }
        };
        new Thread(updateR3).start();
    }

    // SIDDARTH HELP! - seekbar is not moving on play
    // SIDDARTH'S COMMENTS
    // TODO; will look into it.
    private void seekAll() {

        // set seekbar max equal to the longest quad length - should equal longest time
        quadAllSeek.setMax(Math.max(Math.max(xPixelVec1.size(), xPixelVec2.size()), xPixelVec3.size()));

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
                        seekH.post(new Runnable() {
                            @Override
                            public void run() {
                                int value = DataShare.getSeekLoc(quadMax);
                                if (value != 0) {
                                    quadAllSeek.setProgress(value);
                                }
                                if (value == quadAllSeek.getMax() - 1) {
                                    quadAllSeek.setProgress(quadAllSeek.getMax());
                                    toggle.setChecked(false);
                                }
                            }
                        });
                    }
                } else if (xPixelVec2.size() == quadAllSeek.getMax()) {
                    quadMax = 2;
                    while (quadMax < quadAllSeek.getMax()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        seekH.post(new Runnable() {
                            @Override
                            public void run() {
                                int value = DataShare.getSeekLoc(quadMax);
                                if (value != 0) {
                                    quadAllSeek.setProgress(value);
                                }
                                if (value == quadAllSeek.getMax() - 1) {
                                    quadAllSeek.setProgress(quadAllSeek.getMax());
                                    toggle.setChecked(false);
                                }
                            }
                        });
                    }
                } else {
                    quadMax = 3;
                    while (quadMax < quadAllSeek.getMax()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        seekH.post(new Runnable() {
                            @Override
                            public void run() {
                                int value = DataShare.getSeekLoc(quadMax);
                                if (value != 0) {
                                    quadAllSeek.setProgress(value);
                                }
                                if (value == quadAllSeek.getMax() - 1) {
                                    quadAllSeek.setProgress(quadAllSeek.getMax());
                                    toggle.setChecked(false);
                                }
                            }
                        });
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
            switch (k) {
                case 1:
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
                    break;

                case 2:
                    android.graphics.Path mPath2 = new android.graphics.Path();

                    for (int i = 0; i < p.getPoses().size(); i++) {

                        double xMeter = p.getPoses().get(i).getPose().getPosition().getX();
                        double yMeter = p.getPoses().get(i).getPose().getPosition().getY();

                        xMeterVec2.add(xMeter);
                        yMeterVec2.add(yMeter);

                        //Log.i("PreviewActivity","xMeter: " + Double.toString(xMeter) + "\t yMeter: " + Double.toString(yMeter) + " # " + Integer.toString(i + 1));

                        float xPixel = (float) xMeterToPixel(xMeter, yMeter);
                        float yPixel = (float) yMeterToPixel(xMeter, yMeter);

                        xPixelVec2.add(xPixel);
                        yPixelVec2.add(yPixel);

                        if (i == 0) {
                            mPath2.moveTo(xPixel, yPixel);
                        } else {
                            //mPath1.quadTo((xPixelVec1.get(i) + xPixelVec1.get(i - 1)) / 2, (yPixelVec1.get(i) + yPixelVec1.get(i - 1)) / 2, xPixelVec1.get(i), yPixelVec1.get(i));
                            //mPath1.quadTo(xPixelVec1.get(i - 1), yPixelVec1.get(i - 1), (xPixelVec1.get(i) + xPixelVec1.get(i - 1)) / 2, (yPixelVec1.get(i) + yPixelVec1.get(i - 1)) / 2);
                            mPath2.lineTo(xPixel, yPixel);
                        }
                    }

                    DataShare.setPath(2, mPath2);
                    break;

                case 3:
                    android.graphics.Path mPath3 = new android.graphics.Path();

                    for (int i = 0; i < p.getPoses().size(); i++) {

                        double xMeter = p.getPoses().get(i).getPose().getPosition().getX();
                        double yMeter = p.getPoses().get(i).getPose().getPosition().getY();

                        xMeterVec3.add(xMeter);
                        yMeterVec3.add(yMeter);

                        //Log.i("PreviewActivity","xMeter: " + Double.toString(xMeter) + "\t yMeter: " + Double.toString(yMeter) + " # " + Integer.toString(i + 1));

                        float xPixel = (float) xMeterToPixel(xMeter, yMeter);
                        float yPixel = (float) yMeterToPixel(xMeter, yMeter);

                        xPixelVec3.add(xPixel);
                        yPixelVec3.add(yPixel);

                        if (i == 0) {
                            mPath3.moveTo(xPixel, yPixel);
                        } else {
                            //mPath1.quadTo((xPixelVec1.get(i) + xPixelVec1.get(i - 1)) / 2, (yPixelVec1.get(i) + yPixelVec1.get(i - 1)) / 2, xPixelVec1.get(i), yPixelVec1.get(i));
                            //mPath1.quadTo(xPixelVec1.get(i - 1), yPixelVec1.get(i - 1), (xPixelVec1.get(i) + xPixelVec1.get(i - 1)) / 2, (yPixelVec1.get(i) + yPixelVec1.get(i - 1)) / 2);
                            mPath3.lineTo(xPixel, yPixel);
                        }
                    }

                    DataShare.setPath(3, mPath3);
                    break;
            }
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
}