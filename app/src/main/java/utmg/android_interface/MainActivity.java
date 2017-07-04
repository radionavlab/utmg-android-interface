package utmg.android_interface;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import org.ros.android.AppCompatRosActivity;
import org.ros.message.Time;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatRosActivity {

    ROSNodeMain nodeMain;
    ROSNodeService nodeService;
    private CanvasView customCanvas;
    private ArrayList<Float> xCoordVec1;
    private ArrayList<Float> yCoordVec1;
    private ArrayList<Float> zCoordVec1;

    private ArrayList<Float> xCoordVec2;
    private ArrayList<Float> yCoordVec2;
    private ArrayList<Float> zCoordVec2;

    private ArrayList<Float> xCoordVec3;
    private ArrayList<Float> yCoordVec3;
    private ArrayList<Float> zCoordVec3;

    private ArrayList<Time> timesVec1;
    private ArrayList<Time> timesVec2;
    private ArrayList<Time> timesVec3;

    private Switch quad1Switch;
    private Switch quad2Switch;
    private Switch quad3Switch;

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

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5)/pref.getFloat("newHeight", 3)));

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);

        Log.i("canvasSize", canvasSize.getLayoutParams().width + "\t" + canvasSize.getLayoutParams().height);

        // instantiating z control slider
        FrameLayout sbLayout = (FrameLayout) findViewById(R.id.slider_frame_layout);
        sbLayout.getLayoutParams().height = (int) (screenHeight * 0.6);

        // instantiating local copy of input time history vectors
        xCoordVec1 = new ArrayList<>();
        yCoordVec1 = new ArrayList<>();

        xCoordVec2 = new ArrayList<>();
        yCoordVec2 = new ArrayList<>();

        xCoordVec3 = new ArrayList<>();
        yCoordVec3 = new ArrayList<>();

        // instantiating local copy of time vectors
        timesVec1 = new ArrayList<>();
        timesVec2 = new ArrayList<>();
        timesVec3 = new ArrayList<>();

        // instantiating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // PreviewActivity Button
        final Button preview = (Button) findViewById(R.id.previewButton);
        preview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // TODO enable paths again for no servicing
                DataShare.setPath(1, customCanvas.getmPath1());
                DataShare.setPath(2, customCanvas.getmPath2());
                DataShare.setPath(3, customCanvas.getmPath3());

                DataShare.setPaint(1, customCanvas.getmPaint1());
                DataShare.setPaint(2, customCanvas.getmPaint2());
                DataShare.setPaint(3, customCanvas.getmPaint3());

                DataShare.setXPixelVec(1, customCanvas.xPixelVec1);
                DataShare.setXPixelVec(2, customCanvas.xPixelVec2);
                DataShare.setXPixelVec(3, customCanvas.xPixelVec3);

                DataShare.setYPixelVec(1, customCanvas.yPixelVec1);
                DataShare.setYPixelVec(2, customCanvas.yPixelVec2);
                DataShare.setYPixelVec(3, customCanvas.yPixelVec3);

                DataShare.setCurrentTime(1, customCanvas.getTimesVec1());
                DataShare.setCurrentTime(2, customCanvas.getTimesVec2());
                DataShare.setCurrentTime(3, customCanvas.getTimesVec3());

                xCoordVec1 = customCanvas.getxCoordVec1();
                yCoordVec1 = customCanvas.getyCoordVec1();
                zCoordVec1 = customCanvas.getzCoordVec1();
                timesVec1 = customCanvas.getTimesVec1();
                nodeService.setTraj1(xCoordVec1, yCoordVec1, zCoordVec1, timesVec1);

                while (DataShare.getServicedPath(1) == null) { } // TODO fix the sync check

                Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                startActivity(intent);
            }
        });

        // Send FAB
        final FloatingActionsMenu fabSent = (FloatingActionsMenu) findViewById(R.id.fab);
        final com.getbase.floatingactionbutton.FloatingActionButton sendAll = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendAll);
        final com.getbase.floatingactionbutton.FloatingActionButton sendQuad1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendQuad1);
        final com.getbase.floatingactionbutton.FloatingActionButton sendQuad2 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendQuad2);
        final com.getbase.floatingactionbutton.FloatingActionButton sendQuad3 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendQuad3);
        fabSent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        sendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Sent All!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                xCoordVec1 = customCanvas.getxCoordVec1();
                yCoordVec1 = customCanvas.getyCoordVec1();
                zCoordVec1 = customCanvas.getzCoordVec1();
                timesVec1 = customCanvas.getTimesVec1();
                nodeMain.setTraj1(xCoordVec1, yCoordVec1, zCoordVec1, timesVec1);

                xCoordVec2 = customCanvas.getxCoordVec2();
                yCoordVec2 = customCanvas.getyCoordVec2();
                zCoordVec2 = customCanvas.getzCoordVec2();
                timesVec2 = customCanvas.getTimesVec2();
                nodeMain.setTraj2(xCoordVec2, yCoordVec2, zCoordVec2, timesVec2);

                xCoordVec3 = customCanvas.getxCoordVec3();
                yCoordVec3 = customCanvas.getyCoordVec3();
                zCoordVec3 = customCanvas.getzCoordVec3();
                timesVec3 = customCanvas.getTimesVec3();
                nodeMain.setTraj3(xCoordVec3, yCoordVec3, zCoordVec3, timesVec3);
            }
        });

        sendQuad1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Sent Quad1!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                xCoordVec1 = customCanvas.getxCoordVec1();
                yCoordVec1 = customCanvas.getyCoordVec1();
                zCoordVec1 = customCanvas.getzCoordVec1();
                nodeMain.setTraj1(xCoordVec1, yCoordVec1, zCoordVec1, timesVec1);
            }
        });

        sendQuad2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Sent Quad2!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                xCoordVec2 = customCanvas.getxCoordVec2();
                yCoordVec2 = customCanvas.getyCoordVec2();
                zCoordVec2 = customCanvas.getzCoordVec2();
                nodeMain.setTraj2(xCoordVec2, yCoordVec2, zCoordVec2, timesVec2);
            }
        });

        sendQuad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Sent Quad3!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                xCoordVec3 = customCanvas.getxCoordVec3();
                yCoordVec3 = customCanvas.getyCoordVec3();
                zCoordVec3 = customCanvas.getzCoordVec3();
                nodeMain.setTraj3(xCoordVec3, yCoordVec3, zCoordVec3, timesVec3);
            }
        });


        // Clear FAB
        FloatingActionsMenu fabClear = (FloatingActionsMenu) findViewById(R.id.fab_clear);
        final com.getbase.floatingactionbutton.FloatingActionButton clearAll = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearAll);
        final com.getbase.floatingactionbutton.FloatingActionButton clearQuad1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearQuad1);
        final com.getbase.floatingactionbutton.FloatingActionButton clearQuad2 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearQuad2);
        final com.getbase.floatingactionbutton.FloatingActionButton clearQuad3 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearQuad3);
        fabClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customCanvas.clearCanvas();
                Snackbar.make(v, "Cleared All!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        clearQuad1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customCanvas.clearCanvasQuad1();
                Snackbar.make(v, "Cleared Quad1!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        clearQuad2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customCanvas.clearCanvasQuad2();
                Snackbar.make(v, "Cleared Quad2!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        clearQuad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customCanvas.clearCanvasQuad3();
                Snackbar.make(v, "Cleared Quad3!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // TextView for displaying z control slider value
        final TextView seekbarValue = (TextView) findViewById(R.id.seekbar_value);
        final SeekBar slider = (SeekBar) findViewById(R.id.slider);
        slider.getLayoutParams().width = sbLayout.getLayoutParams().height;
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
                seekbarH.postDelayed(this, 10);
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
        final TextView inMeters = (TextView) findViewById(R.id.inMeters);
        final TextView quad2Pixel = (TextView) findViewById(R.id.quad2pixel);
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (!pref.getBoolean("debugMode", false)) {
                    inMeters.setVisibility(View.INVISIBLE);
                    quad2Pixel.setVisibility(View.INVISIBLE);
                }
                else {
                    inMeters.setVisibility(View.VISIBLE);
                    quad2Pixel.setVisibility(View.VISIBLE);

                    mX = customCanvas.xMeters();
                    mY = customCanvas.yMeters();

                    inMeters.setText(Float.toString(mX) + "mX   " + Float.toString(mY) + "mY    ");
                    quad2Pixel.setText(Float.toString(objectXToPixel("quad")) + "xdp    " + Float.toString(objectYToPixel("quad")) + "ydp");

                    handler.postDelayed(this, 10);

                }
            }
        };
        runnable.run();

        // quad display config
        {

            // set quad size
            final ImageView quad1 = (ImageView) findViewById(R.id.quad1);
            final ImageView quad2 = (ImageView) findViewById(R.id.quad2);
            final ImageView quad3 = (ImageView) findViewById(R.id.quad3);

            quad1.getLayoutParams().height = (int) (screenHeight * 0.05);
            quad1.getLayoutParams().width = (int) (screenWidth * 0.05);
            //quad1.setColorFilter(DataShare.getInstance("quad1").getQuadColour());

            quad2.getLayoutParams().height = (int) (screenHeight * 0.05);
            quad2.getLayoutParams().width = (int) (screenWidth * 0.05);
            //quad2.setColorFilter(DataShare.getInstance("quad2").getQuadColour());

            quad3.getLayoutParams().height = (int) (screenHeight * 0.05);
            quad3.getLayoutParams().width = (int) (screenWidth * 0.05);
            //quad3.setColorFilter(DataShare.getInstance("quad3").getQuadColour());


            // quad control toggle switches
            quad1Switch = (Switch) findViewById(R.id.quad1Switch);
            quad2Switch = (Switch) findViewById(R.id.quad2Switch);
            quad3Switch = (Switch) findViewById(R.id.quad3Switch);

            quad1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        prefEditor.putInt("quadControl",1);
                        prefEditor.commit();

                        quad2Switch.setChecked(false);
                        quad3Switch.setChecked(false);
                    }
                }
            });

            quad2Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        prefEditor.putInt("quadControl",2);
                        prefEditor.commit();

                        quad1Switch.setChecked(false);
                        quad3Switch.setChecked(false);
                    }
                }
            });

            quad3Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        prefEditor.putInt("quadControl",3);
                        prefEditor.commit();

                        quad1Switch.setChecked(false);
                        quad2Switch.setChecked(false);
                    }
                }
            });

            // show real-time location of the quads
            final Handler handlerQuad = new Handler();
            Runnable runnableQuad = new Runnable() {
                @Override
                public void run() {
                    // quad 1
                    if (pref.getBoolean("quad1", false)) {
                        quad1.setVisibility(View.VISIBLE);
                        quad1Switch.setEnabled(true);
                        quad1.setX(objectXToPixel("quad1") - quad1.getWidth()/2);
                        quad1.setY(objectYToPixel("quad1") - quad1.getHeight()/2);

                        //Log.d("MA Quad1", "x: " + Float.toString(quad1.getX()) + "\ty:" + Float.toString(quad1.getY()));

                        //quad1.setImageAlpha( (int)(((DataShare.getInstance("quad1").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                    } else {
                        quad1.setVisibility((View.INVISIBLE));
                        quad1Switch.setEnabled(false);
                    }

                    // quad 2
                    if (pref.getBoolean("quad2", false)) {
                        quad2.setVisibility(View.VISIBLE);
                        quad2Switch.setEnabled(true);
                        quad2.setX(objectXToPixel("quad2") - quad2.getWidth()/2);
                        quad2.setY(objectYToPixel("quad2") - quad2.getHeight()/2);
                        quad2.setImageAlpha( (int)(((DataShare.getInstance("quad2").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                    } else {
                        quad2.setVisibility((View.INVISIBLE));
                        quad2Switch.setEnabled(false);
                    }

                    // quad 3
                    if (pref.getBoolean("quad3", false)) {
                        quad3.setVisibility(View.VISIBLE);
                        quad3Switch.setEnabled(true);
                        quad3.setX(objectXToPixel("quad3") - quad3.getWidth()/2);
                        quad3.setY(objectYToPixel("quad3") - quad3.getHeight()/2);
                        quad3.setImageAlpha( (int)(((DataShare.getInstance("quad3").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                    } else {
                        quad3.setVisibility((View.INVISIBLE));
                        quad3Switch.setEnabled(false);
                    }
                    //Log.i("vis", Boolean.toString(pref.getBoolean("quad",false)));

                    handlerQuad.postDelayed(this, 10);
                }
            };
            runnableQuad.run();

        }

        // obstacle display config TODO fix to match quad config
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

                    if (pref.getBoolean("sword", false)) {
                        sword.setVisibility(View.VISIBLE);
                        sword.setX(objectXToPixel("sword") - sword.getWidth()/2);
                        sword.setY(objectYToPixel("sword") - sword.getHeight()/2);
                    } else {
                        sword.setVisibility((View.INVISIBLE));
                    }
                    if (pref.getBoolean("obstacle1", false)) {
                        Log.d("MA Obst1", "x: " + Float.toString(obstacle1.getX()) + "\ty:" + Float.toString(obstacle1.getY()));
                        obstacle1.setVisibility(View.VISIBLE);
                        obstacle1.setX(objectXToPixel("obstacle1") - obstacle1.getWidth()/2);
                        obstacle1.setY(objectYToPixel("obstacle1") - obstacle1.getHeight()/2);

                    } else {
                        obstacle1.setVisibility((View.INVISIBLE));
                    }
                    if (pref.getBoolean("obstacle2", false)) {
                        obstacle2.setVisibility(View.VISIBLE);
                        obstacle2.setX(objectXToPixel("obstacle2") - obstacle2.getWidth()/2);
                        obstacle2.setY(objectYToPixel("obstacle2") - obstacle2.getHeight()/2);
                    } else {
                        obstacle2.setVisibility((View.INVISIBLE));
                    }
                    handlerObstacles.postDelayed(this, 10);
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
    }

    // transform specified object's x position to pixels
    // TODO redo to match new design architecture
    public float objectXToPixel(String item) {

        float normX = 0;

        if (item.equals("quad1")) {
            normX = (float) DataShare.getInstance("quad1").getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("quad2")) {
            Thing quad2 = DataShare.getInstance("quad2");
            normX = (float) quad2.getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("quad3")) {
            Thing quad3 = DataShare.getInstance("quad3");
            normX = (float) quad3.getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("sword")) {
            Thing sword = DataShare.getInstance("sword");
            normX = (float) sword.getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("obstacle1")) {
            Thing obstacle1 = DataShare.getInstance("obstacle1");
            normX = (float) obstacle1.getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("obstacle2")) {
            Thing obstacle2 = DataShare.getInstance("obstacle2");
            normX = (float) obstacle2.getY() / -pref.getFloat("newWidth",5);
        }

        float transX = normX * canvasSize.getWidth();
        float xCoord = canvasSize.getX() + canvasSize.getWidth()/2 + transX;

        return xCoord;
    }

    // transform specified object's y position to pixels
    public float objectYToPixel(String item) {

        float normY = 0;

        if (item.equals("quad1")) {
            Thing quad1 = DataShare.getInstance("quad1");
            normY = (float) quad1.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("quad2")) {
            Thing quad2 = DataShare.getInstance("quad2");
            normY = (float) quad2.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("quad3")) {
            Thing quad3 = DataShare.getInstance("quad3");
            normY = (float) quad3.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("sword")) {
            Thing sword = DataShare.getInstance("sword");
            normY = (float) sword.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("obstacle1")) {
            Thing obstacle1 = DataShare.getInstance("obstacle1");
            normY = (float) obstacle1.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("obstacle2")) {
            Thing obstacle2 = DataShare.getInstance("obstacle2");
            normY = (float) obstacle2.getX() / pref.getFloat("newHeight",3);
        }

        float transY = normY * canvasSize.getHeight();
        float yCoord = canvasSize.getY() + canvasSize.getHeight()/2 - transY;

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
//        if (id == R.id.action_roscam) {
//            startActivity(new Intent(MainActivity.this, ROSCam.class));
//        }

        if (id == R.id.action_settings) {
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

        nodeMain = new ROSNodeMain();
        nodeService = new ROSNodeService();

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

            //nodeConfiguration.setNodeName()

            nodeMainExecutor.execute(nodeMain, nodeConfiguration);
            nodeMainExecutor.execute(nodeService, nodeConfiguration);
        } catch (IOException e) {
            // Socket problem
            Log.e("MainActivity", "socket error trying to get networking information from the master uri");
        }

    }

}