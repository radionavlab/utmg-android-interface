package utmg.android_interface;

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

public class MainActivity extends AppCompatActivity
{

    private GestureLibrary gLib;
    GestureOverlayView gestures;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        gLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gLib.load()) { finish(); }

        //Gesture Overlay
        gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGestureListener(handleGestureListener);
        gestures.setGestureColor(Color.RED);
//        gestures.setGestureStrokeWidth(12);

    }

    /**
     * our gesture listener
     */
    private GestureOverlayView.OnGestureListener handleGestureListener = new GestureOverlayView.OnGestureListener()
    {

        @Override
        public void onGesture(GestureOverlayView gestureView, MotionEvent me)
        {

        }

        @Override
        public void onGestureStarted(GestureOverlayView gestureView, MotionEvent me)
        {

        }

        @Override
        public void onGestureEnded(GestureOverlayView gestureView, MotionEvent me)
        {

        }

        @Override
        public void onGestureCancelled(GestureOverlayView gestureView, MotionEvent me)
        {

        }

    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
