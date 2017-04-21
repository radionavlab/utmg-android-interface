package utmg.android_interface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

public class CheckboxActivity extends MainActivity {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkbox);

        pref = getSharedPreferences("checkboxstate", 0);
        prefEditor = pref.edit();

        final CheckBox isQuadChecked = (CheckBox) findViewById(R.id.quad_checkbox);
        final CheckBox isSwordChecked = (CheckBox) findViewById(R.id.sword_checkbox);

//        Button done = (Button) findViewById(R.id.checkbox_button);
//        done.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                prefEditor.putBoolean("quad", isQuadChecked.isChecked());
//                prefEditor.putBoolean("sword", isSwordChecked.isChecked());
//                prefEditor.commit();
//                finish();
//            }
//        });

        isQuadChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefEditor.putBoolean("quad", isQuadChecked.isChecked());
                prefEditor.commit();
            }
        });

//        if(pref.getBoolean("quad", false) == true) {
//            isQuadChecked.setChecked(true);
//        }
//        if(pref.getBoolean("sword", false) == true) {
//            isSwordChecked.setChecked(true);
//        }
    }

}
