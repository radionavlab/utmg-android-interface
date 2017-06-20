package utmg.android_interface;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
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

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {

    private PreviewCanvas previewCanvas;
    private LinearLayout canvasSize;
    private RelativeLayout seekbarRelative;
    private int screenHeight;
    private int screenWidth;

    private ArrayList<Float> xPixelVec1;
    private ArrayList<Float> yPixelVec1;
    private ArrayList<Float> zPixelVec1;

    private ArrayList<Float> xPixelVec2;
    private ArrayList<Float> yPixelVec2;
    private ArrayList<Float> zPixelVec2;

    private ArrayList<Float> xPixelVec3;
    private ArrayList<Float> yPixelVec3;
    private ArrayList<Float> zPixelVec3;

    public int x = 0;
    public int y = 0;

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

        xPixelVec1 = DataShare.getXPixelVec(1);
        yPixelVec1 = DataShare.getYPixelVec(1);

        xPixelVec2 = DataShare.getXPixelVec(2);
        yPixelVec2 = DataShare.getYPixelVec(2);

        xPixelVec3 = DataShare.getXPixelVec(3);
        yPixelVec3 = DataShare.getYPixelVec(3);

        seekbarRelative = (RelativeLayout) findViewById(R.id.seekbar_relative);
        final SeekBar quad1Seek = (SeekBar) findViewById(R.id.quad1Seek);
        final SeekBar quad2Seek = (SeekBar) findViewById(R.id.quad2Seek);
        final SeekBar quad3Seek = (SeekBar) findViewById(R.id.quad3Seek);
        final SeekBar quadAllSeek = (SeekBar) findViewById(R.id.quadAllSeek);

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

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

        quad1.getLayoutParams().height = (int) (screenHeight * 0.05);
        quad1.getLayoutParams().width = (int) (screenWidth * 0.05);
        //quad1.setColorFilter(DataShare.getInstance("quad1").getQuadColour());

        quad2.getLayoutParams().height = (int) (screenHeight * 0.05);
        quad2.getLayoutParams().width = (int) (screenWidth * 0.05);
        //quad2.setColorFilter(DataShare.getInstance("quad2").getQuadColour());

        quad3.getLayoutParams().height = (int) (screenHeight * 0.05);
        quad3.getLayoutParams().width = (int) (screenWidth * 0.05);
        //quad3.setColorFilter(DataShare.getInstance("quad3").getQuadColour());

        final Handler handlerQuad = new Handler();
        Runnable runnableQuad = new Runnable() {
            @Override
            public void run() {
                // quad 1
                if (pref.getBoolean("quad1", false) == true) {
                    quad1.setVisibility(View.VISIBLE);
                    if(xPixelVec1 == null || yPixelVec1 == null) {
                        quad1.setVisibility(View.INVISIBLE);
                    }
                    else {
                        quad1.setVisibility(View.VISIBLE);
                       quad1.setX(xPixelVec1.get(x));
                        quad1.setY(yPixelVec1.get(y));



                    }

                    //Log.d("MA Quad1", "x: " + Float.toString(quad1.getX()) + "\ty:" + Float.toString(quad1.getY()));

                    //quad1.setImageAlpha( (int)(((DataShare.getInstance("quad1").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                }

                // quad 2
                if (pref.getBoolean("quad2", false) == true) {
                    quad2.setVisibility(View.VISIBLE);
                    if(xPixelVec2 == null || yPixelVec2 == null) {
                        quad2.setVisibility(View.INVISIBLE);
                    }
                    else {
                        quad2.setVisibility(View.VISIBLE);
                        quad2.setX(xPixelVec2.get(0));
                        quad2.setY(yPixelVec2.get(0));
                    }
//                    quad2.setImageAlpha( (int)(((DataShare.getInstance("quad2").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                }

                // quad 3
                if (pref.getBoolean("quad3", false) == true) {
                    quad3.setVisibility(View.VISIBLE);
                    if(xPixelVec3 == null || yPixelVec3 == null) {
                        quad3.setVisibility(View.INVISIBLE);
                    }
                    else {
                        quad3.setVisibility(View.VISIBLE);
                        quad3.setX(xPixelVec3.get(0));
                        quad3.setY(yPixelVec3.get(0));
                    }
//                    quad3.setImageAlpha( (int)(((DataShare.getInstance("quad3").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                }
                //Log.i("vis", Boolean.toString(pref.getBoolean("quad",false)));
                handlerQuad.postDelayed(this, 10);
            }
        };
        runnableQuad.run();
        Log.d("Quad X",Float.toString(quad1.getX()));
        Log.d("Quad Y",Float.toString(quad1.getY()));
        Log.d("Line Start X",Float.toString(xPixelVec1.get(0)));
        Log.d("Line Start Y",Float.toString(yPixelVec1.get(0)));

        final ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        final Handler toggleHandler = new Handler();
        Runnable toggleRunnable = new Runnable() {
            @Override
            public void run() {
                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if (isChecked) {
                            while(x < xPixelVec1.size()-1) {
                                x++;
                                y++;
                                quad1.setX(xPixelVec1.get(x));
                                quad1.setY(yPixelVec1.get(y));

                            }
                        } else {
                        }
                    }
                });
            }
        };
        toggleHandler.postDelayed(toggleRunnable,1000);
        toggleRunnable.run();


    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
