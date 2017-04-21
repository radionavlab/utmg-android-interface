package utmg.android_interface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CanvasSizeActivity extends MainActivity {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    public float newWidth;
    public float newHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_size);

        pref = this.getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        newWidth = 0;
        newHeight = 0;

        final EditText w = (EditText) findViewById(R.id.resize_width);
        final EditText h = (EditText) findViewById(R.id.resize_height);

        w.setText(Float.toString(pref.getFloat("newWidth",3)));

        h.setText(Float.toString(pref.getFloat("newHeight",5)));



        Button done = (Button) findViewById(R.id.resize_button);
        done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                newWidth = Float.valueOf(w.getText().toString());
                newHeight = Float.valueOf(h.getText().toString());

                prefEditor.putFloat("newWidth", newWidth);
                prefEditor.putFloat("newHeight", newHeight);
                prefEditor.commit();
                finish();
            }
        });
    }

}
