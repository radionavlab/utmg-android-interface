package utmg.android_interface;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
    private ArrayList<Float> xCompressed1;
    private ArrayList<Float> yCompressed1;
    private ArrayList<Float> zCompressed1;
    private ArrayList<Time> timeCompressed1;

    private ArrayList<Float> xCoordVec2;
    private ArrayList<Float> yCoordVec2;
    private ArrayList<Float> zCoordVec2;
    private ArrayList<Float> xCompressed2;
    private ArrayList<Float> yCompressed2;
    private ArrayList<Float> zCompressed2;
    private ArrayList<Time> timeCompressed2;

    private ArrayList<Float> xCoordVec3;
    private ArrayList<Float> yCoordVec3;
    private ArrayList<Float> zCoordVec3;
    private ArrayList<Float> xCompressed3;
    private ArrayList<Float> yCompressed3;
    private ArrayList<Float> zCompressed3;
    private ArrayList<Time> timeCompressed3;

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

        DataShare.setMainCanvasHeight(canvasSize.getLayoutParams().height);
        DataShare.setMainCanvasWidth(canvasSize.getLayoutParams().width);

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

        // instantiating compressed copy of coordinate arrays
        xCompressed1 = new ArrayList<>();
        yCompressed1 = new ArrayList<>();
        zCompressed1 = new ArrayList<>();
        timeCompressed1 = new ArrayList<>();

        xCompressed2 = new ArrayList<>();
        yCompressed2 = new ArrayList<>();
        zCompressed2 = new ArrayList<>();
        timeCompressed2 = new ArrayList<>();

        xCompressed3 = new ArrayList<>();
        yCompressed3 = new ArrayList<>();
        zCompressed3 = new ArrayList<>();
        timeCompressed3 = new ArrayList<>();

        // instantiating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        xCoordVec1 = DataShare.getXPixelVec(1);
        xCoordVec2 = DataShare.getXPixelVec(2);
        xCoordVec3 = DataShare.getXPixelVec(3);

        yCoordVec1 = DataShare.getYPixelVec(1);
        yCoordVec2 = DataShare.getYPixelVec(2);
        yCoordVec3 = DataShare.getYPixelVec(3);

        // Button to terminate app so it doesn't have to be done manually
        Button terminate = (Button)this.findViewById(R.id.kill_button);
        terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });


        // PreviewActivity Button
        final Button preview = (Button) findViewById(R.id.previewButton);
        preview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // TODO enable paths again for no servicing
                if(!pref.getBoolean("serviceToggle", false)) {
                    DataShare.setPath(1, customCanvas.getmPath1());
                    DataShare.setPath(2, customCanvas.getmPath2());
                    DataShare.setPath(3, customCanvas.getmPath3());
                }

                DataShare.setPaint(1, customCanvas.getmPaint1());
                DataShare.setPaint(2, customCanvas.getmPaint2());
                DataShare.setPaint(3, customCanvas.getmPaint3());

                DataShare.setXPixelVec(1, customCanvas.xPixelVec1);
                DataShare.setXPixelVec(2, customCanvas.xPixelVec2);
                DataShare.setXPixelVec(3, customCanvas.xPixelVec3);

