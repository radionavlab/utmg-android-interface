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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
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

    Path path1 = new Path();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        //setupActionBar();

        // instantiating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        canvasSize = (LinearLayout) findViewById(R.id.linLay);

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5)/pref.getFloat("newHeight", 3)));


        // instantiate times vectors
        timesVec1 = new ArrayList<Float>();

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
            }
        });


        // initializing canvas
        //canvasSize = (LinearLayout) findViewById(R.id.linLay);

        // getting screen size
        //screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        //screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        // set current state of quad at 0
        DataShare.setSeekLoc(1, 0);
        DataShare.setSeekLoc(2, 0);
        DataShare.setSeekLoc(3, 0);

        // getting arrays from DataShare
        xPixelVec1 = DataShare.getXPixelVec(1);
        yPixelVec1 = DataShare.getYPixelVec(1);

        // boolean playBackState keeps track whether or not PreviewActivity is ready to start
        // playback. It prevents the play/pause button from replaying from the beginning
        // concurrently if the user only paused the playback.
        DataShare.setPlayBackState(true);

        // initializing paths
        path1 = DataShare.getPath(1);

        if (pref.getBoolean("serviceToggle", false)) {

            // initializing arrays for quad1
            xPixelVec1 = new ArrayList<>();
            yPixelVec1 = new ArrayList<>();
            xMeterVec1 = new ArrayList<>();
            yMeterVec1 = new ArrayList<>();

            // calls convertROSPathToPixel - self explanatory
            // TODO enable checks after they are correctly figured out
            Log.i("PreviewActivity", "Calling convertROSPathToPixelVec for quad 111");
            convertROSPathToPixelVec(1, DataShare.getServicedPath(1));
            path1 = DataShare.getPath(1);
        }

        previewCanvas = (PreviewCanvas) findViewById(R.id.preview_canvas);
        previewCanvas.callOnDraw();


        xPixelVec1 = DataShare.getXPixelVec(1);
        yPixelVec1 = DataShare.getYPixelVec(1);


        // quad display config
        {
            // set quad size
            final ImageView quad1 = (ImageView) findViewById(R.id.quad1);

            quad1.getLayoutParams().height = (int) (screenHeight * 0.05);
            quad1.getLayoutParams().width = (int) (screenWidth * 0.05);

            // show real-time location of the quads with GPS
            final Handler handlerQuad = new Handler();
            Runnable runnableQuad = new Runnable() {
                @Override
                public void run() {
                    // quad 1
                    if (pref.getBoolean("quad1", true)) {
                        quad1.setVisibility(View.VISIBLE);
                        Thing quad_thing_1 = DataShare.getInstance("quad1"); // I hate this.

                        double x = quad_thing_1.getX();// + pref.getFloat("newHeight", 3)/2;
                        double y = quad_thing_1.getY();// + pref.getFloat("newWidth", 5)/2;
                        //Log.i("PreviewActivity","Quad 1: " + Double.toString(x) +  "\ty: " + Double.toString(y) );

                        //quad1.setX(quad_thing_1.getPixelX());
                        //quad1.setY(quad_thing_1.getPixelY());

                        //Log.i("PreviewActivity Quad1", "x: " + Float.toString(quad1.getX()) + "\ty:" + Float.toString(quad1.getY()));

                        quad1.setX((float) xMeterToPixel(x, y) - quad1.getWidth()/2);
                        quad1.setY((float) yMeterToPixel(x, y) - quad1.getHeight()/2);
                    } else {
                        quad1.setVisibility((View.INVISIBLE));
                    }

                    handlerQuad.postDelayed(this, 10);
                }
            };
            runnableQuad.run();

        }


        // obstacle display config TODO fix to match quad config
        {
            // set obstacle size

            float heightScale = DataShare.getMainCanvasHeight() / pref.getFloat("newHeight",3); // pixels/meter
            float widthScale = DataShare.getMainCanvasWidth() / pref.getFloat("newWidth",5); // pixels/meter

            final ImageView obstacle1 = (ImageView) findViewById(R.id.obstacle1);
            obstacle1.setMaxHeight((int) (heightScale*1.5));
            obstacle1.setMaxWidth((int) (widthScale*1.5));

            final ImageView obstacle2 = (ImageView) findViewById(R.id.obstacle2);
            obstacle2.setMaxHeight((int) (heightScale*0.2));
            obstacle2.setMaxWidth((int) (widthScale*0.2));

            if (pref.getBoolean("obstacle1", false)) {
                obstacle1.setVisibility(View.VISIBLE);

                Thing obstacle_thing_1 = DataShare.getInstance("obstacle1"); // I hate this.

                double x = obstacle_thing_1.getX() + pref.getFloat("newHeight", 3)/2;
                double y = obstacle_thing_1.getY() + pref.getFloat("newWidth", 5)/2;
                Log.i("PreviewActivity","Obstacle x: " + Double.toString(x) +  "\ty: " + Double.toString(y) );

                //obstacle1.setX(obstacle_thing_1.getPixelX());
                //obstacle1.setY(obstacle_thing_1.getPixelY());

                Log.i("PreviewActivity Obst1", "x: " + Float.toString(obstacle1.getX()) + "\ty:" + Float.toString(obstacle1.getY()));

                obstacle1.setX((float) xMeterToPixel(x, y));
                obstacle1.setY((float) yMeterToPixel(x, y));

            } else {
                obstacle1.setVisibility((View.INVISIBLE));
            }


            if (pref.getBoolean("obstacle2", false)) {
                obstacle2.setVisibility(View.VISIBLE);

                Thing obstacle_thing_2 = DataShare.getInstance("obstacle2");

                double x = obstacle_thing_2.getX() + pref.getFloat("newHeight", 3)/2;
                double y = obstacle_thing_2.getY() + pref.getFloat("newWidth", 5)/2;
                Log.i("PreviewActivity","Balloon x: " + Double.toString(x) +  "\ty: " + Double.toString(y) );

                //obstacle2.setX(obstacle_thing_2.getPixelX());
                //obstacle2.setY(obstacle_thing_2.getPixelY());

                Log.i("PreviewActivity Obst2", "x: " + Float.toString(obstacle2.getX()) + "\ty:" + Float.toString(obstacle2.getY()));

                obstacle2.setX((float) xMeterToPixel(x, y));
                obstacle2.setY((float) yMeterToPixel(x, y));

            } else {
                obstacle2.setVisibility((View.INVISIBLE));
            }

        }
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
                    //scalePath(mPath1);
                    //scaleDot1();
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
