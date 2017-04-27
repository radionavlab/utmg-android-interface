package utmg.android_interface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class CanvasSizeActivity extends MainActivity {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    public float newWidth;
    public float newHeight;
    public float newAltitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_size);

        pref = this.getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        newWidth = 0;
        newHeight = 0;
        newAltitude = 0;

        final EditText w = (EditText) findViewById(R.id.resize_width);
        final EditText h = (EditText) findViewById(R.id.resize_height);
        final EditText a = (EditText) findViewById(R.id.resize_altitude);

        w.setText(Float.toString(pref.getFloat("newWidth",3)));
        h.setText(Float.toString(pref.getFloat("newHeight",5)));
        a.setText(Float.toString(pref.getFloat("newAltitude",2)));


        Button done = (Button) findViewById(R.id.resize_button);
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
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

    }

}
