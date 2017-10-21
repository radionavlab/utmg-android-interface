package utmg.android_interface.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
import java.util.Properties;

import utmg.android_interface.controller.AltitudeSeekBarHandler;
import utmg.android_interface.controller.ClearAllTrajectoriesButtonHandler;
import utmg.android_interface.controller.ClearTrajectoryButtonHandler;
import utmg.android_interface.controller.DrawingEndTouchHandler;
import utmg.android_interface.controller.DrawingMoveTouchHandler;
import utmg.android_interface.controller.DrawingStartTouchHandler;
import utmg.android_interface.controller.OnTouchEventDispatcher;
import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.DrawingCanvas;
import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.R;
import utmg.android_interface.ROS.ROSNodeMain;
import utmg.android_interface.ROS.ROSNodeService;
import utmg.android_interface.view.entitiyView.TrajectoryView;

public class MainActivity extends AppCompatRosActivity {

    /* Config file names */
    private static final String CONFIG_FILE_NAME = "app_config";

    /* Default constants */
    private static final float MAX_CANVAS_HEIGHT_TO_SCREEN_HEIGHT = 0.75f;
    private static final float MAX_CANVAS_WIDTH_TO_SCREEN_WIDTH = 0.75f;
    private static final float DEFAULT_MAX_ALTITUDE_METERS = 2.0f;
    private static final float ARENA_WIDTH_METERS_DEFAULT = 5.0f;
    private static final float ARENA_HEIGHT_METERS_DEFAULT = 10.0f;
    private static final float SLIDER_SCREEN_RATIO = 0.6f;

    /* ROS Nodes */
    private ROSNodeMain nodeMain;
    private ROSNodeService nodeService;

    /* Canvas element */
    private DrawingCanvas canvas;

    /* Model objects */
    private final List<Trajectory> trajectories = new ArrayList<>();;
    private final List<Obstacle> obstacles = new ArrayList<>();

    /* Globals for convenience */
    private SharedPreferences sharedPreferences;
    private int screenHeight;
    private int screenWidth;
    private Trajectory selectedTrajectory;

    /**
     * Constructor. Called once. Initializes model objects
     */
    public MainActivity() {
        super("MainActivity", "MainActivity");
    }

