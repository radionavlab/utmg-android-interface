package utmg.android_interface;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by Nidhi on 4/7/17.
 */

public class SwitchActivity extends Activity {
    Switch switchQ;
    ImageView object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.switch_item);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        object = (ImageView) findViewById(R.id.quad);

        switchQ = (Switch) menu.findItem(R.id.q_switch).getActionView().findViewById(R.id.switchLay);
        switchQ.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    object.setVisibility(View.VISIBLE);
                }
                else if (!isChecked) {
                    object.setVisibility(View.INVISIBLE);
                }
            }
        });
        return true;
    }

}
