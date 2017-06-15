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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

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

        previewCanvas = (PreviewCanvas) findViewById(R.id.preview_canvas);
        canvasSize = (LinearLayout) findViewById(R.id.linLay);
        previewCanvas.callOnDraw();

        seekbarRelative = (RelativeLayout) findViewById(R.id.seekbar_relative);
        final SeekBar quad1Seek = (SeekBar) findViewById(R.id.quad1Seek);
        final SeekBar quad2Seek = (SeekBar) findViewById(R.id.quad2Seek);
        final SeekBar quad3Seek = (SeekBar) findViewById(R.id.quad3Seek);
        final SeekBar quadAllSeek = (SeekBar) findViewById(R.id.quadAllSeek);

        seekSum = quad1Seek.getHeight() + quad2Seek.getHeight() + quad3Seek.getHeight() + quadAllSeek.getHeight()
        + quad2Seek.getPaddingTop() + quad3Seek.getPaddingTop() + quadAllSeek.getPaddingTop() + quadAllSeek.getPaddingBottom() ;

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
//
//        Log.i("seekBarHeight", Float.toString(seekbarRelative.getHeight()));
//        Log.i("screenHeight", Float.toString(screenHeight));

        final ViewTreeObserver observer= seekbarRelative.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                seekbarRelative.getHeight();

                canvasSize.getLayoutParams().height = ((screenHeight - seekbarRelative.getHeight()));
                canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5)/pref.getFloat("newHeight", 3)));

                Log.i("seekBarHeight", Float.toString(seekbarRelative.getHeight()));
                Log.i("screenHeight", Float.toString(screenHeight));
                Log.i("canvasHeight", Float.toString(canvasSize.getLayoutParams().height));
                Log.i("canvasWidth", Float.toString(canvasSize.getLayoutParams().width));
                //observer.removeGlobalOnLayoutListener(this);
            }
        });

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

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
