package utmg.android_interface;

import android.content.Intent;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CanvasView customCanvas;
    private ArrayList<Float> xCoordVec;
    private ArrayList<Float> yCoordVec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        xCoordVec = new ArrayList<>();
        yCoordVec = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Sent!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                xCoordVec = customCanvas.getxCoordVec();
                yCoordVec = customCanvas.getyCoordVec();
            }
        });

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);

        TextView tvX = (TextView) findViewById(R.id.xView);
        TextView tvY = (TextView) findViewById(R.id.yView);

        //tvX.setText(Float.toString(customCanvas.getxCoord()));
        tvY.setText(Float.toString(customCanvas.getyCoord()));

    }

    public void clearCanvas(View v) {
        customCanvas.clearCanvas();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_roscam) {
            startActivity(new Intent(MainActivity.this, ROSCam.class));
        }

        else if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
