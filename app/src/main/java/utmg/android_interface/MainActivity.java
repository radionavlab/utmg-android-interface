package utmg.android_interface;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.ros.android.AppCompatRosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatRosActivity {

    ROSNode node;
    private CanvasView customCanvas;
    private ArrayList<Float> xCoordVec;
    private ArrayList<Float> yCoordVec;
    private ArrayList<Float> zCoordVec;

    private float mX;
    private float mY;

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private LinearLayout canvasSize;
    private int screenHeight;
    private int screenWidth;
    private float canvasWidth;
    private float canvasHeight;

    public static Context contextOfApplication;

    public MainActivity() { super("MainActivity", "MainActivity"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contextOfApplication = getApplicationContext();

        // instantiating SharedPreferences
        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        // instantiating canvas
        canvasSize = (LinearLayout) findViewById(R.id.linLay);

        int orientation = this.getResources().getConfiguration().orientation;

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5)/pref.getFloat("newHeight", 3)));

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);

        Log.i("canvasSize", canvasSize.getLayoutParams().width + "\t" + canvasSize.getLayoutParams().height);

        // instantiating z control slider
        FrameLayout sbLayout = (FrameLayout) findViewById(R.id.slider_frame_layout);
        sbLayout.getLayoutParams().height = (int) (screenHeight * 0.6);

        SeekBar sb = (SeekBar) findViewById(R.id.slider);
        sb.getLayoutParams().width = sbLayout.getLayoutParams().height;

        // instantiating local copy of input time history vectors
        xCoordVec = new ArrayList<>();
        yCoordVec = new ArrayList<>();

        // instantiating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Send FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Sent!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                xCoordVec = customCanvas.getxCoordVec();
                yCoordVec = customCanvas.getyCoordVec();
                zCoordVec = customCanvas.getzCoordVec();

                node.setTraj(xCoordVec, yCoordVec, zCoordVec);
            }
        });

        // Clear FAB
        FloatingActionButton fabClear = (FloatingActionButton) findViewById(R.id.fab_clear);
        fabClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                customCanvas.clearCanvas();
                Snackbar.make(view, "Cleared!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // TextView for displaying z control slider value
        final TextView seekbarValue = (TextView) findViewById(R.id.seekbar_value);
        final SeekBar slider = (SeekBar) findViewById(R.id.slider);
//        slider.getLayoutParams().width = (int)(screenHeight * 0.65);
        final Handler seekbarH = new Handler();
        Runnable seekbarR = new Runnable() {
            @Override
            public void run() {
                int max = (int) pref.getFloat("newAltitude", 2);
                slider.setMax(max * 100);
                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float value = ((float) progress / 100);
                        seekbarValue.setText(Float.toString(value));
                        zObject.getInstance().setZ(value);
                    }
                });
                seekbarH.postDelayed(this, 0);
            }
        };
        seekbarR.run();

        // Get new dimensions from CanvasSizeActivity
        final TextView newDimensionText = (TextView) findViewById(R.id.new_dimensions);
        final Handler canvasH = new Handler();
        Runnable canvasR = new Runnable() {
            @Override
            public void run() {
                canvasWidth = pref.getFloat("newWidth", 5);
                canvasHeight = pref.getFloat("newHeight", 3);
                newDimensionText.setText(Float.toString(canvasWidth) + "m, " + Float.toString(canvasHeight) + "m");

                newDimension(canvasWidth, canvasHeight);
                canvasH.postDelayed(this, 1000);
            }
        };
        canvasR.run();


        // display position of quad in meters
        final TextView xMeters = (TextView) findViewById(R.id.meterX);
        final TextView yMeters = (TextView) findViewById(R.id.meterY);
        final TextView quad2Pixel = (TextView) findViewById(R.id.quad2pixel);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (pref.getBoolean("debugMode", false) == false) {
                    xMeters.setVisibility(View.INVISIBLE);
                    yMeters.setVisibility(View.INVISIBLE);
                    quad2Pixel.setVisibility(View.INVISIBLE);
                }
                else if (pref.getBoolean("debugMode", false) == true) {
                    xMeters.setVisibility(View.VISIBLE);
                    yMeters.setVisibility(View.VISIBLE);
                    quad2Pixel.setVisibility(View.VISIBLE);

                    mX = customCanvas.xMeters();
                    mY = customCanvas.yMeters();
                    xMeters.setText(Float.toString(mX) + "m     ");
                    yMeters.setText(Float.toString(mY) + "m     ");

                    quad2Pixel.setText(Float.toString(objectXToPixel("quad")) + "xdp    " + Float.toString(objectYToPixel("quad")) + "ydp");

                    handler.postDelayed(this, 10);

                }
            }
        };
        runnable.run();

        // quad display config
        {
            // set quad size
            final ImageView quad = (ImageView) findViewById(R.id.quad);
            quad.setMaxHeight((int) (screenHeight * 0.5));
            quad.setMaxWidth((int) (screenWidth * 0.5));

            // show real-time location of the quad
            final Handler handler2 = new Handler();
            Runnable runnable2 = new Runnable() {
                @Override
                public void run() {
                    if (pref.getBoolean("quad", false) == true) {
                        quad.setVisibility(View.VISIBLE);
                        quad.setX(objectXToPixel("quad"));// + quad.getWidth()/2);
                        quad.setY(objectYToPixel("quad"));// + quad.getHeight()/2);
                    } else if (pref.getBoolean("quad", false) == false) {
                        quad.setVisibility((View.INVISIBLE));
                    }
                    //Log.i("vis", Boolean.toString(pref.getBoolean("quad",false)));


                    handler2.postDelayed(this, 0);
                }
            };
            runnable2.run();
        }

        // obstacle display config
        {
            // set obstacle size
            final ImageView sword = (ImageView) findViewById(R.id.sword);
            sword.setMaxHeight((int) (screenHeight * 0.5));
            sword.setMaxWidth((int) (screenWidth * 0.5));

            final ImageView obstacle1 = (ImageView) findViewById(R.id.obstacle1);
            obstacle1.setMaxHeight((int) (screenHeight * 0.25));
            obstacle1.setMaxWidth((int) (screenWidth * 0.25));

            final ImageView obstacle2 = (ImageView) findViewById(R.id.obstacle2);
            obstacle2.setMaxHeight((int) (screenHeight * 0.25));
            obstacle2.setMaxWidth((int) (screenWidth * 0.25));

            // show location of the obstacle
            final Handler handlerObstacles = new Handler();
            Runnable runnableObstacles = new Runnable() {
                @Override
                public void run() {

                    if (pref.getBoolean("sword", false) == true) {
                        sword.setVisibility(View.VISIBLE);
                        sword.setX(objectXToPixel("sword"));// + sword.getWidth()/2);
                        sword.setY(objectYToPixel("sword"));// + sword.getHeight()/2);
                    } else if (pref.getBoolean("sword", false) == false) {
                        sword.setVisibility((View.INVISIBLE));
                    }
                    if (pref.getBoolean("obstacle1", false) == true) {
                        obstacle1.setVisibility(View.VISIBLE);
                        obstacle1.setX((objectXToPixel("obstacle1")));
                        obstacle1.setY((objectYToPixel("obstacle1")));

                    } else if (pref.getBoolean("obstacle1", false) == false) {
                        obstacle1.setVisibility((View.INVISIBLE));
                    }
                    if (pref.getBoolean("obstacle2", false) == true) {
                        obstacle2.setVisibility(View.VISIBLE);
                        obstacle2.setX((objectXToPixel("obstacle2")));
                        obstacle2.setY((objectYToPixel("obstacle2")));
                    } else if (pref.getBoolean("obstacle2", false) == false) {
                        obstacle2.setVisibility((View.INVISIBLE));
                    }

                    handlerObstacles.postDelayed(this, 0);
                }
            };
            runnableObstacles.run();
        }

    }

    // rescaling canvas proportions to (somewhat) fit screen depending on screen orientation
    // TODO
    public void newDimension(float w, float h) {

        float scale = w/h;
        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * scale);

