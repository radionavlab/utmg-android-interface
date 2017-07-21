package utmg.android_interface.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import utmg.android_interface.DataShare;
import utmg.android_interface.R;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    public float newWidth;
    public float newHeight;
    public float newAltitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupActionBar();

        newWidth = 0;
        newHeight = 0;
        newAltitude = 0;

        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        // Get user input to resize canvas
        final EditText w = (EditText) findViewById(R.id.resize_width);
        final EditText h = (EditText) findViewById(R.id.resize_height);
        final EditText a = (EditText) findViewById(R.id.resize_altitude);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        w.setText(Float.toString(pref.getFloat("newWidth",5)));
        h.setText(Float.toString(pref.getFloat("newHeight",3)));
        a.setText(Float.toString(pref.getFloat("newAltitude",2)));

        Button done = (Button) findViewById(R.id.button);
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                newWidth = Float.valueOf(w.getText().toString());
                newHeight = Float.valueOf(h.getText().toString());
                newAltitude = Float.valueOf(a.getText().toString());

                prefEditor.putFloat("newWidth", newWidth);
                prefEditor.putFloat("newHeight", newHeight);
                prefEditor.putFloat("newAltitude", newAltitude);
                prefEditor.commit();

                finish();
            }
        });


        final CheckBox isQuad1Checked = (CheckBox) findViewById(R.id.quad1_checkbox);
        final CheckBox isQuad2Checked = (CheckBox) findViewById(R.id.quad2_checkbox);
        final CheckBox isQuad3Checked = (CheckBox) findViewById(R.id.quad3_checkbox);
        final CheckBox isSwordChecked = (CheckBox) findViewById(R.id.sword_checkbox);
        final CheckBox isObstacle1Checked = (CheckBox) findViewById(R.id.obstacle1_checkbox);
        final CheckBox isObstacle2Checked = (CheckBox) findViewById(R.id.obstacle2_checkbox);
        final ToggleButton serviceToggle = (ToggleButton) findViewById(R.id.service_toggle);
        final ToggleButton obstaclePublishToggle = (ToggleButton) findViewById(R.id.obstacle_publish_toggle);
        final ToggleButton debugModeToggle = (ToggleButton) findViewById(R.id.debug_mode_toggle);

        DataShare.setServiceState(false);

        isQuad1Checked.setChecked(pref.getBoolean("quad1", true));
        isQuad2Checked.setChecked(pref.getBoolean("quad2", true));
        isQuad3Checked.setChecked(pref.getBoolean("quad3", true));
        isSwordChecked.setChecked(pref.getBoolean("sword", false));
        isObstacle1Checked.setChecked(pref.getBoolean("obstacle1", false));
        isObstacle2Checked.setChecked(pref.getBoolean("obstacle2", false));
        serviceToggle.setChecked((pref.getBoolean("serviceToggle", false)));
        obstaclePublishToggle.setChecked((pref.getBoolean("obstaclePublish", false)));
        debugModeToggle.setChecked((pref.getBoolean("debugMode", false)));

        isQuad1Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("quad1", isQuad1Checked.isChecked());
                prefEditor.commit();
                //Log.i("quadvis",Boolean.toString(pref.getBoolean("quad",false)));
            }
        });

        isQuad2Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("quad2", isQuad2Checked.isChecked());
                prefEditor.commit();
                //Log.i("quadvis",Boolean.toString(pref.getBoolean("quad",false)));
            }
        });

        isQuad3Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("quad3", isQuad3Checked.isChecked());
                prefEditor.commit();
                //Log.i("quadvis",Boolean.toString(pref.getBoolean("quad",false)));
            }
        });

        isSwordChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("sword", isSwordChecked.isChecked());
                prefEditor.commit();
            }
        });

        isObstacle1Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("obstacle1", isObstacle1Checked.isChecked());
                prefEditor.commit();
            }
        });

        isObstacle2Checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("obstacle2", isObstacle2Checked.isChecked());
                prefEditor.commit();
            }
        });

        serviceToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("serviceToggle", serviceToggle.isChecked());
                if(isChecked == true){
                    DataShare.setServiceState(true);
                }else{
                    DataShare.setServiceState(false);
                }
                prefEditor.commit();
            }
        });

        obstaclePublishToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("obstaclePublish", obstaclePublishToggle.isChecked());
                prefEditor.commit();
            }
        });

        debugModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("debugMode", debugModeToggle.isChecked());
                prefEditor.commit();
            }
        });


        // mode spinner
        Spinner spinner = (Spinner) findViewById(R.id.mode_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.modes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection( pref.getInt("mode", 0) );

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        if (parent.getItemAtPosition(pos).equals("Trajectory")) {
            prefEditor.putInt("mode", 0);
            prefEditor.commit();
        }
        else if (parent.getItemAtPosition(pos).equals("Waypoint")) {
            prefEditor.putInt("mode", 1);
            prefEditor.commit();
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
