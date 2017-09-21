package utmg.android_interface;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.ToggleButton;

import org.ros.android.AppCompatRosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.IOException;
import java.util.ArrayList;

public class PreviewActivity extends AppCompatRosActivity {

    ROSNodePublish nodePublish;

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

    private int layoutHeight = 0;
    private int layoutWidth = 0;

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
                Snackbar.make(v, "Published trajectory!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Work with quad1 for now
                if (DataShare.getServicedPath(1) != null) {
                    nodePublish.setPath1(DataShare.getServicedPath(1));
                    nodePublish.setPVAT1(DataShare.getServicedPVAT(1));

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
        // read height with myLinearLayout.getHeight() etc.
        TableRow.LayoutParams params = (TableRow.LayoutParams) seekbarRelative.getLayoutParams();
        params.height = (int) (screenHeight/10);
        layoutHeight = params.height;
        params.width = (int) (screenWidth);
        layoutWidth = params.width;
        seekbarRelative.setLayoutParams(params);

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

        xPixelVec1Scaled = new ArrayList<>();
        yPixelVec1Scaled = new ArrayList<>();
        xPixelVec2Scaled = new ArrayList<>();
        yPixelVec2Scaled = new ArrayList<>();
        xPixelVec3Scaled = new ArrayList<>();
        yPixelVec3Scaled = new ArrayList<>();

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