//        if (h > w) {
//            float scale = h/w;
//            canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
//            canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height / scale);
//        }
//        else if (w > h) {
//            float scale = w/h;
//            canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
//            canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * scale);
//            canvasSize.getLayoutParams().width = (int) (screenWidth * 0.95);
//            canvasSize.getLayoutParams().height = (int) (canvasSize.getLayoutParams().width / scale);
//        }
    }

    // transform specified object's x position to pixels
    // TODO redo to match new design architecture
    public float objectXToPixel(String item) {

        float normX = 0;

        if (item.equals("quad")) {
            Thing quad1 = DataShare.getInstance("quad1");
            normX = (float) quad1.getY() / -5;
        }
        else if (item.equals("sword")) {
            Thing sword = DataShare.getInstance("sword");
            normX = (float) sword.getY() / -5;
        }
        else if (item.equals("obstacle1")) {
            Thing obstacle1 = DataShare.getInstance("obstacle1");
            normX = (float) obstacle1.getY() / -5;
        }
        else if (item.equals("obstacle2")) {
            Thing obstacle2 = DataShare.getInstance("obstacle2");
            normX = (float) obstacle2.getY() / -5;
        }

        float transX = normX * customCanvas.getWidth();
        float xCoord = transX + customCanvas.getCenterX() + customCanvas.getLeft();

        return xCoord;
    }

    // transform specified object's y position to pixels
    // TODO redo to match new design architecture
    public float objectYToPixel(String item) {

        float normY = 0;

        if (item.equals("quad")) {
            Thing quad1 = DataShare.getInstance("quad1");
            normY = (float) quad1.getX() / 3;
        }
        else if (item.equals("sword")) {
            Thing sword = DataShare.getInstance("sword");
            normY = (float) sword.getX() / 3;
        }
        else if (item.equals("obstacle1")) {
            Thing obstacle1 = DataShare.getInstance("obstacle1");
            normY = (float) obstacle1.getX() / 3;
        }
        else if (item.equals("obstacle2")) {
            Thing obstacle2 = DataShare.getInstance("obstacle2");
            normY = (float) obstacle2.getX() / 3;
        }

        float transY = normY * customCanvas.getHeight();
        float yCoord = (-transY + customCanvas.getCenterY() + customCanvas.getTop());

        return yCoord;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_roscam) {
            startActivity(new Intent(MainActivity.this, ROSCam.class));
        }

        else if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
//        rosTextView.setText("test");
//        rosTextView.setTopicName("testtopic");
//        rosTextView.setMessageType("std_msgs/String");

        node = new ROSNode();

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

            //nodeConfiguration.setNodeName()

            nodeMainExecutor.execute(node, nodeConfiguration);
        } catch (IOException e) {
            // Socket problem
            Log.e("MainActivity", "socket error trying to get networking information from the master uri");
        }

    }

}