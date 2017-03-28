package utmg.android_interface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ros.android.AppCompatRosActivity;
import org.ros.android.view.RosTextView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatRosActivity {

    TrajectoryArrayPublisherNode node;
    private LinearLayout innerLayout;
    private CanvasView customCanvas;
    private ArrayList<Float> xCoordVec;
    private ArrayList<Float> yCoordVec;
    private float centerX;
    private float centerY;
    private float transX;
    private float transY;
    private float nX;
    private float nY;

    private RosTextView<std_msgs.String> rosTextView;


    public MainActivity() { super("MainActivity", "MainActivity"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        xCoordVec = new ArrayList<>();
        yCoordVec = new ArrayList<>();

        // Send FAB
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

                node.setTraj(xCoordVec, yCoordVec);
                //org.ros.message.std_msgs.String str = new org.ros.message.std_msgs.String();

            }
        });


        // Clear FAB
        FloatingActionButton fabClear = (FloatingActionButton) findViewById(R.id.fab_clear);
        fabClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                customCanvas.clearCanvas();
                Snackbar.make(view, "Cleared!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // Get x and y coordinates in pixels
        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);

//        final TextView tvX = (TextView) findViewById(R.id.xView);
//        final TextView tvY = (TextView) findViewById(R.id.yView);
//
//        tvX.setText(Float.toString(customCanvas.getxCoord()));
//        tvY.setText(Float.toString(customCanvas.getyCoord()));
//
//        final Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                tvX.setText(Float.toString(customCanvas.getxCoord()));
//                tvY.setText(Float.toString(customCanvas.getyCoord()));
//
//                handler.postDelayed(this, 10); // refresh every 1000 ms = 1 sec
//            }
//        };
//        runnable.run();


        // Get center coordinates in pixels
        innerLayout = (LinearLayout) findViewById(R.id.linLay);
        //final TextView centerCoord = (TextView) findViewById(R.id.centerCoord);

        innerLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                centerX  = customCanvas.getWidth()/2;
                centerY = customCanvas.getHeight()/2;
                //centerCoord.setText("(" + Float.toString(centerX) + ", " + Float.toString(centerY) + ")");
            }
        });


//        // Transform x and y with the center equal to (0,0)
//        final TextView newX = (TextView) findViewById(R.id.newX);
//        final TextView newY = (TextView) findViewById(R.id.newY);
//
//        final Handler handler1 = new Handler();
//        Runnable runnable1 = new Runnable() {
//            @Override
//            public void run() {
//                transX = customCanvas.getxCoord() - centerX;
//                transY = -(customCanvas.getyCoord() - centerY);
//                newX.setText("transX: " + Float.toString(transX));
//                newY.setText("     transY: " + Float.toString(transY));
//
//                handler1.postDelayed(this, 10);
//            }
//        };
//        runnable1.run();


        // Normalize x and y
        final TextView normX = (TextView) findViewById(R.id.normX);
        final TextView normY = (TextView) findViewById(R.id.normY);

        final Handler handler2 = new Handler();
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                nX = transX/customCanvas.getWidth();
                nY = transY/customCanvas.getHeight();
                normX.setText("normX: " + Float.toString(nX));
                normY.setText("    normY:" + Float.toString(nY));

                handler2.postDelayed(this, 10);
            }
        };
        runnable2.run();


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


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
//        rosTextView.setText("test");
//        rosTextView.setTopicName("testtopic");
//        rosTextView.setMessageType("std_msgs/String");

        node = new TrajectoryArrayPublisherNode();

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

            //nodeConfiguration.setNodeName()

            nodeMainExecutor.execute(node, nodeConfiguration);
        } catch (IOException e) {
            // Socket problem
            Log.e("MainActivity", "socket error trying to get networking information from the master uri");
        }

    }

}
