package utmg.android_interface.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

import utmg.android_interface.R;
import utmg.android_interface.controller.button.ApplySettingsButtonHandler;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferenceEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("Pref", 0);
        sharedPreferenceEditor = sharedPreferences.edit();

        initArenaSettingsTextboxes();
        initApplyArenaSettingsButton();
    }

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

    /**
     * Initializes the button that applies the arena settings
     */
    private void initApplyArenaSettingsButton() {
        final Button applySettingsButton = (Button) findViewById(R.id.apply_button);
        applySettingsButton.setOnClickListener(new ApplySettingsButtonHandler(
                this.sharedPreferenceEditor,
                (EditText) findViewById(R.id.num_quads_setting_box),
                (EditText) findViewById(R.id.arena_width_setting_box),
                (EditText) findViewById(R.id.arena_length_setting_box),
                (EditText) findViewById(R.id.altitude_setting_box),
                        this.getApplicationContext()));
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
