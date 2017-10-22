package utmg.android_interface.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

import utmg.android_interface.R;
import utmg.android_interface.controller.button.ApplySettingsButtonHandler;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferenceEditor;

    private float newWidth;
    private float newHeight;
    private float newAltitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        setupActionBar();

        sharedPreferences = getSharedPreferences("Pref", 0);
        sharedPreferenceEditor = sharedPreferences.edit();

        initArenaSettingsTextboxes();
        initApplyArenaSettingsButton();


//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

//        final CheckBox isQuad1Checked = (CheckBox) findViewById(R.id.quad1_checkbox);
//        final CheckBox isQuad2Checked = (CheckBox) findViewById(R.id.quad2_checkbox);
//        final CheckBox isQuad3Checked = (CheckBox) findViewById(R.id.quad3_checkbox);
//        final CheckBox isSwordChecked = (CheckBox) findViewById(R.id.sword_checkbox);
//        final CheckBox isObstacle1Checked = (CheckBox) findViewById(R.id.obstacle1_checkbox);
//        final CheckBox isObstacle2Checked = (CheckBox) findViewById(R.id.obstacle2_checkbox);
//        final ToggleButton serviceToggle = (ToggleButton) findViewById(R.id.service_toggle);
//        final ToggleButton obstaclePublishToggle = (ToggleButton) findViewById(R.id.obstacle_publish_toggle);
//        final ToggleButton debugModeToggle = (ToggleButton) findViewById(R.id.debug_mode_toggle);
//
//        isQuad1Checked.setChecked(sharedPreferences.getBoolean("quad1", true));
//        isQuad2Checked.setChecked(sharedPreferences.getBoolean("quad2", true));
//        isQuad3Checked.setChecked(sharedPreferences.getBoolean("quad3", true));
//        isSwordChecked.setChecked(sharedPreferences.getBoolean("sword", false));
//        isObstacle1Checked.setChecked(sharedPreferences.getBoolean("obstacle1", false));
//        isObstacle2Checked.setChecked(sharedPreferences.getBoolean("obstacle2", false));
//        serviceToggle.setChecked((sharedPreferences.getBoolean("serviceToggle", false)));
//        obstaclePublishToggle.setChecked((sharedPreferences.getBoolean("obstaclePublish", false)));
//        debugModeToggle.setChecked((sharedPreferences.getBoolean("debugMode", false)));
//
//        isQuad1Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferenceEditor.putBoolean("quad1", isQuad1Checked.isChecked());
//                sharedPreferenceEditor.apply();
//                //Log.i("quadvis",Boolean.toString(sharedPreferences.getBoolean("quad",false)));
//            }
//        });
//
//        isQuad2Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferenceEditor.putBoolean("quad2", isQuad2Checked.isChecked());
//                sharedPreferenceEditor.apply();
//                //Log.i("quadvis",Boolean.toString(sharedPreferences.getBoolean("quad",false)));
//            }
//        });
//
//        isQuad3Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferenceEditor.putBoolean("quad3", isQuad3Checked.isChecked());
//                sharedPreferenceEditor.apply();
//                //Log.i("quadvis",Boolean.toString(sharedPreferences.getBoolean("quad",false)));
//            }
//        });
//
//        isSwordChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferenceEditor.putBoolean("sword", isSwordChecked.isChecked());
//                sharedPreferenceEditor.apply();
//            }
//        });
//
//        isObstacle1Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferenceEditor.putBoolean("obstacle1", isObstacle1Checked.isChecked());
//                sharedPreferenceEditor.apply();
//            }
//        });
//
//        isObstacle2Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferenceEditor.putBoolean("obstacle2", isObstacle2Checked.isChecked());
//                sharedPreferenceEditor.apply();
//            }
//        });
//
//        serviceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferenceEditor.putBoolean("serviceToggle", serviceToggle.isChecked());
//                sharedPreferenceEditor.apply();
//            }
//        });
//
//        obstaclePublishToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferenceEditor.putBoolean("obstaclePublish", obstaclePublishToggle.isChecked());
//                sharedPreferenceEditor.apply();
//            }
//        });
//
//        debugModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                sharedPreferenceEditor.putBoolean("debugMode", debugModeToggle.isChecked());
//                sharedPreferenceEditor.apply();
//            }
//        });
//
//
//        // mode spinner
//        Spinner spinner = (Spinner) findViewById(R.id.mode_spinner);
//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.modes_array, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);
//        spinner.setSelection( sharedPreferences.getInt("mode", 0) );
//
    }
//
//    private void setupActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
//    }
//
//    public void onItemSelected(AdapterView<?> parent, View view,
//                               int pos, long id) {
//        // An item was selected. You can retrieve the selected item using
//        // parent.getItemAtPosition(pos)
//        sharedPreferences = getSharedPreferences("Pref", 0);
//        sharedPreferenceEditor = sharedPreferences.edit();
//
//        if (parent.getItemAtPosition(pos).equals("Trajectory")) {
//            sharedPreferenceEditor.putInt("mode", 0);
//            sharedPreferenceEditor.apply();
//        }
//        else if (parent.getItemAtPosition(pos).equals("Waypoint")) {
//            sharedPreferenceEditor.putInt("mode", 1);
//            sharedPreferenceEditor.apply();
//        }
//
//    }
//
//    public void onNothingSelected(AdapterView<?> parent) {
//        // Another interface callback
//    }

    /**
     * Initialized the textboxes that change the arena settings.
     */
    private void initArenaSettingsTextboxes() {
        // Get user input to resize canvas
        final EditText numQuadsSetting = (EditText) findViewById(R.id.num_quads_setting_box);
        final EditText arenaWidthSetting = (EditText) findViewById(R.id.arena_width_setting_box);
        final EditText arenaLengthSetting = (EditText) findViewById(R.id.arena_length_setting_box);
        final EditText altitudeSetting = (EditText) findViewById(R.id.altitude_setting_box);

        // Retrieve the current settings
        final float numQuads = sharedPreferences.getFloat("numQuads", 0);
        final float areaWidth = sharedPreferences.getFloat("arenaWidthMeters", 0);
        final float arenaLength = sharedPreferences.getFloat("arenaLengthMeters", 0);
        final float maxAltitude = sharedPreferences.getFloat("maxAltitude", 0);

        // Put the current values in the textboxes
        numQuadsSetting.setText(String.format(Locale.US, "%d", (int)numQuads));
        arenaWidthSetting.setText(String.format(Locale.US, "%.2f", areaWidth));
        arenaLengthSetting.setText(String.format(Locale.US, "%.2f", arenaLength));
        altitudeSetting.setText(String.format(Locale.US, "%.2f", maxAltitude));
    }

    private void initApplyArenaSettingsButton() {
        final Button applySettingsButton = (Button) findViewById(R.id.apply_button);
        applySettingsButton.setOnClickListener(new ApplySettingsButtonHandler(
                this.sharedPreferenceEditor,
                (EditText) findViewById(R.id.num_quads_setting_box),
                (EditText) findViewById(R.id.arena_width_setting_box),
                (EditText) findViewById(R.id.arena_length_setting_box),
                (EditText) findViewById(R.id.altitude_setting_box)));
    }

    @Override
    public void onItemSelected(
            AdapterView<?> adapterView,
            View view,
            int i,
            long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