//                Log.i("xPixelVec1", "" + DataShare.getXPixelVec(1).size());
//                Log.i("xPixelVec2", "" + DataShare.getXPixelVec(2).size());
//                Log.i("xPixelVec3", "" + DataShare.getXPixelVec(3).size());

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

                xCoordVec2 = customCanvas.getxCoordVec2();
                yCoordVec2 = customCanvas.getyCoordVec2();
                zCoordVec2 = customCanvas.getzCoordVec2();
                timesVec2 = customCanvas.getTimesVec2();

                xCoordVec3 = customCanvas.getxCoordVec3();
                yCoordVec3 = customCanvas.getyCoordVec3();
                zCoordVec3 = customCanvas.getzCoordVec3();
                timesVec3 = customCanvas.getTimesVec3();

                // compress arrays, send to path planner, wait to launch PreviewActivity until service finishes
                //if(pref.getBoolean("serviceToggle",false) && CONNECTED TO ROS) {
                if(pref.getBoolean("serviceToggle",false)) {
                    Log.i("MainActivity", "Service Toggle enabled. Will attempt to contact service.");
                    if (xCoordVec1.size() != 0) {
                        compressArrays(1);
                        DataShare.setXCompressedVec(1, xCompressed1);
                        DataShare.setYCompressedVec(1, yCompressed1);
                        nodeService.setTraj(1, xCompressed1, yCompressed1, zCompressed1, timeCompressed1);
                        while (DataShare.getServicedPath(1) == null) {
                            //Log.i("Quad111 Waiting ", " yessss ");
                        }
                    }
                    if (xCoordVec2.size() != 0) {
                        compressArrays(2);
                        DataShare.setXCompressedVec(2, xCompressed2);
                        DataShare.setYCompressedVec(2, yCompressed2);
                        nodeService.setTraj(2 ,xCompressed2, yCompressed2, zCompressed2, timeCompressed2);
                        while (DataShare.getServicedPath(2) == null) {
                            //Log.i("Quad222 Waiting ", " yessss ");
                        }
                    }
                    if (xCoordVec3.size() != 0) {
                        compressArrays(3);
                        DataShare.setXCompressedVec(3, xCompressed3);
                        DataShare.setYCompressedVec(3, yCompressed3);
                        nodeService.setTraj(3 ,xCompressed3, yCompressed3, zCompressed3, timeCompressed3);
                        while (DataShare.getServicedPath(3) == null) {
                            //Log.i("Quad333 Waiting ", " yessss ");
                        }
                    }
                }

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
                timesVec1 = customCanvas.getTimesVec1();
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
                timesVec2 = customCanvas.getTimesVec2();
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
                timesVec3 = customCanvas.getTimesVec3();
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

            // show real-time  location of the quads
            final Handler handlerQuad = new Handler();
            Runnable runnableQuad = new Runnable() {
                @Override
                public void run() {
                    // quad 1
                    if (pref.getBoolean("quad1", true)) {
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
                    if (pref.getBoolean("quad2", true)) {
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
                    if (pref.getBoolean("quad3", true)) {
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

    public void compressArrays(int quad) {
        int i = 0;
        float siddarth = 1;
        float x1, x2 = 0;
        float y1, y2 = 0;
        float slope1 = 0;
        float slope2 = 0;
        int q = 0;
        int u = 0;
        int w = 0;
        int o = 0;

        switch (quad) {
            case 1:
                Log.i(" 1111111111 ", "" + xCoordVec1.size());
                q = 0;
                while ( q <= xCoordVec1.size()-1) {
                    Log.i(" 11XValues ", "" + xCoordVec1.get(q));
                    q++;
                }
                u = 0;
                while ( u <= yCoordVec1.size()-1) {
                    Log.i(" 11YValues ", "" + yCoordVec1.get(u));
                    u++;
                }
                xCompressed1.add(xCoordVec1.get(0));
                yCompressed1.add(yCoordVec1.get(0));
                zCompressed1.add(zCoordVec1.get(0));
                timeCompressed1.add(timesVec1.get(0));
                while (i + 1 < xCoordVec1.size() - 1) {
                    x1 = xCoordVec1.get(i);
                    x2 = xCoordVec1.get(i + 1);
                    y1 = yCoordVec1.get(i);
                    y2 = yCoordVec1.get(i + 1);
//                    slope1 = (y2 - y1)/(x2 - x1);
                    slope1 = (float)Math.atan2((y2 - y1),(x2 - x1));
                    if(Math.abs(slope1 - slope2) > siddarth) {
                        xCompressed1.add(x2);
                        yCompressed1.add(y2);
                        timeCompressed1.add(timesVec1.get(i+1));
                        zCompressed1.add(zCoordVec1.get(i+1));
                        slope2 = slope1;
                    }

                    i++;
                }
                xCompressed1.add(xCoordVec1.get(xCoordVec1.size()-1));
                yCompressed1.add(yCoordVec1.get(yCoordVec1.size()-1));
                zCompressed1.add(zCoordVec1.get(zCoordVec1.size()-1));
                timeCompressed1.add(timesVec1.get(timesVec1.size()-1));
                Log.i(" 1C1C1C1C1C1 ", "" + xCompressed1.size());
                w = 0;
                while ( w <= xCompressed1.size()-1) {
                    Log.i(" 1CXValues ", "" + xCompressed1.get(w));
                    w++;
                }
                o = 0;
                while ( o <= yCompressed1.size()-1) {
                    Log.i(" 1CYValues ", "" + yCompressed1.get(o));
                    o++;
                }
                break;

            case 2:
                Log.i(" 2222222222 ", "" + xCoordVec2.size());
                q = 0;
                while ( q <= xCoordVec2.size()-1) {
                    Log.i(" 22XValues ", "" + xCoordVec2.get(q));
                    q++;
                }
                u = 0;
                while ( u <= yCoordVec2.size()-1) {
                    Log.i(" 22YValues ", "" + yCoordVec2.get(u));
                    u++;
                }
                xCompressed2.add(xCoordVec2.get(0));
                yCompressed2.add(yCoordVec2.get(0));
                zCompressed2.add(zCoordVec2.get(0));
                timeCompressed2.add(timesVec2.get(0));
                while (i + 1 < xCoordVec2.size() - 1) {
                    x1 = xCoordVec2.get(i);
                    x2 = xCoordVec2.get(i + 1);
                    y1 = yCoordVec2.get(i);
                    y2 = yCoordVec2.get(i + 1);
//                    slope1 = (y2 - y1)/(x2 - x1);
                    slope1 = (float)Math.atan2((y2 - y1),(x2 - x1));
                    if(Math.abs(slope1 - slope2) > siddarth) {
                        xCompressed2.add(x2);
                        yCompressed2.add(y2);
                        timeCompressed2.add(timesVec2.get(i+1));
                        zCompressed2.add(zCoordVec2.get(i+1));
                        slope2 = slope1;
                    }

                    i++;
                }
                xCompressed2.add(xCoordVec2.get(xCoordVec2.size()-1));
                yCompressed2.add(yCoordVec2.get(yCoordVec2.size()-1));
                zCompressed2.add(zCoordVec2.get(zCoordVec2.size()-1));
                timeCompressed2.add(timesVec2.get(timesVec2.size()-1));
                Log.i(" 2C2C2C2C2C2C2C ", "" + xCompressed2.size());
                w = 0;
                while ( w <= xCompressed2.size()-1) {
                    Log.i(" 2CXValues ", "" + xCompressed2.get(w));
                    w++;
                }
                o = 0;
                while ( o <= yCompressed2.size()-1) {
                    Log.i(" 2CYValues ", "" + yCompressed2.get(o));
                    o++;
                }
                break;
            
            case 3:
                Log.i(" 33333333333 ", "" + xCoordVec3.size());
                q = 0;
                while ( q <= xCoordVec3.size()-1) {
                    Log.i(" 33XValues ", "" + xCoordVec3.get(q));
                    q++;
                }
                u = 0;
                while ( u <= yCoordVec3.size()-1) {
                    Log.i(" 33YValues ", "" + yCoordVec3.get(u));
                    u++;
                }
                xCompressed3.add(xCoordVec3.get(0));
                yCompressed3.add(yCoordVec3.get(0));
                zCompressed3.add(zCoordVec3.get(0));
                timeCompressed3.add(timesVec3.get(0));
                while (i + 1 < xCoordVec3.size() - 1) {
                    x1 = xCoordVec3.get(i);
                    x2 = xCoordVec3.get(i + 1);
                    y1 = yCoordVec3.get(i);
                    y2 = yCoordVec3.get(i + 1);
//                    slope1 = (y2 - y1)/(x2 - x1);
                    slope1 = (float)Math.atan2((y2 - y1),(x2 - x1));
                    if(Math.abs(slope1 - slope2) > siddarth) {
                        xCompressed3.add(x2);
                        yCompressed3.add(y2);
                        timeCompressed3.add(timesVec3.get(i+1));
                        zCompressed3.add(zCoordVec3.get(i+1));
                        slope2 = slope1;
                    }
                    i++;
                }
                xCompressed3.add(xCoordVec3.get(xCoordVec3.size()-1));
                yCompressed3.add(yCoordVec3.get(yCoordVec3.size()-1));
                zCompressed3.add(zCoordVec3.get(zCoordVec3.size()-1));
                timeCompressed3.add(timesVec3.get(timesVec3.size()-1));
                Log.i(" 3C3C3C3C3C3C3C ", "" + xCompressed3.size());
                w = 0;
                while ( w <= xCompressed3.size()-1) {
                    Log.i(" 3CXValues ", "" + xCompressed3.get(w));
                    w++;
                }
                o = 0;
                while ( o <= yCompressed3.size()-1) {
                    Log.i(" 3CYValues ", "" + yCompressed3.get(o));
                    o++;
                }
                break;

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