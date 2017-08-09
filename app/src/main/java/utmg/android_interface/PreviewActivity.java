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

import org.ros.android.AppCompatRosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PreviewActivity extends AppCompatRosActivity {

    ROSNodePublish nodePublish;

    //ROSNodeMain nodeMain = new ROSNodeMain();

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    private PreviewCanvas previewCanvas;
    private LinearLayout canvasSize;
    private RelativeLayout seekbarRelative;
    private int screenHeight;
    private int screenWidth;

    private ImageView quad1;
    private ArrayList<Float> timesVec1;
    private ArrayList<Float> xPixelVec1;
    private ArrayList<Float> yPixelVec1;
    private ArrayList<Double> xMeterVec1;
    private ArrayList<Double> yMeterVec1;
    private ArrayList<Float> xPixelVec1Scaled = new ArrayList<>();
    private ArrayList<Float> yPixelVec1Scaled = new ArrayList<>();

    private ImageView quad2;
    private ArrayList<Float> timesVec2;
    private ArrayList<Float> xPixelVec2;
    private ArrayList<Float> yPixelVec2;
    private ArrayList<Double> xMeterVec2;
    private ArrayList<Double> yMeterVec2;
    private ArrayList<Float> xPixelVec2Scaled = new ArrayList<>();
    private ArrayList<Float> yPixelVec2Scaled = new ArrayList<>();

    private ImageView quad3;
    private ArrayList<Float> timesVec3;
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

    private float scaledx1;
    private float scaledy1;
    private float scaledx2;
    private float scaledy2;
    private float scaledx3;
    private float scaledy3;

    Path path1 = new Path();
    Path path2 = new Path();
    Path path3 = new Path();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setupActionBar();

        // instantiate times vectors
        timesVec1 = new ArrayList<Float>();
        timesVec2 = new ArrayList<Float>();
        timesVec3 = new ArrayList<Float>();

        // Button to terminate app so it doesn't have to be done manually
        com.getbase.floatingactionbutton.FloatingActionButton sendAirSim = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.send_to_airSim);
        com.getbase.floatingactionbutton.FloatingActionButton terminate_preview = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.terminate_preview);
        terminate_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
                finish();
            }
        });

        // Button to send path to AirSim and RViz
        sendAirSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("PreviewActivity", "Sent path 1 to ROSNodePublish");

                // Work with quad1 for now
                if (DataShare.getServicedPath(1) != null) {
                    nodePublish.setPath1(DataShare.getServicedPath(1));
                }

                if (DataShare.getServicedPath(2) != null) {
                    nodePublish.setPath2(DataShare.getServicedPath(2));
                }

                if (DataShare.getServicedPath(3) != null) {
                    nodePublish.setPath3(DataShare.getServicedPath(3));
                }
            }
        });

        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        // initializing canvas
        canvasSize = (LinearLayout) findViewById(R.id.linLay);

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

        // initializing paths
        path1 = DataShare.getPath(1);
        path2 = DataShare.getPath(2);
        path3 = DataShare.getPath(3);

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
            // TODO enable checks after they are correctly figured out
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

        if(path1 != null){
            path1.transform(scaleMatrix);
        }
        if(path2 != null){
            path2.transform(scaleMatrix);
        }
        if(path3 != null){
            path3.transform(scaleMatrix);
        }

        xPixelVec1Scaled = new ArrayList<Float>();
        yPixelVec1Scaled = new ArrayList<Float>();
        xPixelVec2Scaled = new ArrayList<Float>();
        yPixelVec2Scaled = new ArrayList<Float>();
        xPixelVec3Scaled = new ArrayList<Float>();
        yPixelVec3Scaled = new ArrayList<Float>();

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
            DataShare.setXPixelVec(2, xPixelVec2Scaled);
            xPixelVec2Scaled = null;

            // transforming y's
            float tempy2 = 0;
            for (int i = 0; i < yPixelVec2.size() - 1; i++) {
                tempy2 = yPixelVec2.get(i);
                tempy2 = tempy2 * scaledHeight;
                yPixelVec2Scaled.add(i, tempy2);
            }
            yPixelVec2 = yPixelVec2Scaled;
            DataShare.setYPixelVec(2, yPixelVec2Scaled);
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
            DataShare.setXPixelVec(3, xPixelVec3Scaled);
            xPixelVec3Scaled = null;

            // transforming y's
            float tempy3 = 0;
            for (int i = 0; i < yPixelVec3.size() - 1; i++) {
                tempy3 = yPixelVec3.get(i);
                tempy3 = tempy3 * scaledHeight;
                yPixelVec3Scaled.add(i, tempy3);
            }
            yPixelVec3 = yPixelVec3Scaled;
            DataShare.setYPixelVec(3, yPixelVec3Scaled);
            yPixelVec3Scaled = null;
        }

        previewCanvas = (PreviewCanvas) findViewById(R.id.preview_canvas);
        previewCanvas.callOnDraw();

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
        // TODO fix logic
        final Handler handlerQuad = new Handler();
        Runnable runnableQuad = new Runnable() {
            @Override
            public void run() {

                // quad 1
                if (pref.getBoolean("quad1", true)) {
                    if(DataShare.getXPixelVec(1) == null){
                      quad1.setVisibility(View.INVISIBLE);
                  }else {
                      if (DataShare.getXPixelVec(1).isEmpty() == true) {
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
                }

                // quad 2
                if (pref.getBoolean("quad2", true)) {
                    if(DataShare.getXPixelVec(2) == null){
                        quad2.setVisibility(View.INVISIBLE);
                    }else {
                        if (DataShare.getXPixelVec(2).isEmpty() == true) { // error is being thrown here NullPointer because cannot determine if it is empty if it is null
                            quad2.setVisibility(View.INVISIBLE);
                        } else {
                            // place imageView at start of path on launch of PreviewActivity
                            if (togglei == 0) {
                                quad2.setX(xPixelVec2.get(0) - quad2.getWidth() / 2 + canvasSize.getLeft());
                                quad2.setY(yPixelVec2.get(0) - quad2.getHeight() / 2);
                                quad2.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

                // quad 3
                if (pref.getBoolean("quad3", true)) {
                    if (DataShare.getXPixelVec(3) == null) {
                        quad3.setVisibility(View.INVISIBLE);
                    } else {
                        if (DataShare.getXPixelVec(3).isEmpty() == true) {
                            quad3.setVisibility(View.INVISIBLE);
                        } else {
                            // place imageView at start of path on launch of PreviewActivity
                            if (togglei == 0) {
                                quad3.setX(xPixelVec3.get(0) - quad2.getWidth() / 2 + canvasSize.getLeft());
                                quad3.setY(yPixelVec3.get(0) - quad2.getHeight() / 2);
                                quad3.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
                handlerQuad.postDelayed(this, 10);
            }
        };
        runnableQuad.run();

        // ONLY WORKS AFTER PLAYING THROUGH ONCE
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
                        scaledx1 = xPixelVec1.get(leastDistanceIndex) - quad1.getWidth() / 2 + canvasSize.getLeft();
                        scaledy1 = yPixelVec1.get(leastDistanceIndex) - quad1.getHeight() / 2;
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // ONLY WORKS AFTER PLAYING THROUGH ONCE
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

        // ONLY WORKS AFTER PLAYING THROUGH ONCE
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
                                        if(xPixelVec1 != null) {
                                            if (xPixelVec1.isEmpty() == false) {

                                                if (index == xPixelVec1.size()) {
                                                    // This will handle the error come back to this

                                                } else if (index > xPixelVec1.size()) {
                                                    quad1.setX(xPixelVec1.get(xPixelVec1.size() - 1) - quad1.getWidth() / 2 + canvasSize.getLeft());
                                                    quad1.setY(yPixelVec1.get(yPixelVec1.size() - 1) - quad1.getHeight() / 2);

                                                } else {
                                                    quad1.setX(xPixelVec1.get(index) - quad1.getWidth() / 2 + canvasSize.getLeft());
                                                    quad1.setY(yPixelVec1.get(index) - quad1.getHeight() / 2);
                                                }
                                            }
                                        }
                                        if(xPixelVec2 != null) {
                                            if (xPixelVec2.isEmpty() == false) {
                                                if (index == xPixelVec2.size()) {
                                                    // This will handle the error

                                                } else if (index > xPixelVec2.size()) {
                                                    quad2.setX(xPixelVec2.get(xPixelVec2.size() - 1) - quad2.getWidth() / 2 + canvasSize.getLeft());
                                                    quad2.setY(yPixelVec2.get(yPixelVec2.size() - 1) - quad2.getHeight() / 2);

                                                } else {
                                                    quad2.setX(xPixelVec2.get(index) - quad2.getWidth() / 2 + canvasSize.getLeft());
                                                    quad2.setY(yPixelVec2.get(index) - quad2.getHeight() / 2);
                                                }
                                            }
                                        }
                                        if(xPixelVec3 != null) {
                                            if (xPixelVec3.isEmpty() == false) {
                                                if (index == xPixelVec3.size()) {
                                                    // This will handle the error

                                                } else if (index > xPixelVec3.size()) {
                                                    quad3.setX(xPixelVec3.get(xPixelVec3.size() - 1) - quad3.getWidth() / 2 + canvasSize.getLeft());
                                                    quad3.setY(yPixelVec3.get(yPixelVec3.size() - 1) - quad3.getHeight() / 2);

                                                } else {
                                                    quad3.setX(xPixelVec3.get(index) - quad3.getWidth() / 2 + canvasSize.getLeft());
                                                    quad3.setY(yPixelVec3.get(index) - quad3.getHeight() / 2);
                                                }
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

    // coordinate seekbar with quads
    private void seekAll() {

        // TODO has to be a better way to do this
        // set seekbar max equal to the longest quad length - should equal longest time
        if(xPixelVec1 != null && xPixelVec2 != null && xPixelVec3 != null){
            quadAllSeek.setMax(Math.max(Math.max(xPixelVec1.size(), xPixelVec2.size()), xPixelVec3.size()));
            if(xPixelVec3.size() == quadAllSeek.getMax()){
                quadMax = 3;
            }else if(xPixelVec2.size() == quadAllSeek.getMax()){
                quadMax = 2;
            }else{
                quadMax = 1;
            }
        }else if(xPixelVec1 == null){
            if(xPixelVec2 == null){
                quadAllSeek.setMax(xPixelVec3.size());
                quadMax = 3;
            }else if (xPixelVec3 == null){
                quadAllSeek.setMax(xPixelVec2.size());
                quadMax = 2;
            }else {
                quadAllSeek.setMax(Math.max(xPixelVec3.size(), xPixelVec2.size()));
                if(xPixelVec3.size() == quadAllSeek.getMax()){
                    quadMax = 3;
                }else{
                    quadMax = 2;
                }
            }
        }
        else if(xPixelVec2 == null){
            if(xPixelVec1 == null){
                quadAllSeek.setMax(xPixelVec3.size());
                quadMax = 3;
            }else if (xPixelVec3 == null){
                quadAllSeek.setMax(xPixelVec1.size());
                quadMax = 1;
            }else {
                quadAllSeek.setMax(Math.max(xPixelVec3.size(), xPixelVec1.size()));
                if(xPixelVec3.size() == quadAllSeek.getMax()){
                    quadMax = 3;
                }else{
                    quadMax = 1;
                }
            }
        }
        else if(xPixelVec3 == null){
            if(xPixelVec2 == null){
                quadAllSeek.setMax(xPixelVec1.size());
                quadMax = 1;
            }else if (xPixelVec1 == null){
                quadAllSeek.setMax(xPixelVec2.size());
                quadMax = 2;
            }else {
                quadAllSeek.setMax(Math.max(xPixelVec1.size(), xPixelVec2.size()));
                if(xPixelVec1.size() == quadAllSeek.getMax()){
                    quadMax = 1;
                }else{
                    quadMax = 2;
                }
            }
        }

        final Handler seekH = new Handler();
        Runnable seekR = new Runnable() {
            @Override
            public void run() {
                switch (quadMax){
                    case 1:
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
                    } break;

                    case 2:
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
                        } break;

                    case 3:
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
                    } break;
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

                        // convert path to meters
                        xMeterVec1.add(xMeter);
                        yMeterVec1.add(yMeter);

                        //Log.i("PreviewActivity","xMeter: " + Double.toString(xMeter) + "\t yMeter: " + Double.toString(yMeter) + " # " + Integer.toString(i + 1));

                        // convert path to pixels
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
                    break;

                case 2:
                    android.graphics.Path mPath2 = new android.graphics.Path();

                    for (int i = 0; i < p.getPoses().size(); i++) {

                        double xMeter = p.getPoses().get(i).getPose().getPosition().getX();
                        double yMeter = p.getPoses().get(i).getPose().getPosition().getY();

                        // convert path to meters
                        xMeterVec2.add(xMeter);
                        yMeterVec2.add(yMeter);

                        //Log.i("PreviewActivity","xMeter: " + Double.toString(xMeter) + "\t yMeter: " + Double.toString(yMeter) + " # " + Integer.toString(i + 1));

                        float xPixel = (float) xMeterToPixel(xMeter, yMeter);
                        float yPixel = (float) yMeterToPixel(xMeter, yMeter);

                        // convert path to pixels
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
                    scalePath(mPath2);
                    scaleDot2();
                    break;

                case 3:
                    android.graphics.Path mPath3 = new android.graphics.Path();

                    for (int i = 0; i < p.getPoses().size(); i++) {

                        double xMeter = p.getPoses().get(i).getPose().getPosition().getX();
                        double yMeter = p.getPoses().get(i).getPose().getPosition().getY();

                        // convert path to meters
                        xMeterVec3.add(xMeter);
                        yMeterVec3.add(yMeter);

                        //Log.i("PreviewActivity","xMeter: " + Double.toString(xMeter) + "\t yMeter: " + Double.toString(yMeter) + " # " + Integer.toString(i + 1));

                        float xPixel = (float) xMeterToPixel(xMeter, yMeter);
                        float yPixel = (float) yMeterToPixel(xMeter, yMeter);

                        // convert path to pixels
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
                    scalePath(mPath3);
                    scaleDot3();
                    break;
            }
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

    public void scaleDot2 () {
        float mainWidth = DataShare.getMainCanvasWidth();
        float mainHeight = DataShare.getMainCanvasHeight();
        float previewHeight = canvasSize.getLayoutParams().height;
        float previewWidth = canvasSize.getLayoutParams().width;
        float scaledWidth = mainWidth / previewWidth;
        float scaledHeight = mainHeight / previewHeight;

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
            DataShare.setXPixelVec(2, xPixelVec2Scaled);
            xPixelVec2Scaled = null;

            // transforming y's
            float tempy2 = 0;
            for (int i = 0; i < yPixelVec2.size() - 1; i++) {
                tempy2 = yPixelVec2.get(i);
                tempy2 = tempy2 * scaledHeight;
                yPixelVec2Scaled.add(i, tempy2);
            }
            yPixelVec2 = yPixelVec2Scaled;
            DataShare.setYPixelVec(2, yPixelVec2Scaled);
            yPixelVec2Scaled = null;
        }

    }

    public void scaleDot3 () {
        float mainWidth = DataShare.getMainCanvasWidth();
        float mainHeight = DataShare.getMainCanvasHeight();
        float previewHeight = canvasSize.getLayoutParams().height;
        float previewWidth = canvasSize.getLayoutParams().width;
        float scaledWidth = mainWidth / previewWidth;
        float scaledHeight = mainHeight / previewHeight;

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
            DataShare.setXPixelVec(3, xPixelVec3Scaled);
            xPixelVec3Scaled = null;

            // transforming y's
            float tempy3 = 0;
            for (int i = 0; i < yPixelVec3.size() - 1; i++) {
                tempy3 = yPixelVec3.get(i);
                tempy3 = tempy3 * scaledHeight;
                yPixelVec3Scaled.add(i, tempy3);
            }
            yPixelVec3 = yPixelVec3Scaled;
            DataShare.setYPixelVec(3, yPixelVec3Scaled);
            yPixelVec3Scaled = null;
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

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
//        rosTextView.setText("test");
//        rosTextView.setTopicName("testtopic");
//        rosTextView.setMessageType("std_msgs/String");

        nodePublish = new ROSNodePublish();

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

            //nodeConfiguration.setNodeName()

            nodeMainExecutor.execute(nodePublish, nodeConfiguration);
        } catch (IOException e) {
            // Socket problem
            Log.e("PreviewActivity", "socket error trying to get networking information from the master uri");
        }

    }

}
