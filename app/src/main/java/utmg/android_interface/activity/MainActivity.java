package utmg.android_interface.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import utmg.android_interface.controller.AltitudeSeekBarHandler;
import utmg.android_interface.controller.button.ClearAllTrajectoriesButtonHandler;
import utmg.android_interface.controller.button.ClearTrajectoryButtonHandler;
import utmg.android_interface.controller.button.SelectTrajectoryButtonHandler;
import utmg.android_interface.controller.canvas.DrawingEndTouchHandler;
import utmg.android_interface.controller.canvas.DrawingMoveTouchHandler;
import utmg.android_interface.controller.canvas.DrawingStartTouchHandler;
import utmg.android_interface.controller.canvas.OnTouchEventDispatcher;
import utmg.android_interface.controller.button.SendAllTrajectoriesButtonHandler;
import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.DrawingCanvas;
import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.R;
import utmg.android_interface.ROS.ROSNodeMain;
import utmg.android_interface.ROS.ROSNodeService;
import utmg.android_interface.view.entitiyView.TrajectoryView;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Main activity. This is where the program begins.
 */
public class MainActivity extends AppCompatActivity {

    /* Config file names */
    private static final String CONFIG_FILE_NAME = "app_config";

    /* Default constants */
    private static final float MAX_CANVAS_HEIGHT_TO_SCREEN_HEIGHT = 0.85f;
    private static final float MAX_CANVAS_WIDTH_TO_SCREEN_WIDTH = 0.78f;
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
    private List<Trajectory> trajectories = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();

    /* Globals for convenience */
    private SharedPreferences sharedPreferences;
    private int screenHeight;
    private int screenWidth;
    private Trajectory selectedTrajectory;
    final int[] trajectoryColors = {Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.MAGENTA};

    /**
     * Constructor.
     */
    public MainActivity() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Initialize model elements
        this.initModelElements();
        this.selectInitialTrajectory();

