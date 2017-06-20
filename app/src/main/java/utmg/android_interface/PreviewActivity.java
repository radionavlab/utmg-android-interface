package utmg.android_interface;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class PreviewActivity extends AppCompatActivity {

    private PreviewCanvas previewCanvas;
    private LinearLayout canvasSize;
    private RelativeLayout seekbarRelative;
    private int screenHeight;
    private int screenWidth;

    private float seekSum;

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setupActionBar();

        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        canvasSize = (LinearLayout) findViewById(R.id.linLay);
        previewCanvas = (PreviewCanvas) findViewById(R.id.preview_canvas);

        seekbarRelative = (RelativeLayout) findViewById(R.id.seekbar_relative);
        final SeekBar quad1Seek = (SeekBar) findViewById(R.id.quad1Seek);
        final SeekBar quad2Seek = (SeekBar) findViewById(R.id.quad2Seek);
        final SeekBar quad3Seek = (SeekBar) findViewById(R.id.quad3Seek);
        final SeekBar quadAllSeek = (SeekBar) findViewById(R.id.quadAllSeek);

        //seekSum = quad1Seek.getHeight() + quad2Seek.getHeight() + quad3Seek.getHeight() + quadAllSeek.getHeight()
        //+ quad2Seek.getPaddingTop() + quad3Seek.getPaddingTop() + quadAllSeek.getPaddingTop() + quadAllSeek.getPaddingBottom() ;

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
//
//        Log.i("seekBarHeight", Float.toString(seekbarRelative.getHeight()));
//        Log.i("screenHeight", Float.toString(screenHeight));

        seekbarRelative.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                // read height with myLinearLayout.getHeight() etc.

                Log.i("seekBarHeight", Float.toString(seekbarRelative.getHeight()));

                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) canvasSize.getLayoutParams();
                params.height = (int)(screenHeight - seekbarRelative.getHeight() - (getSupportActionBar().getHeight()*1.5));
                params.width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5)/pref.getFloat("newHeight", 3)));
                canvasSize.setLayoutParams(params);

                // remember to remove the listener if possible
                ViewTreeObserver viewTreeObserver = seekbarRelative.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });

        previewCanvas.callOnDraw();

        final Handler seekbarH = new Handler();
        Runnable seekbarR = new Runnable() {
            @Override
            public void run() {

                quadAllSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        quad1Seek.setProgress(progress);
                        quad2Seek.setProgress(progress);
                        quad3Seek.setProgress(progress);
                    }
                });
                seekbarH.postDelayed(this, 1000);
            }
        };
        seekbarR.run();

        final ImageView quad1 = (ImageView) findViewById(R.id.demo_quad1);
        final ImageView quad2 = (ImageView) findViewById(R.id.demo_quad2);
        final ImageView quad3 = (ImageView) findViewById(R.id.demo_quad3);

        quad1.getLayoutParams().height = (int) (screenHeight * 0.025);
        quad1.getLayoutParams().width = (int) (screenWidth * 0.025);
        //quad1.setColorFilter(DataShare.getInstance("quad1").getQuadColour());

        quad2.getLayoutParams().height = (int) (screenHeight * 0.025);
        quad2.getLayoutParams().width = (int) (screenWidth * 0.025);
        //quad2.setColorFilter(DataShare.getInstance("quad2").getQuadColour());

        quad3.getLayoutParams().height = (int) (screenHeight * 0.025);
        quad3.getLayoutParams().width = (int) (screenWidth * 0.025);
        //quad3.setColorFilter(DataShare.getInstance("quad3").getQuadColour());

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // When it is playing
                } else {
                    // When it is on pause
                }
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
