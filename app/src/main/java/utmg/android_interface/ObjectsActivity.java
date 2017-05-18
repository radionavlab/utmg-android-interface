package utmg.android_interface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class ObjectsActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objects);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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


    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


}
