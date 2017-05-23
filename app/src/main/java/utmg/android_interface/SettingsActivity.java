package utmg.android_interface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupActionBar();

        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();


        final CheckBox isQuadChecked = (CheckBox) findViewById(R.id.quad_checkbox);
        final CheckBox isSwordChecked = (CheckBox) findViewById(R.id.sword_checkbox);
        final CheckBox isObstacle1Checked = (CheckBox) findViewById(R.id.obstacle1_checkbox);
        final CheckBox isObstacle2Checked = (CheckBox) findViewById(R.id.obstacle2_checkbox);
        final ToggleButton obstaclePublishToggle = (ToggleButton) findViewById(R.id.obstacle_publish_toggle);


        isQuadChecked.setChecked(pref.getBoolean("quad", false));
        isSwordChecked.setChecked(pref.getBoolean("sword", false));
        isObstacle1Checked.setChecked(pref.getBoolean("obstacle1", false));
        isObstacle2Checked.setChecked(pref.getBoolean("obstacle2", false));
        obstaclePublishToggle.setChecked((pref.getBoolean("obstaclePublish", false)));

        isQuadChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("quad", isQuadChecked.isChecked());
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

        obstaclePublishToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("obstaclePublish", obstaclePublishToggle.isChecked());
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
