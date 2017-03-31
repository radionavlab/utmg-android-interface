package utmg.android_interface;

import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ros.android.AppCompatRosActivity;
import org.ros.android.view.RosTextView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatRosActivity {

    ROSNode node;
    private CanvasView customCanvas;
    private ArrayList<Float> xCoordVec;
    private ArrayList<Float> yCoordVec;

    private float mX;
    private float mY;

    private double quadx = 0;
    private double quady = 0;
    private double quadz = 0;

    private float quadXCoord = 0;
    private float quadYCoord = 0;

    private RosTextView<std_msgs.String> rosTextView;


    public MainActivity() { super("MainActivity", "MainActivity"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout canvasSize = (LinearLayout) findViewById(R.id.linLay);

        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.75);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height / 1.6);

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);
        xCoordVec = new ArrayList<>();
        yCoordVec = new ArrayList<>();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        // Scaled x and y
        final TextView xMeters = (TextView) findViewById(R.id.meterX);
        final TextView yMeters = (TextView) findViewById(R.id.meterY);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mX = customCanvas.xMeters();
                mY = customCanvas.yMeters();
                xMeters.setText(Float.toString(mX) + "m     ");
                yMeters.setText(Float.toString(mY) + "m     ");

                handler.postDelayed(this, 10);
            }
        };
        runnable.run();


        // Thread for pinging quad's position in meters
        final Handler quadPosHandler = new Handler();
        Runnable quadPosRunnable = new Runnable() {
            @Override
            public void run() {

                if (node != null) {

                    quadx = node.getQuadPosX();
                    quady = node.getQuadPosY();
                    quadz = node.getQuadPosZ();

                    //Log.i("QuadPos", Double.toString(quadx) + "\t" + Double.toString(quady) + "\t\t" + Double.toString(quadz));
                }

                quadPosHandler.postDelayed(this, 0);
            }
        };
        quadPosRunnable.run();


        // Denormalize coordinates from quad
        final TextView quad2Pixel = (TextView) findViewById(R.id.quad2pixel);
        final Handler handler1 = new Handler();
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                quad2Pixel.setText(Float.toString(quadXToPixel()) + "xdp    " + Float.toString(quadYToPixel()) + "ydp");

                handler1.postDelayed(this, 10);
            }
        };
        runnable1.run();


        // set image size
        final ImageView quad = (ImageView) findViewById(R.id.quad);
        quad.setMaxHeight((int)(screenHeight * .1));
        quad.setMaxWidth((int)(screenWidth * .1));

        // show real-time location of the quad
        final Handler handler2 = new Handler();
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                quad.setX(quadXToPixel() + quad.getWidth()/2);
                quad.setY(quadYToPixel() + quad.getHeight()/2);

                handler2.postDelayed(this, 0);
            }
        };
        runnable2.run();
    }

    public float quadXToPixel() {
        float normX = (float) quadx / 3;
        float transX = normX * customCanvas.getWidth();
        float xCoord = transX + customCanvas.centerX();

        return xCoord;
    }

    public float quadYToPixel() {
        float normY = (float) quady / 5;
        float transY = normY * customCanvas.getHeight();
        float yCoord = (-transY + customCanvas.centerY());

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

        node = new ROSNode();

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