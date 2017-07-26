package utmg.android_interface.Activities;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import org.ros.android.AppCompatRosActivity;
import org.ros.master.client.MasterStateClient;
import org.ros.message.Time;
import org.ros.node.Node;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import java.io.IOException;
import java.util.ArrayList;

import utmg.android_interface.DataShare;
import utmg.android_interface.QuadUtils.Quad;
import utmg.android_interface.R;
import utmg.android_interface.ROSClasses.ROSNodeMain;
import utmg.android_interface.ROSClasses.ROSNodeService;

public class MainActivity extends AppCompatRosActivity {

    ROSNodeMain nodeMain;
    ROSNodeService nodeService;
    private CanvasView customCanvas;

    private Switch quad1Switch;
    private Switch quad2Switch;
    private Switch quad3Switch;

    SharedPreferences pref;

    private LinearLayout canvasSize;
    private int screenHeight;
    private int screenWidth;
    private float canvasWidth;
    private float canvasHeight;
    private ArrayList<Quad> quads;
    private int qIndex;

    public static Context contextOfApplication;

    public MainActivity() { super("MainActivity", "MainActivity",(java.net.URI)DataShare.retrieve("masterUri")); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("create");
        setContentView(R.layout.activity_main);

        contextOfApplication = getApplicationContext();

        // instantiating SharedPreferences
        pref = getSharedPreferences("Pref", 0);

        //Get stuff from DataShare
        quads=(ArrayList<Quad>)DataShare.retrieve("quads");
        qIndex=0;

        // instantiating canvas
        canvasSize = (LinearLayout) findViewById(R.id.linLay);

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * (pref.getFloat("newWidth", 5)/pref.getFloat("newHeight", 3)));

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);

        Log.i("canvasSize", canvasSize.getLayoutParams().width + "\t" + canvasSize.getLayoutParams().height);

        // instantiating z control slider
        FrameLayout sbLayout = (FrameLayout) findViewById(R.id.slider_frame_layout);
        sbLayout.getLayoutParams().height = (int) (screenHeight * 0.6);

        // instantiating local copy of input time history vectors

        // instantiating toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Button to terminate app so it doesn't have to be done manually
        Button terminate = (Button)this.findViewById(R.id.kill_button);
        terminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        // PreviewActivity Button
        final Button preview = (Button) findViewById(R.id.previewButton);
        preview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
            // compress arrays, send to path planner, wait to launch PreviewActivity until service finishes
            if(pref.getBoolean("serviceToggle",false)) {
                if (xCoordVec1.size() != 0) {
                    compressArrays(1);
                    nodeService.setTrajectory(xCompressed1, yCompressed1, zCompressed1, timeCompressed1);
                    while (DataShare.getServicedPath(1) == null) {//todo this is kinda wrong
                    }
                }
                if (xCoordVec2.size() != 0) {
                    compressArrays(2);
                    nodeService.setTrajectory(xCompressed2, yCompressed2, zCompressed2, timeCompressed2);
                    while (DataShare.getServicedPath(2) == null) {
                    }
                }
                if (xCoordVec3.size() != 0) {
                    compressArrays(3);
                    nodeService.setTrajectory(xCompressed3, yCompressed3, zCompressed3, timeCompressed3);
                    while (DataShare.getServicedPath(3) == null) {
                    }
                }
            }

            Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
            startActivity(intent);
            }
        });

        // Send FAB
        //// TODO: 7/25/2017 Dynamically generate action buttons
        final FloatingActionsMenu fabSent = (FloatingActionsMenu) findViewById(R.id.fab);
        final com.getbase.floatingactionbutton.FloatingActionButton sendAll = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendAll);
        final com.getbase.floatingactionbutton.FloatingActionButton sendQuad1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendQuad1);
        final com.getbase.floatingactionbutton.FloatingActionButton sendQuad2 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendQuad2);
        final com.getbase.floatingactionbutton.FloatingActionButton sendQuad3 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.sendQuad3);
        fabSent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        sendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Sent All!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                nodeMain.sendAll();
            }
        });

        sendQuad1.setOnClickListener(new sendOnClickListener());

        sendQuad2.setOnClickListener(new sendOnClickListener());

        sendQuad3.setOnClickListener(new sendOnClickListener());



        // Clear FAB
        //// TODO: 7/25/2017 Generate these automatically too ffs
        FloatingActionsMenu fabClear = (FloatingActionsMenu) findViewById(R.id.fab_clear);
        final com.getbase.floatingactionbutton.FloatingActionButton clearAll = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearAll);
        final com.getbase.floatingactionbutton.FloatingActionButton clearQuad1 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearQuad1);
        final com.getbase.floatingactionbutton.FloatingActionButton clearQuad2 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearQuad2);
        final com.getbase.floatingactionbutton.FloatingActionButton clearQuad3 = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.clearQuad3);
        fabClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customCanvas.clearCanvas();
                Snackbar.make(v, "Cleared All!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        clearQuad1.setOnClickListener(new clearOnClickListener());

        clearQuad2.setOnClickListener(new clearOnClickListener());

        clearQuad3.setOnClickListener(new clearOnClickListener());

        // TextView for displaying z control slider value
        final TextView seekbarValue = (TextView) findViewById(R.id.seekbar_value);
        final SeekBar slider = (SeekBar) findViewById(R.id.slider);
        slider.getLayoutParams().width = sbLayout.getLayoutParams().height;
//        slider.getLayoutParams().width = (int)(screenHeight * 0.65);
        final Handler seekbarH = new Handler();
        Runnable seekbarR = new Runnable() {
            @Override
            public void run() {
                int max = (int) pref.getFloat("newAltitude", 2);
                slider.setMax(max * 100);
                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) { }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float value = ((float) progress / 100);
                        seekbarValue.setText(Float.toString(value));
                    }
                });
                seekbarH.postDelayed(this, 10);
            }
        };
        seekbarR.run();

        // Get new dimensions from CanvasSizeActivity
        final TextView newDimensionText = (TextView) findViewById(R.id.new_dimensions);
        final Handler canvasH = new Handler();
        Runnable canvasR = new Runnable() {
            @Override
            public void run() {
                canvasWidth = pref.getFloat("width", 5);
                canvasHeight = pref.getFloat("height", 3);
                newDimensionText.setText(Float.toString(canvasWidth) + "m, " + Float.toString(canvasHeight) + "m");

                newDimension(canvasWidth, canvasHeight);
                canvasH.postDelayed(this, 1000);
            }
        };
        canvasR.run();


        // display position of quad in meters
        final TextView inMeters = (TextView) findViewById(R.id.inMeters);
        final TextView quad2Pixel = (TextView) findViewById(R.id.quad2pixel);
        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                if (!pref.getBoolean("debugMode", false)) {
//                    inMeters.setVisibility(View.INVISIBLE);
//                    quad2Pixel.setVisibility(View.INVISIBLE);
//                }
//                else {
//                    inMeters.setVisibility(View.VISIBLE);
//                    quad2Pixel.setVisibility(View.VISIBLE);
//
//                    inMeters.setText(Float.toString(mX) + "mX   " + Float.toString(mY) + "mY    ");
//                    quad2Pixel.setText(Float.toString(objectXToPixel("quad")) + "xdp    " + Float.toString(objectYToPixel("quad")) + "ydp");
//
//                    handler.postDelayed(this, 10);
//
//                }
//            }
//        };
//        runnable.run();

        // quad display config
        {
            // set quad size
            final ImageView quad1 = (ImageView) findViewById(R.id.quad1);
            final ImageView quad2 = (ImageView) findViewById(R.id.quad2);
            final ImageView quad3 = (ImageView) findViewById(R.id.quad3);

            quad1.getLayoutParams().height = (int) (screenHeight * 0.05);
            quad1.getLayoutParams().width = (int) (screenWidth * 0.05);
            //quad1.setColorFilter(DataShare.getInstance("quad1").getQuadColour());

            quad2.getLayoutParams().height = (int) (screenHeight * 0.05);
            quad2.getLayoutParams().width = (int) (screenWidth * 0.05);
            //quad2.setColorFilter(DataShare.getInstance("quad2").getQuadColour());

            quad3.getLayoutParams().height = (int) (screenHeight * 0.05);
            quad3.getLayoutParams().width = (int) (screenWidth * 0.05);
            //quad3.setColorFilter(DataShare.getInstance("quad3").getQuadColour());


            // quad control toggle switches
            //// TODO: 7/25/2017 Make a spinner
            quad1Switch = (Switch) findViewById(R.id.quad1Switch);
            quad2Switch = (Switch) findViewById(R.id.quad2Switch);
            quad3Switch = (Switch) findViewById(R.id.quad3Switch);

            quad1Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    customCanvas.setQuad(buttonView.getText().toString());
                    setQuad(buttonView.getText().toString());
                    quad2Switch.setChecked(false);
                    quad3Switch.setChecked(false);
                }
                }
            });

            quad2Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    customCanvas.setQuad(buttonView.getText().toString());
                    setQuad(buttonView.getText().toString());
                    quad1Switch.setChecked(false);
                    quad3Switch.setChecked(false);
                }
                }
            });

            quad3Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    customCanvas.setQuad(buttonView.getText().toString());
                    setQuad(buttonView.getText().toString());
                    quad1Switch.setChecked(false);
                    quad2Switch.setChecked(false);
                }
                }
            });

            // show real-time location of the quads
            final Handler handlerQuad = new Handler();
            Runnable runnableQuad = new Runnable() {
                @Override
                public void run() {
                    // quad 1
                customCanvas.update();
                handlerQuad.postDelayed(this, 20);
                }
            };
            runnableQuad.run();
        }
    }

    public void compressArrays(int quad) {
        int i = 0;
        float siddarth = 1;
        float x1, x2 = 0;
        float y1, y2 = 0;
        float slope1 = 0;
        float slope2 = 0;

        Log.i(" 1111111111 ", "" + xCoordVec1.size());
        xCompressed1.add(xCoordVec1.get(0));
        yCompressed1.add(yCoordVec1.get(0));
        zCompressed1.add(zCoordVec1.get(0));
        timeCompressed1.add(timesVec1.get(0));
        while (i + 1 < xCoordVec1.size() - 1) {
            x1 = xCoordVec1.get(i);
            x2 = xCoordVec1.get(i + 1);
            y1 = yCoordVec1.get(i);
            y2 = yCoordVec1.get(i + 1);
            slope1 = (y2 - y1)/(x2 - x1);
            if(Math.abs(slope1 - slope2) < siddarth) {
                xCompressed1.add(x2);
                yCompressed1.add(y2);
                timeCompressed1.add(timesVec1.get(i+1));
                zCompressed1.add(zCoordVec1.get(i+1));
            }
            slope2 = slope1;
            i++;
        }
        Log.i(" 1C1C1C1C1C1 ", "" + xCompressed1.size());


    }

    // rescaling canvas proportions to (somewhat) fit screen depending on screen orientation
    // TODO
    public void newDimension(float w, float h) {
        float scale = w/h;
        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.65);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height * scale);
    }

    // transform specified object's x position to pixels
    // TODO redo to match new design architecture
    public float objectXToPixel(String item) {

        float normX = 0;

        if (item.equals("quad1")) {
            normX = (float) DataShare.getInstance("quad1").getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("quad2")) {
            Thing quad2 = DataShare.getInstance("quad2");
            normX = (float) quad2.getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("quad3")) {
            Thing quad3 = DataShare.getInstance("quad3");
            normX = (float) quad3.getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("sword")) {
            Thing sword = DataShare.getInstance("sword");
            normX = (float) sword.getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("obstacle1")) {
            Thing obstacle1 = DataShare.getInstance("obstacle1");
            normX = (float) obstacle1.getY() / -pref.getFloat("newWidth",5);
        }
        else if (item.equals("obstacle2")) {
            Thing obstacle2 = DataShare.getInstance("obstacle2");
            normX = (float) obstacle2.getY() / -pref.getFloat("newWidth",5);
        }

        float transX = normX * canvasSize.getWidth();
        float xCoord = canvasSize.getX() + canvasSize.getWidth()/2 + transX;

        return xCoord;
    }

    // transform specified object's y position to pixels
    public float objectYToPixel(String item) {

        float normY = 0;

        if (item.equals("quad1")) {
            Thing quad1 = DataShare.getInstance("quad1");
            normY = (float) quad1.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("quad2")) {
            Thing quad2 = DataShare.getInstance("quad2");
            normY = (float) quad2.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("quad3")) {
            Thing quad3 = DataShare.getInstance("quad3");
            normY = (float) quad3.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("sword")) {
            Thing sword = DataShare.getInstance("sword");
            normY = (float) sword.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("obstacle1")) {
            Thing obstacle1 = DataShare.getInstance("obstacle1");
            normY = (float) obstacle1.getX() / pref.getFloat("newHeight",3);
        }
        else if (item.equals("obstacle2")) {
            Thing obstacle2 = DataShare.getInstance("obstacle2");
            normY = (float) obstacle2.getX() / pref.getFloat("newHeight",3);
        }

        float transY = normY * canvasSize.getHeight();
        float yCoord = canvasSize.getY() + canvasSize.getHeight()/2 - transY;

        return yCoord;
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
//        if (id == R.id.action_roscam) {
//            startActivity(new Intent(MainActivity.this, ROSCam.class));
//        }

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
//        rosTextView.setText("test");
//        rosTextView.setTopicName("testtopic");
//        rosTextView.setMessageType("std_msgs/String");
        System.out.println("init");
        nodeMain = new ROSNodeMain();
        nodeService = new ROSNodeService();

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

            //nodeConfiguration.setNodeName()
            nodeMainExecutor.execute(nodeMain, nodeConfiguration);
            nodeMainExecutor.execute(nodeService, nodeConfiguration);
        } catch (IOException e) {
            // Socket problem
            Log.e("MainActivity", "socket error trying to get networking information from the master uri");
        }

    }
    public void setQuad(String name){
        for(int i=0;i<quads.size();i++){
            if(quads.get(i).getName().equals(name)){
                qIndex=i;
                break;
            }
        }
    }
    class sendOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Sent Quad3!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            nodeMain.sendQuad(((FloatingActionButton)v).getTitle());
        }
    }
    class clearOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Snackbar.make(v, "Sent Quad3!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            customCanvas.clearQuad(((FloatingActionButton)v).getTitle());
        }
    }

}