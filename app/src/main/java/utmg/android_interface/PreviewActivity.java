package utmg.android_interface;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
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

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
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
    private ArrayList<Float> xPixelVec1Scaled = new ArrayList<>();
    private ArrayList<Float> yPixelVec1Scaled = new ArrayList<>();
    private ArrayList<Float> xPixelVec2Scaled = new ArrayList<>();
    private ArrayList<Float> yPixelVec2Scaled = new ArrayList<>();
    private ArrayList<Float> xPixelVec3Scaled = new ArrayList<>();
    private ArrayList<Float> yPixelVec3Scaled = new ArrayList<>();
    private SeekBar quadAllSeek;
    private int togglei = 0;
    private int quadMax = 0;

    private ArrayList<Double> xMeterVec1;
    private ArrayList<Double> yMeterVec1;
    private float x2;
    private float y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setupActionBar();

        Log.i("PreviewActivity", "PreviewActivity started.");

        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        canvasSize = (LinearLayout) findViewById(R.id.linLay);

        DataShare.setSeekLoc(1, 0);
        DataShare.setSeekLoc(2, 0);
        DataShare.setSeekLoc(3, 0);

        xPixelVec1 = DataShare.getXPixelVec(1);
        yPixelVec1 = DataShare.getYPixelVec(1);

        xPixelVec2 = DataShare.getXPixelVec(2);
        yPixelVec2 = DataShare.getYPixelVec(2);

        xPixelVec3 = DataShare.getXPixelVec(3);
        yPixelVec3 = DataShare.getYPixelVec(3);

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
                params.height = (int) (screenHeight - seekbarRelative.getHeight() - (getSupportActionBar().getHeight() * 1.5));
                params.width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5) / pref.getFloat("newHeight", 3)));
                canvasSize.setLayoutParams(params);

                Matrix transform = new Matrix();
                float mainWidth = canvasSize.getWidth();
                float mainHeight = canvasSize.getHeight();
                float previewHeight = params.height;
                float previewWidth = params.width;
                float scaledWidth = previewWidth/mainWidth;
                float scaledHeight = previewHeight/mainHeight;

                Path path1 = DataShare.getPath(1);
                Path path2 = DataShare.getPath(2);
                Path path3 = DataShare.getPath(3);

                transform.postScale(scaledWidth, scaledHeight);

                path1.transform(transform);
                path2.transform(transform);
                path3.transform(transform);


                if(xPixelVec1 != null) {
                    float tempx1 = 0;
                    for (int i = 0; i < xPixelVec1.size() - 1; i++) {
                        tempx1 = xPixelVec1.get(i);
                        tempx1 = tempx1 * scaledWidth;
                        xPixelVec1Scaled.add(i, tempx1);
                        //Log.i("xPixelVec1Scaled", Float.toString(xPixelVec1Scaled.get(i) - quad1.getWidth() / 2 + canvasSize.getLeft()));
                    }
                    xPixelVec1 = xPixelVec1Scaled;
                    xPixelVec1Scaled = null;

                    float tempy1 = 0;
                    for (int i = 0; i < yPixelVec1.size() - 1; i++) {
                        tempy1 = yPixelVec1.get(i);
                        tempy1 = tempy1 * scaledWidth;
                        yPixelVec1Scaled.add(i, tempy1);
                        //Log.i("yPixelVec1Scaled", Float.toString(yPixelVec1Scaled.get(i) - quad1.getHeight() / 2));
                    }
                    yPixelVec1 = yPixelVec1Scaled;
                    yPixelVec1Scaled = null;
                }

                DataShare.setPath(1,path1);

