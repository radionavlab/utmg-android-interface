package utmg.android_interface.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import utmg.android_interface.controller.button.ClearAllTrajectoriesButtonHandler;
import utmg.android_interface.controller.button.ClearTrajectoryButtonHandler;
import utmg.android_interface.controller.button.PreviewButtonHandler;
import utmg.android_interface.controller.button.SelectTrajectoryButtonHandler;
import utmg.android_interface.controller.canvas.DrawingEndTouchHandler;
import utmg.android_interface.controller.canvas.DrawingMoveTouchHandler;
import utmg.android_interface.controller.canvas.DrawingStartTouchHandler;
import utmg.android_interface.controller.canvas.OnTouchEventDispatcher;
import utmg.android_interface.controller.button.SendAllTrajectoriesButtonHandler;
import utmg.android_interface.controller.button.SendTrajectoryButtonHandler;
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

        // TODO: Ensure that this does not override current shared preferences
        // Load config values into the shared preferences
        this.sharedPreferences = this.getPreferences(0);
        this.loadConfigProperties();

        // Set the content layout
        setContentView(R.layout.activity_main);

        // Initialize model elements
        this.initModelElements();
        this.selectInitialTrajectory();

        // Initialize UI elements
        this.initUIElements();

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

    private void initTrajectorySelectionMenu() {
        RadioGroup selectTrajectoryGroup = (RadioGroup) findViewById(R.id.select_trajectory_group);

        // Add a radio button for every trajectory
        // TODO: Change the color of radio buttons to match the trajectory color
        // TODO: Get these damn buttons to center!
        for(int i = 0; i < this.sharedPreferences.getFloat("numQuads", 0.0f); i++) {
            final RadioButton trajectorySelectButton = new RadioButton(this.getApplicationContext());
            final String buttonName = "Quad " + (i+1);
            trajectorySelectButton.setText(buttonName);

            // Mark the first quad/trajectory as selected
            if(i == 0) trajectorySelectButton.setChecked(true);

            // Set the listener. Listener must change the selected trajectory
            trajectorySelectButton.setOnClickListener(new SelectTrajectoryButtonHandler(
                    trajectories.get(i),
                    // TODO: Must reset the canvas handlers for the new selected trajectory
                    trajectory -> {selectedTrajectory = trajectory; Log.i("New Trajectory", trajectory.toString());}
            ));

            selectTrajectoryGroup.addView(trajectorySelectButton, i);
        }
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

    /* =================================== COMPLETED FUNCTIONS =================================== */

    /**
     * Initializes the preview button
     */
    private void initPreviewButton() {
        final Button previewButton = (Button) findViewById(R.id.previewButton);
        previewButton.setOnClickListener(new PreviewButtonHandler());
    }

    /**
     * Initializes the buttons to send the trajectories. Creates one button for every trajectory and one button to send them all.
     */
    private void initSendTrajectoryButtons() {
        final FloatingActionsMenu sendTrajectoriesMenu = (FloatingActionsMenu) findViewById(R.id.fab);

        // Add a send all trajectories button
        final FloatingActionButton sendAllTrajectoriesButton = new FloatingActionButton(this.getApplicationContext());
        sendAllTrajectoriesButton.setColorNormal(Color.GREEN);
        sendTrajectoriesMenu.addButton(sendAllTrajectoriesButton);
        sendAllTrajectoriesButton.setOnClickListener(new SendAllTrajectoriesButtonHandler(this.trajectories, nodeMain));

        // Add a send trajectory button for every quad
        // TODO: Change the color of the buttons and add numbers to indicate the trajectory numbers
        for(int i = 0; i < this.sharedPreferences.getFloat("numQuads", 0.0f); i++) {
            final FloatingActionButton sendTrajectoryButton = new FloatingActionButton(this.getApplicationContext());
            sendTrajectoryButton.setColorNormal(Color.GRAY);
            sendTrajectoriesMenu.addButton(sendTrajectoryButton);
            sendTrajectoryButton.setOnClickListener(new SendTrajectoryButtonHandler(this.trajectories.get(i)));
        }
    }

    /**
     * Initializes the buttons to clear the trajectories. Creates one button for every trajectory and one button to clear them all.
     */
    private void initClearTrajectoryButtons() {
        final FloatingActionsMenu clearTrajectoriesMenu = (FloatingActionsMenu) findViewById(R.id.fab_clear);

        // Add a clear all trajectories button
        final FloatingActionButton clearAllTrajectoryButton = new FloatingActionButton(this.getApplicationContext());
        clearAllTrajectoryButton.setColorNormal(Color.RED);
        clearTrajectoriesMenu.addButton(clearAllTrajectoryButton);
        clearAllTrajectoryButton.setOnClickListener(new ClearAllTrajectoriesButtonHandler(this.trajectories, this.canvas));

        // Add a clear trajectory button for every quad
        // TODO: Change the color of the buttons and add numbers to indicate the trajectory numbers
        for(int i = 0; i < this.sharedPreferences.getFloat("numQuads", 0.0f); i++) {
            final FloatingActionButton clearTrajectoryButton = new FloatingActionButton(this.getApplicationContext());
            clearTrajectoryButton.setColorNormal(Color.GRAY);
            clearTrajectoriesMenu.addButton(clearTrajectoryButton);
            clearTrajectoryButton.setOnClickListener(new ClearTrajectoryButtonHandler(this.trajectories.get(i), this.canvas));
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
        initSendTrajectoryButtons();
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
            this.trajectories.add(new Trajectory("Quad " + (i+1)));
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


    /**
     * Initializes the various UI elements of the main activity.
     */
    private void initUIElements() {
        this.initCanvas();
        this.initAltitudeSlider();
        this.initButtons();
        this.initTextViews();
        this.initTrajectorySelectionMenu();
    }
}