        // Initialize UI elements
        this.initUIElements();
    }

    /**
     * On create is called every time this activity is created or restarted. Rotating the screen causes an activity restart.
     *
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
        this.sharedPreferences = getSharedPreferences("Pref", 0);
        this.loadConfigProperties();

        // Set the content layout
        setContentView(R.layout.activity_main);

        // Initialize model elements
        this.initModelElements();
        this.selectInitialTrajectory();

        // Initialize UI elements
        this.initUIElements();

    }

    /**
     * Initializes the various UI elements of the main activity.
     */
    private void initUIElements() {
        this.initCanvas();
        this.initAltitudeSlider();
        this.initButtons();
        this.initTextViews();
        this.initToolbar();
        this.initTrajectorySelectionMenu();
    }

    /**
     * Initializes the canvas. Sets the dimensions, adds the various view elements, and attaches event listeners.
     */
    private void initCanvas() {

        // Get the canvas container
        final LinearLayout canvasHolder = (LinearLayout) findViewById(R.id.canvas_container);

        // Create a new canvas
        this.canvas = new DrawingCanvas(getApplicationContext());

        // Set the canvas layout parameters
        final ViewGroup.LayoutParams canvasLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.canvas.setLayoutParams(canvasLayoutParams);
        canvas.setBackgroundResource(R.drawable.canvas_border);

        // Add the canvas to its container
        canvasHolder.removeAllViews();
        canvasHolder.addView(this.canvas);

        // Set the canvas dimensions
        final float arenaWidthMeters = sharedPreferences.getFloat("arenaWidthMeters", ARENA_WIDTH_METERS_DEFAULT);
        final float arenaLengthMeters = sharedPreferences.getFloat("arenaLengthMeters", ARENA_HEIGHT_METERS_DEFAULT);
        final float arenaAspectRatio = arenaWidthMeters / arenaLengthMeters;
        this.setCanvasDimensions(arenaAspectRatio);

        // Create and add all the trajectory views
        for (int i = 0; i < trajectories.size(); i++) {
            final Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            paint.setColor(trajectoryColors[i % trajectoryColors.length]);

            // Add trajectory view
            this.canvas.addTrajectoryView(new TrajectoryView(trajectories.get(i), paint));
        }

        this.initCanvasHandlers();
    }

    /**
     * Rescales the canvas size for a new aspect ratio. This actually changes the size of the object containing the canvas since the canvas scales to fit the container.
     *
     * @param aspectRatio The desired aspect ratio
     */
    private void setCanvasDimensions(
            final float aspectRatio) {

        // Set the canvas container size
        final ViewGroup.LayoutParams canvasLayoutParams = findViewById(R.id.canvas_container).getLayoutParams();

        // TODO: Dont base this off of screen height. Base it off of the maximum canvas holder size
        // Assume the new screen size
//        float canvasHeightNew = screenHeight * MAX_CANVAS_HEIGHT_TO_SCREEN_HEIGHT;
        float canvasHeightNew = screenHeight - 200;
        float canvasWidthNew = canvasHeightNew * aspectRatio;

        // Check to see if it fits. If too big, shrink to limiting dimensions
//        if (canvasWidthNew > screenWidth * MAX_CANVAS_WIDTH_TO_SCREEN_WIDTH) {
        if (canvasWidthNew > screenWidth - 375) {
//            canvasWidthNew = screenWidth * MAX_CANVAS_WIDTH_TO_SCREEN_WIDTH;
            canvasWidthNew = screenWidth - 375;
            canvasHeightNew = canvasLayoutParams.width / aspectRatio;
        }

        // Set the new dimensions
        canvasLayoutParams.width = (int) canvasWidthNew;
        canvasLayoutParams.height = (int) canvasHeightNew;
    }

    /**
     * Initializes the trajectory selection radio button group at the top of the app.
     */
    private void initTrajectorySelectionMenu() {
        RadioGroup selectTrajectoryGroup = (RadioGroup) findViewById(R.id.select_trajectory_group);

        // Remove any previous buttons
        selectTrajectoryGroup.removeAllViews();

        // Add a radio button for every trajectory
        // TODO: Get these damn buttons to center!
        for (int i = 0; i < this.sharedPreferences.getFloat("numQuads", 0.0f); i++) {
            final RadioButton trajectorySelectButton = new RadioButton(this.getApplicationContext());
            final String buttonName = "Quad " + (i + 1);
            trajectorySelectButton.setText(buttonName);

            // Set the listener. Listener must change the selected trajectory
            trajectorySelectButton.setOnClickListener(new SelectTrajectoryButtonHandler(
                    trajectories.get(i),
                    trajectory -> {
                        // Select the new trajectory
                        selectedTrajectory = trajectory;

                        // Reinitialize the canvas controllers to control the new trajectory
                        initCanvasHandlers();
                    }
            ));

            selectTrajectoryGroup.addView(trajectorySelectButton, i);

            // Mark the first quad/trajectory as selected
            if (i == 0) selectTrajectoryGroup.check(trajectorySelectButton.getId());
        }
    }

    /**
     * Sets the various touch handlers for the canvas to control the selected trajectory.
     */
    private void initCanvasHandlers() {
        this.canvas.setOnTouchListener(new OnTouchEventDispatcher(
                this.canvas,
                new DrawingStartTouchHandler(this.selectedTrajectory),
                new DrawingMoveTouchHandler(this.selectedTrajectory),
                new DrawingEndTouchHandler()));
    }

    /**
     * Helper function that draws a text string as a bitmap. Useful for overlaying text on buttons.
     *
     * @param text      The text the be drawn.
     * @param textSize  The size of the text. 40 is good for buttons.
     * @param textColor The color of the text.
     * @return A bitmap of the text.
     */
    private static Bitmap textAsBitmap(
            final String text,
            final float textSize,
            final int textColor) {
        final Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        final float baseline = -paint.ascent(); // ascent() is negative
        final int width = (int) (paint.measureText(text) + 0.0f); // round
        final int height = (int) (baseline + paint.descent() + 0.0f);
        final Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    /**
     * Initializes the buttons to clear the trajectories. Creates one button for every trajectory and one button to clear them all.
     */
    private void initClearTrajectoryButtons() {

        // Get the menu container
        final LinearLayout clearTrajectoriesMenuContainer = (LinearLayout) findViewById(R.id.clear_trajectories_menu_container);

        // Clear any previous buttons and add the new ones
        clearTrajectoriesMenuContainer.removeAllViews();


        // Add a clear all trajectories button
        final FloatingActionButton clearAllTrajectoryButton = new FloatingActionButton(this.getApplicationContext());
        clearAllTrajectoryButton.setColorNormal(Color.YELLOW);
        clearAllTrajectoryButton.setImageBitmap(textAsBitmap("All", 40, Color.BLACK));
        clearAllTrajectoryButton.setOnClickListener(new ClearAllTrajectoriesButtonHandler(this.trajectories, this.canvas));
        clearTrajectoriesMenuContainer.addView(clearAllTrajectoryButton);

        // Add a clear trajectory button for every quad
        for (int i = 0; i < this.sharedPreferences.getFloat("numQuads", 0.0f); i++) {
            final FloatingActionButton clearTrajectoryButton = new FloatingActionButton(this.getApplicationContext());
            clearTrajectoryButton.setColorNormal(trajectoryColors[i % trajectoryColors.length]);
            clearTrajectoryButton.setImageBitmap(textAsBitmap("" + (i + 1), 40, Color.BLACK));
            clearTrajectoryButton.setOnClickListener(new ClearTrajectoryButtonHandler(this.trajectories.get(i), this.canvas));
            clearTrajectoriesMenuContainer.addView(clearTrajectoryButton);
        }
    }

    /**
     * Initializes the various buttons on the main activity
     */
    private void initButtons() {
        initTerminateButton();
        initClearTrajectoryButtons();
        initSendTrajectoryButton();
    }

    private void initSendTrajectoryButton() {
        final Button sendTrajectoriesButton = (Button) findViewById(R.id.send_button);
        sendTrajectoriesButton.setOnClickListener(new SendAllTrajectoriesButtonHandler(trajectories));
    }

    /**
     * Button to terminate app so it doesn't have to be done manually
     */
    private void initTerminateButton() {
        final Button terminateProgramButton = (Button) this.findViewById(R.id.kill_button);
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
        slider.setMax((int) (maxAltitude * 100));

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
            for (String configValue : prop.stringPropertyNames()) {
                // Prevent the config file from overwriting current settings.
                // This can happen if this function is called twice, for example if the screen is rotated
                // Screen rotation recreates the activity, re-calling this function
                final float currentValue = this.sharedPreferences.getFloat(configValue, Float.NaN);
                if (currentValue == Float.NaN) {
                    editor.putFloat(configValue, Float.valueOf(prop.getProperty(configValue)));
                }
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
     * Initializes the various trajectories
     */
    private void initTrajectories() {
        trajectories = new ArrayList<>();
        for (int i = 0; i < this.sharedPreferences.getFloat("numQuads", 0.0f); i++) {
            this.trajectories.add(new Trajectory("Quad " + (i + 1)));
        }
    }

    /**
     * Initializes the various obstacle entities
     */
    private void initObstacles() {
        obstacles = new ArrayList<>();
        this.obstacles.add(new Obstacle("Obstacle 1"));
    }

    /**
     * Assumes that there is at least one initialized trajectory. Sets the first trajectory as the 'selected' trajectory to be controlled by the app.
     */
    private void selectInitialTrajectory() {
        this.selectedTrajectory = this.trajectories.get(0);
    }


    /**
     * Inflate the menu. This adds items to the action bar if it is present.
     *
     * @param menu The options menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(
            final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Initialize the toolbar at the top of the screen
     */
    private void initToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as you specify a parent activity in AndroidManifest.xml.
     *
     * @param item The MenuItem selected
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(
            final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
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
        final float arenaHeightMeters = sharedPreferences.getFloat("arenaLengthMeters", ARENA_HEIGHT_METERS_DEFAULT);

        dimensionText.setText(Float.toString(arenaWidthMeters) + "m, " + Float.toString(arenaHeightMeters) + "m");
    }
}