//                if(xPixelVec2 != null) {
//                    float tempx2 = 0;
//                    for (int i = 0; i < xPixelVec2.size() - 1; i++) {
//                        tempx2 = xPixelVec2.get(i);
//                        tempx2 = tempx2 * scaledWidth;
//                        xPixelVec2Scaled.add(i, tempx2);
//                    }
//                    xPixelVec2 = xPixelVec2Scaled;
//
//                    float tempy2 = 0;
//                    for (int i = 0; i < yPixelVec2.size() - 1; i++) {
//                        tempy2 = yPixelVec2.get(i);
//                        tempy2 = tempy2 * scaledWidth;
//                        yPixelVec2Scaled.add(i, tempy2);
//                    }
//                    yPixelVec2 = yPixelVec2Scaled;
//
//                }
//
//                if(xPixelVec3 != null) {
//                    float tempx3 = 0;
//                    for (int i = 0; i < xPixelVec3.size() - 1; i++) {
//                        tempx3 = xPixelVec3.get(i);
//                        tempx3 = tempx3 * scaledWidth;
//                        xPixelVec3Scaled.add(i, tempx3);
//                    }
//                    xPixelVec3 = xPixelVec3Scaled;
//
//                    float tempy3 = 0;
//                    for (int i = 0; i < yPixelVec3.size() - 1; i++) {
//                        tempy3 = yPixelVec3.get(i);
//                        tempy3 = tempy3 * scaledWidth;
//                        yPixelVec3Scaled.add(i, tempy3);
//                    }
//                    yPixelVec3 = yPixelVec3Scaled;
//                }

                // remember to remove the listener if possible
                ViewTreeObserver viewTreeObserver = seekbarRelative.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });

        // TODO if path planner servicing is enabled, redo the path
        if (pref.getBoolean("serviceToggle", false)) {
            xPixelVec1 = new ArrayList<>();
            yPixelVec1 = new ArrayList<>();

            xMeterVec1 = new ArrayList<>();
            yMeterVec1 = new ArrayList<>();
            convertROSPathToPixelVec(1, DataShare.getServicedPath(1));

            Log.i("NNNNNNNNNNNNN", "" + xMeterVec1.size());
            for(int i = 0; i < xMeterVec1.size() - 1; i++) {
                Log.i("NX", "" + xMeterVec1.get(i));
            }

            for(int i = 0; i < yMeterVec1.size() - 1; i++) {
                Log.i("NY", "" + yMeterVec1.get(i));
            }

            //Log.i("PreviewActivity","Is Path null: " + Boolean.toString(DataShare.getPath(1) == null));
        }

        previewCanvas = (PreviewCanvas) findViewById(R.id.preview_canvas);
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
                    if (xPixelVec1 == null || yPixelVec1 == null) {
                        quad1.setVisibility(View.INVISIBLE);
                    } else {
                        // place imageView at start of path on beginning of PreviewActivity
                        if (togglei == 0) {
                            quad1.setX(xPixelVec1.get(0) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            //
                            quad1.setY(yPixelVec1.get(0) - quad1.getHeight() / 2);
                            quad1.setVisibility(View.VISIBLE);
                        }

                    }
                    //quad1.setImageAlpha( (int)(((DataShare.getInstance("quad1").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                }

                // quad 2
                if (pref.getBoolean("quad2", false)) {
                    if (xPixelVec2 == null || yPixelVec2 == null) {
                        quad2.setVisibility(View.INVISIBLE);
                    } else {
                        // place imageView at start of path on beginning of PreviewActivity
                        if (togglei == 0) {
                            quad2.setX(xPixelVec2.get(0) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad2.setY(yPixelVec2.get(0) - quad1.getHeight() / 2);
                            quad2.setVisibility(View.VISIBLE);
                        }
                    }
//                    quad2.setImageAlpha( (int)(((DataShare.getInstance("quad2").getZ()/pref.getFloat("newAltitude",2))*0.75+0.25)*255.0) );
                }

                // quad 3
                if (pref.getBoolean("quad3", false)) {
                    if (xPixelVec3 == null || yPixelVec3 == null) {
                        quad3.setVisibility(View.INVISIBLE);
                    } else {
                        // place imageView at start of path on beginning of PreviewActivity
                        if (togglei == 0) {
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
            public boolean onTouch(View v, MotionEvent event) {
                int eid = event.getAction();
                switch (eid) {
                    case MotionEvent.ACTION_MOVE:
                        Log.i("ACTION_MOVE","mmmmoooovvvveee");
                        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                        quad1.setX((int) (StartPT.x + mv.x));
                        quad1.setY((int) (StartPT.y + mv.y));
                        StartPT = new Point((int) quad1.getX(), (int) quad1.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        Log.i("ACTION_DOWN","ddddoooowwwwnnnn");
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new Point((int) quad1.getX(), (int) quad1.getY());

                        break;
                    case MotionEvent.ACTION_UP:

                        Log.i("ACTION_UP","uuuupppp");
                        int t = 0;
                        double distance = 0;
                        double leastdistance = 100000000;// needs to be large in order for distance to enter into the loop
                        int leastdistancei = 0;
                        double d = 0.0;
                        float x1 = quad1.getX()- quad1.getWidth()/2 - canvasSize.getLeft();
                        float x2 = x1;
                        float y2 = 0;
                        float y1 = quad1.getY()- quad1.getHeight()/2;

                       // Log.i("x position of the quad",Float.toString(x1));
                        int p = 0;
                        while(p < xPixelVec1.size()){
                            //Log.i("all variables in string",Float.toString(xPixelVec1.get(p)));
                            p ++;
                        }
                        while(t < xPixelVec1.size()-1) {
                           // Log.i("while loop", "this is the while loop");
                            x2 = xPixelVec1.get(t);
                            y2 = yPixelVec1.get(t);

                            d = Math.pow(x2 - x1, 2.0) + Math.pow(y2 - y1, 2.0);// part 1 of the distance formula (x2 - x1)^2 + (y2 - y1)^2
                            distance = Math.sqrt((float) d);// converts from double to float then does the square root of it

                            //Log.i("Distance", Double.toString(distance));
                            if (distance < leastdistance) {// if the distance found is smaller than the greatest
                                leastdistance = distance;// then it becomes the leastdistance
                                leastdistancei = t;
                               // Log.i("Leastdistancei", Integer.toString(leastdistancei));
                            }
                            t++;
                        }
                        quad1.setX(xPixelVec1.get(leastdistancei) - quad1.getWidth()/2 + canvasSize.getLeft());
                        quad1.setY(yPixelVec1.get(leastdistancei) - quad1.getHeight()/2 );
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        quad2.setOnTouchListener(new View.OnTouchListener() {
            Point DownPT = new Point(); // Record mouse position when pressed down
            Point StartPT = new Point(); // Record start position of quad1

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eid = event.getAction();
                switch (eid) {
                    case MotionEvent.ACTION_MOVE:
                        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                        quad2.setX((int) (StartPT.x + mv.x));
                        quad2.setY((int) (StartPT.y + mv.y));
                        StartPT = new Point((int) quad2.getX(), (int) quad2.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new Point((int) quad2.getX(), (int) quad2.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        // Nothing have to do
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        quad3.setOnTouchListener(new View.OnTouchListener() {
            Point DownPT = new Point(); // Record mouse position when pressed down
            Point StartPT = new Point(); // Record start position of quad1

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int eid = event.getAction();
                switch (eid) {
                    case MotionEvent.ACTION_MOVE:
                        PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                        quad3.setX((int) (StartPT.x + mv.x));
                        quad3.setY((int) (StartPT.y + mv.y));
                        StartPT = new Point((int) quad3.getX(), (int) quad3.getY());
                        break;
                    case MotionEvent.ACTION_DOWN:
                        DownPT.x = (int) event.getX();
                        DownPT.y = (int) event.getY();
                        StartPT = new Point((int) quad3.getX(), (int) quad3.getY());
                        break;
                    case MotionEvent.ACTION_UP:
                        // Nothing have to do
                        break;
                    default:
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
                        } else if (!isChecked && DataShare.getPlayBackState()) {
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

//                                            if (index == DataShare.getCurrentTime(1).size()) {
//                                                // This will handle the error come back to this
//
//                                            } else if (index > DataShare.getCurrentTime(1).size()) {
//                                                quad1.setX(xPixelVec1.size() - quad1.getWidth() / 2+ canvasSize.getLeft());
//                                                quad1.setY(yPixelVec1.size() - quad1.getHeight() / 2);
//
//                                            } else {
//                                                quad1.setX(xPixelVec1.get(index) - quad1.getWidth() / 2 + canvasSize.getLeft());
//                                                quad1.setY(yPixelVec1.get(index) - quad1.getHeight() / 2);
//                                            }

                                            while(index < DataShare.getCurrentTime(1).size()-1){
                                                quad1.setX(xPixelVec1.get(index) - quad1.getWidth() / 2 + canvasSize.getLeft());
                                                quad1.setY(yPixelVec1.get(index) - quad1.getHeight() / 2);
                                            }
                                        }
                                        if (xPixelVec2 != null) {

                                            if (index == DataShare.getCurrentTime(2).size()) {
                                                // This will handle the error

                                            } else if (index > DataShare.getCurrentTime(2).size()) {
                                                quad2.setX(xPixelVec2.size() - quad2.getWidth() / 2 + canvasSize.getLeft());
                                                quad2.setY(yPixelVec2.size() - quad2.getHeight() / 2);

                                            } else {
                                                quad2.setX(xPixelVec2.get(index) - quad2.getWidth() / 2 + canvasSize.getLeft());
                                                quad2.setY(yPixelVec2.get(index) - quad2.getHeight() / 2);
                                            }
                                        }
                                        if (xPixelVec3 != null) {

                                            if (index == DataShare.getCurrentTime(3).size()) {
                                                // This will handle the error

                                            } else if (index > DataShare.getCurrentTime(3).size()) {
                                                quad3.setX(xPixelVec3.size() - quad3.getWidth() / 2 + canvasSize.getLeft());
                                                quad3.setY(yPixelVec3.size() - quad3.getHeight() / 2);

                                            } else {
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

                while (DataShare.getSeekLoc(1) < xPixelVec1.size() - 1) {
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

                            //quad1.setX(xPixelVec1.get(value) - quad1.getWidth() / 2 + canvasSize.getLeft());
                            //quad1.setY(yPixelVec1.get(value) - quad1.getHeight() / 2);
                            quad1.setX(xPixelVec1.get(xPixelVec1.size()-1)-quad1.getWidth() / 2 + canvasSize.getLeft());
                            quad1.setY(yPixelVec1.get(yPixelVec1.size()-1)-quad1.getHeight() / 2);
                            Log.i("QUAD! X POSITION screen" , Float.toString(quad1.getX()));
                            Log.i("QUAD! Y POSITION screen" , Float.toString(quad1.getY()));
                            //Log.i("QUAD! X POSITION array" , Float.toString(xPixelVec1.get(xPixelVec1.size())));
                            //Log.i("QUAD! Y POSITION array" , Float.toString(yPixelVec1.get(yPixelVec1.size())));


                            if (toggle.isChecked()) {
                                DataShare.setSeekLoc(1, value + 1);
                            } else {
                                DataShare.setPlayBackState(false);
                            }

                            if (value >= xPixelVec1.size() - 1) {
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

                while (DataShare.getSeekLoc(2) < xPixelVec2.size() - 1) {
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
                            } else {
                                DataShare.setPlayBackState(false);
                            }

                            if (value >= xPixelVec2.size() - 1) {
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

                while (DataShare.getSeekLoc(3) < xPixelVec3.size() - 1) {
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
                            } else {
                                DataShare.setPlayBackState(false);
                            }

                            if (value >= xPixelVec3.size() - 1) {
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

        //Log.i("11111111111", Float.toString(DataShare.getCurrentTime(1).size()));
        //Log.i("22222222222", Float.toString(DataShare.getCurrentTime(2).size()));
        //Log.i("33333333333", Float.toString(DataShare.getCurrentTime(3).size()));
        //Log.i("MMMMMMMMMMM", Float.toString(Math.max(Math.max(DataShare.getCurrentTime(1).size(), DataShare.getCurrentTime(2).size()), DataShare.getCurrentTime(3).size())));
        final Handler seekH = new Handler();
        Runnable seekR = new Runnable() {
            @Override
            public void run() {
                if (DataShare.getCurrentTime(1).size() == quadAllSeek.getMax()) {
                    quadMax = 1;
                } else if (DataShare.getCurrentTime(2).size() == quadAllSeek.getMax()) {
                    quadMax = 2;
                } else {
                    quadMax = 3;
                }
                while (quadMax < quadAllSeek.getMax()) {
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
                            if (value != 0) {
                                quadAllSeek.setProgress(value);
                                quadAllSeek.setProgress(quadAllSeek.getMax());
                            }
                            if (value == quadAllSeek.getMax()) {
                                quadAllSeek.setProgress(quadAllSeek.getMax());
                                toggle.setChecked(false);
                            }
//                            while(value < quadAllSeek.getMax()-1){
//                                quadAllSeek.setProgress(value);
//
//                            }



                            //Log.i("VauleValueValue", Integer.toString(value));
                            //Log.i("MaxMaxMax", Integer.toString(max));
                            //Log.i("quadMaxquadMaxquadMax", Integer.toString(quadAllSeek.getMax()));
                        }
                    });
                }

            }
        };
        new Thread(seekR).start();
    }

    // convert nav_msgs/Path to ArrayList of pixels
    private void convertROSPathToPixelVec(int k, nav_msgs.Path p) {

        if (p == null) {
            Log.i("PreviewActivity", "Received null nav_msgs/Path.");
        } else {
            Log.i("PreviewActivity", "Received valid nav_msgs/Path. Size: " + Double.toString(p.getPoses().size()));
            switch (k) {
                case 1:
                    android.graphics.Path mPath1 = new android.graphics.Path();

                    for (int i = 0; i < p.getPoses().size(); i++) {
                        // TODO convert meters to pixels

                        double xMeter = p.getPoses().get(i).getPose().getPosition().getX();
                        double yMeter = p.getPoses().get(i).getPose().getPosition().getY();

                        xMeterVec1.add(xMeter);
                        yMeterVec1.add(yMeter);

                        //Log.i("PreviewActivity","xMeter: " + Double.toString(xMeter) + "\t yMeter: " + Double.toString(yMeter) + " # " + Integer.toString(i + 1));

                        float xPixel = (float) xMeterToPixel(xMeter, yMeter);
                        float yPixel = (float) yMeterToPixel(xMeter, yMeter);

                        xPixelVec1.add(xPixel);
                        yPixelVec1.add(yPixel);

                        if (i == 0) {
                            mPath1.moveTo(xPixel, yPixel);
                        } else {
                            //mPath1.quadTo((xPixelVec1.get(i) + xPixelVec1.get(i - 1)) / 2, (yPixelVec1.get(i) + yPixelVec1.get(i - 1)) / 2, xPixelVec1.get(i), yPixelVec1.get(i));
                            //mPath1.quadTo(xPixelVec1.get(i - 1), yPixelVec1.get(i - 1), (xPixelVec1.get(i) + xPixelVec1.get(i - 1)) / 2, (yPixelVec1.get(i) + yPixelVec1.get(i - 1)) / 2);
                            mPath1.lineTo(xPixel, yPixel);
                        }
                    }

                    DataShare.setPath(1, mPath1);
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }
    }

    // transform specified object's y position to pixels
    public double xMeterToPixel(double x, double y) {

        double normX = y / -pref.getFloat("newWidth",5);
        double transX = normX * canvasSize.getLayoutParams().width;
        double xCoord = canvasSize.getX() + canvasSize.getLayoutParams().width/2 + transX;

        return xCoord;
    }

    // transform specified object's y position to pixels
    public double yMeterToPixel(double x, double y) {

        double normY = x / pref.getFloat("newHeight",3);
        double transY = normY * canvasSize.getLayoutParams().height;
        double yCoord = canvasSize.getY() + canvasSize.getLayoutParams().height/2 - transY;

        return yCoord;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}