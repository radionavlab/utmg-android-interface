package utmg.android_interface.activity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import utmg.android_interface.R;


import utmg.android_interface.view.canvas.PreviewCanvas;

public class PreviewActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    private PreviewCanvas previewCanvas;
    private LinearLayout canvasSize;
    private RelativeLayout seekbarRelative;
    private int screenHeight;
    private int screenWidth;


    private SeekBar quadAllSeek;
    private ToggleButton toggle;
    private int togglei = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setupActionBar();

        // TODO: 7/31/2017 Switch between compressed and original points. Not an awful idea I guess 
        //com.getbase.floatingactionbutton.FloatingActionButton compressed_points = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.compressed_points);
        //com.getbase.floatingactionbutton.FloatingActionButton original_points = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.orignal_points);
        com.getbase.floatingactionbutton.FloatingActionButton terminate_preview = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.terminate_preview);
        com.getbase.floatingactionbutton.FloatingActionButton sendAirSim = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendToAirSim);
        com.getbase.floatingactionbutton.FloatingActionButton scalePath = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.scalePath);
        terminate_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.exit(0);
                finish();
            }
        });

        sendAirSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO in order to get a simulation in AirSim we need to send the x,y,z and time arrays of the quad to AirSim
                // right now only one quad works in AirSim
            }
        });

        scalePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scaling(1);
                //scaling(2);
                //scaling(3);
            }
        });

        // TODO: 7/31/2017 Compressed and original points are found here too
//        compressed_points.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                // plot points of compressed array on canvas
//
//            }
//        });
//        original_points.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // plot path of original user input
//            }
//        });
        //Log.i("PreviewActivity", "PreviewActivity started.");



        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        // intializing canvas
        canvasSize = (LinearLayout) findViewById(R.id.linLay);

        // getting screen size
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        // initializing quadAllSeek
        seekbarRelative = (RelativeLayout) findViewById(R.id.seekbar_relative);
        quadAllSeek = (SeekBar) findViewById(R.id.quadAllSeek);
        quadAllSeek.setMax(100);

        // seekbarRelative is the container view for the bottom seekbar for playback.
        // The listener is implemented such that PreviewActivity first waits for the seekbarRelative
        // to render first, then adjust the size of the canvas to fit the screen properly.
        // The listener is then attempted to be removed, as it is not needed anymore.
        seekbarRelative.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {

                // read height with myLinearLayout.getHeight() etc.
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) canvasSize.getLayoutParams();
                params.height = (int) (screenHeight - seekbarRelative.getHeight() - (getSupportActionBar().getHeight() * 1.5));
                params.width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5) / pref.getFloat("newHeight", 3)));
                canvasSize.setLayoutParams(params);

                // remember to remove the listener if possible
                ViewTreeObserver viewTreeObserver = seekbarRelative.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });

        previewCanvas = (PreviewCanvas) findViewById(R.id.preview_canvas);
        previewCanvas.callOnDraw();
        
        final Handler handlerQuad = new Handler();
        Runnable runnableQuad = new Runnable() {
            @Override
            public void run() {
                // quad 1
                previewCanvas.update();
                handlerQuad.postDelayed(this, 20);
            }
        };
        runnableQuad.run();


        

        // play/pause toggle button
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        
    }

    // run quad1 on play along the path
//    



    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}