    /**
     * On create is called every time this activity is created or restarted. Rotating the screen causes an activity restart.
     * @param savedInstanceState Saved instance state from the last time this app was destroyed.
     */
    @Override
    protected void onCreate(
            final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set globals
        this.screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        this.screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        // Load config values into the shared preferences
        this.sharedPreferences = this.getPreferences(0);
        this.loadConfigProperties();

        // Set the content layout
        setContentView(R.layout.activity_main);

        // Initialize the various elements
        this.initModelElements();
        this.selectInitialTrajectory();
        this.initCanvas();
        this.initAltitudeSlider();
        this.initButtons();
        this.initTextViews();
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

    /**
     * TODO: What is the toolbar?
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    /**
     * Pressing this button starts the preview activity
     */
    private void initPreviewButton() {
        final Button previewButton = (Button) findViewById(R.id.previewButton);
        previewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // compress arrays, send to path planner, wait to launch PreviewActivity until service finishes
//                nodeService.getOptimizedTrajectories(new TrajectoryOptimizerCallback(quads.size(), (Void) -> {
//                            Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
//                            MainActivity.this.startActivity(intent);
//                            return Void;
//                        })
//                );
                Log.i("DEBUG", "Preview button pressed.");
            }
        });
    }

    private void initSendButtons() {
//        // Send FAB
//        //// TODO: 7/25/2017 Dynamically generate action buttons
//        final FloatingActionsMenu fabSent = (FloatingActionsMenu) findViewById(R.id.fab);
//        final FloatingActionButton sendAll = (FloatingActionButton) findViewById(R.id.sendAll);
//        final FloatingActionButton sendQuad1 = (FloatingActionButton) findViewById(R.id.sendQuad1);
//        fabSent.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//
//            }
//        });
//
//        sendAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Snackbar.make(v, "Sent All!", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
////                nodeMain.sendAll();
//            }
//        });
//
//        sendQuad1.setOnClickListener(new sendOnClickListener());
    }

    /* =================================== COMPLETED FUNCTIONS =================================== */

    /**
     * Initializes the buttons to clear the trajectories. Creates one button for every trajectory and one button to clear them all.
     */
    private void initClearTrajectoryButtons() {
        final FloatingActionsMenu clearTrajectoriesMenu = (FloatingActionsMenu) findViewById(R.id.fab_clear);

        // Add a clear all trajectory button
        final FloatingActionButton clearAllTrajectoryButton = new FloatingActionButton(this.getApplicationContext());
        clearAllTrajectoryButton.setColorNormal(Color.BLACK);
        clearTrajectoriesMenu.addButton(clearAllTrajectoryButton);
        clearAllTrajectoryButton.setOnClickListener(new ClearAllTrajectoriesButtonHandler(this.trajectories, this.canvas));

        // Add a clear trajectory button for every quad
        // TODO: Change the color of the buttons and add numbers to indicate the trajectory numbers
        for(int i = 0; i < this.sharedPreferences.getFloat("numQuads", 0.0f); i++) {
            final FloatingActionButton clearTrajectoryButton = new FloatingActionButton(this.getApplicationContext());
            clearTrajectoryButton.setColorNormal(Color.GRAY);
            clearTrajectoriesMenu.addButton(clearTrajectoryButton);
            clearTrajectoryButton.setOnClickListener(new ClearTrajectoryButtonHandler(this.canvas, this.trajectories.get(i)));
        }
    }

    /**
     * Rescales the canvas size for a new aspect ratio
     * @param aspectRatio The desired aspect ratio
     */
    private void setCanvasDimensions (
            final float aspectRatio) {

        // Set the canvas size
        final ViewGroup.LayoutParams canvasLayoutParams = findViewById(R.id.linLay).getLayoutParams();

        // Assume the new screen size
        float canvasHeightNew = screenHeight * MAX_CANVAS_HEIGHT_TO_SCREEN_HEIGHT;
        float canvasWidthNew = canvasHeightNew * aspectRatio;

        // Check to see if it fits. If too big, shrink to limiting dimensions
        if(canvasWidthNew > screenWidth * MAX_CANVAS_WIDTH_TO_SCREEN_WIDTH) {
            canvasWidthNew = screenWidth * MAX_CANVAS_WIDTH_TO_SCREEN_WIDTH;
            canvasHeightNew = canvasLayoutParams.width / aspectRatio;
        }

        // Set the new dimensions
        canvasLayoutParams.width = (int) canvasWidthNew;
        canvasLayoutParams.height = (int) canvasHeightNew;
    }

    /**
     * Initializes the various buttons on the main activity
     */
    private void initButtons() {
        initTerminateButton();
        initPreviewButton();
        initClearTrajectoryButtons();
        initSendButtons();
    }

    /**
     * Button to terminate app so it doesn't have to be done manually
     */
    private void initTerminateButton() {
        final Button terminateProgramButton = (Button)this.findViewById(R.id.kill_button);
        terminateProgramButton.setOnClickListener(view -> {
            finish();
            System.exit(0);
        });
    }

    /**
     * Initialize the altitude slider.
     */
    private void initAltitudeSlider() {
        final int sliderLength = (int) (screenHeight * SLIDER_SCREEN_RATIO);

        final FrameLayout sbLayout = (FrameLayout) findViewById(R.id.slider_frame_layout);
        sbLayout.getLayoutParams().height = sliderLength;

        final SeekBar slider = (SeekBar) findViewById(R.id.slider);
        slider.getLayoutParams().width = sliderLength;

        final float maxAltitude = this.sharedPreferences.getFloat("maxAltitude", DEFAULT_MAX_ALTITUDE_METERS);
        slider.setMax((int) (maxAltitude * 100) );

        final TextView altitudeDisplayTextView = (TextView) findViewById(R.id.seekbar_value);
        slider.setOnSeekBarChangeListener(new AltitudeSeekBarHandler(altitudeDisplayTextView, this.selectedTrajectory));
    }

    /**
     * Load the app config file into the shared preferences.
     */
    private void loadConfigProperties() {

        final Properties prop = new Properties();

        try {
            // Load the app_config file
            prop.load(this.getApplicationContext().getAssets().open(CONFIG_FILE_NAME));

            // Inject all of the values into the shared preferences
            // TODO: Assuming all of these are floats. Not safe!
            final SharedPreferences.Editor editor = this.sharedPreferences.edit();
            for(String configValue: prop.stringPropertyNames()) {
                editor.putFloat(configValue, Float.valueOf(prop.getProperty(configValue)));
            }
            editor.apply();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Initializes the model elements.
     */
    private void initModelElements() {
        this.initObstacles();
        this.initTrajectories();
    }

    /**
     * Initializes the canvas. Sets the dimensions, adds the various view elements, and attaches event listeners.
     */
    private void initCanvas() {
        this.canvas = (DrawingCanvas) findViewById(R.id.signature_canvas);

        // Set the canvas dimensions
        final float arenaWidthMeters = sharedPreferences.getFloat("arenaWidthMeters", ARENA_WIDTH_METERS_DEFAULT);
        final float arenaLengthMeters = sharedPreferences.getFloat("arenaLengthMeters", ARENA_HEIGHT_METERS_DEFAULT);
        final float arenaAspectRatio = arenaWidthMeters/arenaLengthMeters;
        setCanvasDimensions(arenaAspectRatio);

        // Create and add all the trajectory views
        for(Trajectory trajectory: trajectories) {
            // Create paint object
            final Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            // TODO: Change to color for multiple quads
            paint.setColor(Color.BLUE);

            // Add trajectory view
            this.canvas.addTrajectoryView(new TrajectoryView(trajectory, paint));
        }

        this.canvas.setOnTouchListener(new OnTouchEventDispatcher(
                this.canvas,
                new DrawingStartTouchHandler(this.selectedTrajectory),
                new DrawingMoveTouchHandler(this.selectedTrajectory),
                new DrawingEndTouchHandler()));
    }

    /**
     * Initializes the various trajectories
     */
    private void initTrajectories() {
        for(int i = 0; i < this.sharedPreferences.getFloat("numQuads", 0.0f); i++) {
            this.trajectories.add(new Trajectory());
        }
    }

    /**
     * Initializes the various obstacle entities
     */
    private void initObstacles() {
        this.obstacles.add(new Obstacle("Obstacle 1"));
    }

    /**
     * Initializes the various text views.
     */
    private void initTextViews() {
        this.initDimensionTextView();
    }

    /**
     * Initializes the text box that displays the arena dimensions.
     */
    private void initDimensionTextView() {
        final TextView dimensionText = (TextView) findViewById(R.id.dimension_text);
        final float arenaWidthMeters = sharedPreferences.getFloat("arenaWidthMeters", ARENA_WIDTH_METERS_DEFAULT);
        final float arenaHeightMeters = sharedPreferences.getFloat("arenaHeightMeters", ARENA_HEIGHT_METERS_DEFAULT);

        dimensionText.setText(Float.toString(arenaWidthMeters) + "m, " + Float.toString(arenaHeightMeters) + "m");
    }

    /**
     * Assumes that there is at least one initialized trajectory. Sets the first trajectory as the 'selected' trajectory to be controlled by the app.
     */
    private void selectInitialTrajectory() {
        this.selectedTrajectory = this.trajectories.get(0);
    }
}