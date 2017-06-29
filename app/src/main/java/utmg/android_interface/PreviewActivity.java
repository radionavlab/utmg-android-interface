package utmg.android_interface;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.TranslateAnimation;
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

    private ToggleButton toggle;

    private ImageView quad1;
    private ImageView quad2;
    private ImageView quad3;

    private ArrayList<Float> xPixelVec1;
    private ArrayList<Float> yPixelVec1;
    private ArrayList<Float> zPixelVec1;

    private ArrayList<Float> xPixelVec2;
    private ArrayList<Float> yPixelVec2;
    private ArrayList<Float> zPixelVec2;

    private ArrayList<Float> xPixelVec3;
    private ArrayList<Float> yPixelVec3;
    private ArrayList<Float> zPixelVec3;

    private SeekBar quadAllSeek;

    private int togglei = 0;
    private int quadMax = 0;

    private float x2;
    private float y2;

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

        DataShare.setSeekLoc(1, 0);
        DataShare.setSeekLoc(2, 0);
        DataShare.setSeekLoc(3, 0);

        DataShare.setPlayBackState(true);

        seekbarRelative = (RelativeLayout) findViewById(R.id.seekbar_relative);
        quadAllSeek = (SeekBar) findViewById(R.id.quadAllSeek);

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

        quad1 = (ImageView) findViewById(R.id.demo_quad1);
        quad2 = (ImageView) findViewById(R.id.demo_quad2);
        quad3 = (ImageView) findViewById(R.id.demo_quad3);

        quad1.getLayoutParams().height = (int) (screenHeight * 0.02);
        quad1.getLayoutParams().width = (int) (screenWidth * 0.02);
        //quad1.setColorFilter(DataShare.getInstance("quad1").getQuadColour());

        quad2.getLayoutParams().height = (int) (screenHeight * 0.02);
        quad2.getLayoutParams().width = (int) (screenWidth * 0.02);
        //quad2.setColorFilter(DataShare.getInstance("quad2").getQuadColour());

        quad3.getLayoutParams().height = (int) (screenHeight * 0.02);
        quad3.getLayoutParams().width = (int) (screenWidth * 0.02);
        //quad3.setColorFilter(DataShare.getInstance("quad3").getQuadColour());

        final Handler handlerQuad = new Handler();
        Runnable runnableQuad = new Runnable() {
            @Override
            public void run() {
                // quad 1
                if (pref.getBoolean("quad1", false)) {
                    if(xPixelVec1 == null || yPixelVec1 == null) {
                        quad1.setVisibility(View.INVISIBLE);
                    }
                    else {
                        // place imageView at start of path on beginning of PreviewActivity
                        if(togglei==0){
                            quad1.setX(xPixelVec1.get(0) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad1.setY(yPixelVec1.get(0) - quad1.getHeight() / 2);
                            quad1.setVisibility(View.VISIBLE);
                        }

                    }
                    //quad1.setImageAlpha( (int)(((DataShare.getInstance("quad1").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                }

                // quad 2
                if (pref.getBoolean("quad2", false)) {
                    if(xPixelVec2 == null || yPixelVec2 == null) {
                        quad2.setVisibility(View.INVISIBLE);
                    }
                    else {
                        // place imageView at start of path on beginning of PreviewActivity
                        if(togglei==0){
                            quad2.setX(xPixelVec2.get(0) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad2.setY(yPixelVec2.get(0) - quad1.getHeight() / 2);
                            quad2.setVisibility(View.VISIBLE);
                        }
                    }
//                    quad2.setImageAlpha( (int)(((DataShare.getInstance("quad2").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                }

                // quad 3
                if (pref.getBoolean("quad3", false)) {
                    if(xPixelVec3 == null || yPixelVec3 == null) {
                        quad3.setVisibility(View.INVISIBLE);
                    }
                    else {
                        // place imageView at start of path on beginning of PreviewActivity
                        if(togglei==0){
                            quad3.setX(xPixelVec3.get(0) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad3.setY(yPixelVec3.get(0) - quad1.getHeight() / 2);
                            quad3.setVisibility(View.VISIBLE);
                        }
                    }
//                    quad3.setImageAlpha( (int)(((DataShare.getInstance("quad3").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                }
                //Log.i("vis", Boolean.toString(pref.getBoolean("quad",false)));
               handlerQuad.postDelayed(this, 10);
            }
        };
        runnableQuad.run();

        quadAllSeek.setMax(Math.max(Math.max(DataShare.getCurrentTime(1).size(), DataShare.getCurrentTime(2).size()), DataShare.getCurrentTime(3).size()));


        // TODO touch listeners only work for single paths, FIX THIS!
        // ONLY WORKS AFTER RUNNING THROUGH ONCE
        quad1.setOnTouchListener(new View.OnTouchListener() {
            Point DownPT = new Point(); // Record mouse position when pressed down
            Point StartPT = new Point(); // Record start position of quad1
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int eid = event.getAction();
                switch (eid)
                {
                    case MotionEvent.ACTION_MOVE :
                        PointF mv = new PointF( event.getX() - DownPT.x, event.getY() - DownPT.y);
                        quad1.setX((int)(StartPT.x+mv.x));
                        quad1.setY((int)(StartPT.y+mv.y));
                        StartPT = new Point((int) quad1.getX(), (int) quad1.getY());
                        break;
                    case MotionEvent.ACTION_DOWN :
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new Point((int) quad1.getX(), (int) quad1.getY());
                        break;
                    case MotionEvent.ACTION_UP :
                        // Nothing have to do
                        break;
                    default :
                        break;
                }
                return true;
            }
        });

        quad2.setOnTouchListener(new View.OnTouchListener() {
            Point DownPT = new Point(); // Record mouse position when pressed down
            Point StartPT = new Point(); // Record start position of quad1
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int eid = event.getAction();
                switch (eid)
                {
                    case MotionEvent.ACTION_MOVE :
                        PointF mv = new PointF( event.getX() - DownPT.x, event.getY() - DownPT.y);
                        quad2.setX((int)(StartPT.x+mv.x));
                        quad2.setY((int)(StartPT.y+mv.y));
                        StartPT = new Point((int) quad2.getX(), (int) quad2.getY());
                        break;
                    case MotionEvent.ACTION_DOWN :
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new Point((int) quad2.getX(), (int) quad2.getY());
                        break;
                    case MotionEvent.ACTION_UP :
                        // Nothing have to do
                        break;
                    default :
                        break;
                }
                return true;
            }
        });

        quad3.setOnTouchListener(new View.OnTouchListener() {
            Point DownPT = new Point(); // Record mouse position when pressed down
            Point StartPT = new Point(); // Record start position of quad1
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                int eid = event.getAction();
                switch (eid)
                {
                    case MotionEvent.ACTION_MOVE :
                        PointF mv = new PointF( event.getX() - DownPT.x, event.getY() - DownPT.y);
                        quad3.setX((int)(StartPT.x+mv.x));
                        quad3.setY((int)(StartPT.y+mv.y));
                        StartPT = new Point((int) quad3.getX(), (int) quad3.getY());
                        break;
                    case MotionEvent.ACTION_DOWN :
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new Point((int) quad3.getX(), (int) quad3.getY());
                        break;
                    case MotionEvent.ACTION_UP :
                        // Nothing have to do
                        break;
                    default :
                        break;
                }
                return true;
            }
        });
        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        Runnable quadToggle = new Runnable() {
            @Override
            public void run() {
                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked && DataShare.getPlayBackState()) {
                            togglei = 1; // will allow it to change rather than stay at starting position
                            if (xPixelVec1 != null) {
                                runQuad1();
                                seekAll();
                            }

                            if (xPixelVec2 != null) {
                                runQuad2();
                                seekAll();
                            }

                            if (xPixelVec3 != null) {
                                runQuad3();
                                seekAll();
                            }
                        }else if(!isChecked && DataShare.getPlayBackState()) {
                            quadAllSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {
                                }

                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    int index = quadAllSeek.getProgress();
                                    if (toggle.isChecked() == false) {
                                        if (xPixelVec1 != null) {

                                            if(index == quadAllSeek.getMax()){
                                                // This will handle the error

                                            }else {
                                                quad1.setX(xPixelVec1.get(index) - quad1.getWidth() / 2 + canvasSize.getLeft());
                                                quad1.setY(yPixelVec1.get(index) - quad1.getHeight() / 2);
                                            }
                                        }
                                        if (xPixelVec2 != null) {

                                            if(index == quadAllSeek.getMax()){
                                                // This will handle the error

                                            }else {
                                                quad2.setX(xPixelVec2.get(index) - quad2.getWidth() / 2 + canvasSize.getLeft());
                                                quad2.setY(yPixelVec2.get(index) - quad2.getHeight() / 2);
                                            }
                                        }
                                        if (xPixelVec3 != null) {

                                            if(index == quadAllSeek.getMax()){
                                                // This will handle the error

                                            }else {
                                                quad3.setX(xPixelVec3.get(index) - quad3.getWidth() / 2 + canvasSize.getLeft());
                                                quad3.setY(yPixelVec3.get(index) - quad3.getHeight() / 2);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        };
        quadToggle.run();
    }

    private void runQuad1() {
        final Handler updateH1 = new Handler();
        Runnable updateR1 = new Runnable() {
            @Override
            public void run() {

                while (DataShare.getSeekLoc(1) < xPixelVec1.size() - 1)  {
                    // NOTE: CHANGE THIS WAIT TIME IN MS AS DESIRED BY PLAYBACK SPEED
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateH1.post(new Runnable() {
                        @Override
                        public void run() {

                            int value = DataShare.getSeekLoc(1);

                            quad1.setX(xPixelVec1.get(value) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad1.setY(yPixelVec1.get(value) - quad1.getHeight() / 2);


                            if (toggle.isChecked()) {
                                DataShare.setSeekLoc(1, value + 1);
                            }
                            else {
                                DataShare.setPlayBackState(false);
                            }

                            if (value >= xPixelVec1.size()-1) {
                                DataShare.setSeekLoc(1, 0);
                                DataShare.setPlayBackState(true);
                            }
                        }
                    });
                }
            }
        };
        new Thread(updateR1).start();
    }

    private void runQuad2() {
        final Handler updateH2 = new Handler();
        Runnable updateR2 = new Runnable() {
            @Override
            public void run() {

                while (DataShare.getSeekLoc(2) < xPixelVec2.size() - 1)  {
                    // NOTE: CHANGE THIS WAIT TIME IN MS AS DESIRED BY PLAYBACK SPEED
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateH2.post(new Runnable() {
                        @Override
                        public void run() {

                            int value = DataShare.getSeekLoc(2);

                            quad2.setX(xPixelVec2.get(value) - quad2.getWidth() / 2 + canvasSize.getLeft());
                            quad2.setY(yPixelVec2.get(value) - quad2.getHeight() / 2);

                            if (toggle.isChecked()) {
                                DataShare.setSeekLoc(2, value + 1);
                            }
                            else {
                                DataShare.setPlayBackState(false);
                            }

                            if (value >= xPixelVec2.size()-1) {
                                DataShare.setSeekLoc(2, 0);
                                DataShare.setPlayBackState(true);
                            }
                        }
                    });
                }
            }
        };
        new Thread(updateR2).start();
    }

    private void runQuad3() {
        final Handler updateH3 = new Handler();
        Runnable updateR3 = new Runnable() {
            @Override
            public void run() {

                while (DataShare.getSeekLoc(3) < xPixelVec3.size() - 1)  {
                    // NOTE: CHANGE THIS WAIT TIME IN MS AS DESIRED BY PLAYBACK SPEED
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateH3.post(new Runnable() {
                        @Override
                        public void run() {

                            int value = DataShare.getSeekLoc(3);

                            quad3.setX(xPixelVec3.get(value) - quad3.getWidth() / 2 + canvasSize.getLeft());
                            quad3.setY(yPixelVec3.get(value) - quad3.getHeight() / 2);

                            if (toggle.isChecked()) {
                                DataShare.setSeekLoc(3, value + 1);
                            }
                            else {
                                DataShare.setPlayBackState(false);
                            }

                            if (value >= xPixelVec3.size()-1) {
                                DataShare.setSeekLoc(3, 0);
                                DataShare.setPlayBackState(true);
                            }
                        }
                    });
                }
            }
        };
        new Thread(updateR3).start();
    }

    private void seekAll() {
        quadMax = 0;
        quadAllSeek.setMax(Math.max(Math.max(DataShare.getCurrentTime(1).size(), DataShare.getCurrentTime(2).size()), DataShare.getCurrentTime(3).size()));

        Log.i("11111111111", Float.toString(DataShare.getCurrentTime(1).size()));
        Log.i("22222222222", Float.toString(DataShare.getCurrentTime(2).size()));
        Log.i("33333333333", Float.toString(DataShare.getCurrentTime(3).size()));
        Log.i("MMMMMMMMMMM", Float.toString(Math.max(Math.max(DataShare.getCurrentTime(1).size(), DataShare.getCurrentTime(2).size()), DataShare.getCurrentTime(3).size())));
        final Handler seekH = new Handler();
        Runnable seekR = new Runnable() {
            @Override
            public void run() {
                if (DataShare.getCurrentTime(1).size() == quadAllSeek.getMax()) {
                    quadMax = 1;
                }
                else if (DataShare.getCurrentTime(2).size() == quadAllSeek.getMax()) {
                    quadMax = 2;
                }
                else {
                    quadMax = 3;
                }
                while(quadMax < quadAllSeek.getMax() - 1) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    seekH.post(new Runnable() {
                        @Override
                        public void run() {
                            int value = DataShare.getSeekLoc(quadMax);
                            int max = 0;
                            if(value != 0){
                                max = value;
                                quadAllSeek.setProgress(max);
                            }else{

                            }
                            if(max == quadAllSeek.getMax()-1){
                                quadAllSeek.setProgress(max+1);
                                toggle.setChecked(false);
                                max = 0;
                            }
                            Log.i("VauleValueValue", Integer.toString(value));
                            Log.i("MaxMaxMax", Integer.toString(max));
                            Log.i("quadMaxquadMaxquadMax", Integer.toString(quadAllSeek.getMax()));
                        }
                    });
                }

            }
        };
         new Thread(seekR).start();
    }


    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}