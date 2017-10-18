package utmg.android_interface.activity;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import org.ros.android.AppCompatRosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utmg.android_interface.ROS.IROSCallback;
import utmg.android_interface.ROS.callbacks.TrajectoryOptimizerCallback;
import utmg.android_interface.controller.AltitudeSeekBarHandler;
import utmg.android_interface.model.entity.Quad;
import utmg.android_interface.view.canvas.DrawingCanvas;
import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.R;
import utmg.android_interface.ROS.ROSNodeMain;
import utmg.android_interface.ROS.ROSNodeService;

public class MainActivity extends AppCompatRosActivity {


    private static final float CANVAS_SCREEN_RATIO = 0.65f;
    private static final float SLIDER_SCREEN_RATIO = 0.6f;

    ROSNodeMain nodeMain;
    ROSNodeService nodeService;
    private DrawingCanvas customCanvas;

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

        // Get the shared preferences
        pref = getSharedPreferences("Pref", 0);

        // Set the content layout
        setContentView(R.layout.activity_main);

        // instantiating canvas
        canvasSize = (LinearLayout) findViewById(R.id.linLay);

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5)/pref.getFloat("newHeight", 3)));

        customCanvas = (DrawingCanvas) findViewById(R.id.signature_canvas);

        // instantiating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    private void initSlider() {
        final FrameLayout sbLayout = (FrameLayout) findViewById(R.id.slider_frame_layout);
        final int sliderLength = (int) (screenHeight * SLIDER_SCREEN_RATIO);
        sbLayout.getLayoutParams().height = sliderLength;

        final SeekBar slider = (SeekBar) findViewById(R.id.slider);
        slider.getLayoutParams().width = sliderLength;

        int max = (int) pref.getFloat("newAltitude", 2);
        slider.setMax(max * 100);

        slider.setOnSeekBarChangeListener(new AltitudeSeekBarHandler((TextView) findViewById(R.id.seekbar_value)));
    }

    @Override
    protected void init(
            final NodeMainExecutor nodeMainExecutor) {
        nodeMain = new ROSNodeMain();
        nodeService = new ROSNodeService();

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

            nodeMainExecutor.execute(nodeMain, nodeConfiguration);
            nodeMainExecutor.execute(nodeService, nodeConfiguration);
        } catch (IOException e) {
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
        public void onClick(final View view) {
            Snackbar.make(view, "Sent Quad3!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            customCanvas.clearQuad(((FloatingActionButton)view).getTitle());
        }
    }

    // rescaling canvas proportions to (somewhat) fit screen depending on screen orientation
    // TODO
    public void newDimension(
            final float width,
            final float height) {
        float aspectRatio = width/height;
        canvasSize.getLayoutParams().height = (int) (screenHeight * CANVAS_SCREEN_RATIO);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * aspectRatio);
    }

    @Override
    public boolean onCreateOptionsMenu(
            final Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     * @param item The MenuItem selected
     * @return TODO What does this return?
     */
    @Override
    public boolean onOptionsItemSelected(
            final MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private void initButtons() {

        // Button to terminate app so it doesn't have to be done manually
        final Button terminateProgramButton = (Button)this.findViewById(R.id.kill_button);
        terminateProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                finish();
                System.exit(0);
            }
        });

        // PreviewActivity Button
        final Button previewButton = (Button) findViewById(R.id.previewButton);
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // compress arrays, send to path planner, wait to launch PreviewActivity until service finishes
                nodeService.getOptimizedTrajectories(new TrajectoryOptimizerCallback(quads.size(), (Void) -> {
                            Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                            MainActivity.this.startActivity(intent);
                            return Void;
                        })
                );
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
    }

    private void initHandlers() {
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

        // set quad size
        final ImageView quad1 = (ImageView) findViewById(R.id.quad1);

        quad1.getLayoutParams().height = (int) (screenHeight * 0.05);
        quad1.getLayoutParams().width = (int) (screenWidth * 0.05);

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