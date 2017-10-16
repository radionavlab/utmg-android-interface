package utmg.android_interface.activity;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import org.ros.android.AppCompatRosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utmg.android_interface.model.entity.Quad;
import utmg.android_interface.view.canvas.DrawingCanvas;
import utmg.android_interface.DataShare;
import utmg.android_interface.DefaultCallback;
import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.R;
import utmg.android_interface.ROS.ROSNodeMain;
import utmg.android_interface.ROS.ROSNodeService;

public class MainActivity extends AppCompatRosActivity {

    ROSNodeMain nodeMain;
    ROSNodeService nodeService;
    private DrawingCanvas customCanvas;

    private Switch quad1Switch;

    SharedPreferences pref;

    private LinearLayout canvasSize;
    private int screenHeight;
    private int screenWidth;
    private float canvasWidth;
    private float canvasHeight;

    private final List<Quad> quads;
    private final List<Obstacle> obstacles;


    public static Context contextOfApplication;

    /**
     * Constructor.
     */
    public MainActivity() {
        super("MainActivity", "MainActivity");

        // Initialize quad entities
        this.quads = new ArrayList<>();
        this.initQuads();

        // Initialize obstacle entitites
        this.obstacles = new ArrayList<>();
        this.initObstacles();


    }

    /**
     * Initializes the various quad entities
     */
    private void initQuads() {
        this.quads.add(new Quad("Quad 1"));
    }

    /**
     * Initializes the various obstacle entities
     */
    private void initObstacles() {
        this.obstacles.add(new Obstacle("Obstacle 1"));
    }

    @Override
    protected void onCreate(
            final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contextOfApplication = getApplicationContext();

        // instantiating SharedPreferences
        pref = getSharedPreferences("Pref", 0);

        setContentView(R.layout.activity_main);

        // instantiating canvas
        canvasSize = (LinearLayout) findViewById(R.id.linLay);

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5)/pref.getFloat("newHeight", 3)));

        customCanvas = (DrawingCanvas) findViewById(R.id.signature_canvas);
        DataShare.save("canvasView",customCanvas);

        // instantiating z control slider
        FrameLayout sbLayout = (FrameLayout) findViewById(R.id.slider_frame_layout);
        sbLayout.getLayoutParams().height = (int) (screenHeight * 0.6);

        // instantiating local copy of input time history vectors

        // instantiating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            // compress arrays, send to path planner, wait to launch PreviewActivity until service finishes
            nodeService.getOptimizedTrajectories(new TrajectoryCallback(quads.size()){
                    public void onFinishedAll(boolean success){//once all quads have finished, this should be called. Who knows if it will work
                        Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        // Send FAB
        //// TODO: 7/25/2017 Dynamically generate action buttons
        final FloatingActionsMenu fabSent = (FloatingActionsMenu) findViewById(R.id.fab);
        final com.getbase.floatingactionbutton.FloatingActionButton sendAll = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendAll);
        final com.getbase.floatingactionbutton.FloatingActionButton sendQuad1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendQuad1);
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
                nodeMain.sendAll();
            }
        });

        sendQuad1.setOnClickListener(new sendOnClickListener());


        // Clear FAB
        //// TODO: 7/25/2017 Generate these automatically too ffs
        FloatingActionsMenu fabClear = (FloatingActionsMenu) findViewById(R.id.fab_clear);
        final com.getbase.floatingactionbutton.FloatingActionButton clearAll = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearAll);
        final com.getbase.floatingactionbutton.FloatingActionButton clearQuad1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearQuad1);

        fabClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
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

        clearQuad1.setOnClickListener(new clearOnClickListener());

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
                canvasWidth = pref.getFloat("width", 5);
                canvasHeight = pref.getFloat("height", 3);
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
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                if (!pref.getBoolean("debugMode", false)) {
//                    inMeters.setVisibility(View.INVISIBLE);
//                    quad2Pixel.setVisibility(View.INVISIBLE);
//                }
//                else {
//                    inMeters.setVisibility(View.VISIBLE);
//                    quad2Pixel.setVisibility(View.VISIBLE);
//
//                    inMeters.setText(Float.toString(mX) + "mX   " + Float.toString(mY) + "mY    ");
//                    quad2Pixel.setText(Float.toString(objectXToPixel("quad")) + "xdp    " + Float.toString(objectYToPixel("quad")) + "ydp");
//
//                    handler.postDelayed(this, 10);
//
//                }
//            }
//        };
//        runnable.run();

        // quad display config
        {
            // set quad size
            final ImageView quad1 = (ImageView) findViewById(R.id.quad1);

            quad1.getLayoutParams().height = (int) (screenHeight * 0.05);
            quad1.getLayoutParams().width = (int) (screenWidth * 0.05);
            //quad1.setColorFilter(DataShare.getInstance("quad1").getQuadColour());


            // quad control toggle switches
            //// TODO: 7/25/2017 Make a spinner
            quad1Switch = (Switch) findViewById(R.id.quad1Switch);

            quad1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    customCanvas.setQuad(buttonView.getText().toString());
                    setQuad(buttonView.getText().toString());
                }
                }
            });

            // show real-time location of the quads
            final Handler handlerQuad = new Handler();
            Runnable runnableQuad = new Runnable() {
                @Override
                public void run() {
                    // quad 1
                customCanvas.update();
                handlerQuad.postDelayed(this, 20);
                }
            };
            runnableQuad.run();
        }
    }



    // rescaling canvas proportions to (somewhat) fit screen depending on screen orientation
    // TODO
    public void newDimension(float w, float h) {
        float scale = w/h;
        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * scale);
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
        System.out.println("init");
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

    class sendOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Sent Quad3!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            nodeMain.sendQuad(((FloatingActionButton)v).getTitle());
        }
    }

    class clearOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Sent Quad3!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            customCanvas.clearQuad(((FloatingActionButton)v).getTitle());
        }
    }

    abstract class TrajectoryCallback extends DefaultCallback{
        int total,count;
        public TrajectoryCallback(int total){
            this.total=total;
        }
        @Override
        public void onFinished(boolean success){
            count++;
            if(total==count){
                onFinishedAll(success);
            }
        }
        public void onFinishedAll(boolean success){}

    }

}