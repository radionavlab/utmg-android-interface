package utmg.android_interface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.ros.android.AppCompatRosActivity;
import org.ros.android.RosActivity;
import org.ros.android.view.RosTextView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import java.io.IOException;
import java.util.ArrayList;

import std_msgs.String;

public class MainActivity extends AppCompatRosActivity {

    private CanvasView customCanvas;
    private ArrayList<Float> xCoordVec;
    private ArrayList<Float> yCoordVec;

    private RosTextView<std_msgs.String> rosTextView;


    public MainActivity() { super("MainActivity", "MainActivity"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rosTextView = (RosTextView<std_msgs.String>) findViewById(R.id.rosText);

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

        FloatingActionButton fabClear = (FloatingActionButton) findViewById(R.id.fab_clear);
        fabClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Why you no work!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);

        final TextView tvX = (TextView) findViewById(R.id.xView);
        final TextView tvY = (TextView) findViewById(R.id.yView);

        tvX.setText(Float.toString(customCanvas.getxCoord()));
        tvY.setText(Float.toString(customCanvas.getyCoord()));


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            public void run() {

                tvX.setText(Float.toString(customCanvas.getxCoord()));
                tvY.setText(Float.toString(customCanvas.getyCoord()));

                handler.postDelayed(this, 10); // refresh every 1000 ms = 1 sec
            }
        };

        runnable.run();


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


    @Override
    protected void init(NodeMainExecutor nodeMainExecutor)
    {
        rosTextView.setText("test");
        rosTextView.setTopicName("testtopic");
        rosTextView.setMessageType("std_msgs/String");

        try {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());
            nodeMainExecutor.execute(rosTextView, nodeConfiguration);
        } catch (IOException e) {
            // Socket problem
            Log.e("MainActivity", "socket error trying to get networking information from the master uri");
        }

    }

}
