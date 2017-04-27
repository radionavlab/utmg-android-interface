package utmg.android_interface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.ros.android.AppCompatRosActivity;
import org.ros.android.view.RosTextView;
import org.ros.android.view.camera.RosCameraPreviewView;
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
    private ArrayList<Float> zCoordVec;

    private float mX;
    private float mY;

    private double quadx = 0;
    private double quady = 0;
    private double quadz = 0;


    private double swordx = 0;
    private double swordy = 0;
    private double swordz = 0;

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private LinearLayout canvasSize;
    private int screenHeight;
    private int screenWidth;
    private float canvasWidth;
    private float canvasHeight;

    private RosTextView<std_msgs.String> rosTextView;


    public MainActivity() { super("MainActivity", "MainActivity"); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasSize = (LinearLayout) findViewById(R.id.linLay);

        screenHeight = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().heightPixels;

        canvasSize.getLayoutParams().height = (int) (screenHeight * 0.75);
        canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height / 1.6);

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);
        xCoordVec = new ArrayList<>();
        yCoordVec = new ArrayList<>();

        pref = getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();


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


        final TextView seekbarValue = (TextView) findViewById(R.id.seekbar_value);
        final SeekBar slider = (SeekBar) findViewById(R.id.slider);
        if(slider.getProgress() > 0) {
            slider.setProgress(0);
        }
        final Handler seekbarH = new Handler();
        Runnable seekbarR = new Runnable() {
            @Override
            public void run() {
                int max = (int) pref.getFloat("newAltitude", 2);
                slider.setMax(max * 100);
                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        // TODO Auto-generated method stub
                        float value = ((float) progress / 100);
                        seekbarValue.setText(Float.toString(value));
                         // = value;
                    }
                });
                seekbarH.postDelayed(this, 10);
            }
        };
        seekbarR.run();


        // Get new dimensions from CanvasSizeActivity
        final TextView newDimension = (TextView) findViewById(R.id.new_dimensions);
        final Handler canvasH = new Handler();
        Runnable canvasR = new Runnable() {
            @Override
            public void run() {
                canvasWidth = pref.getFloat("newWidth", 0);
                canvasHeight = pref.getFloat("newHeight", 0);
                newDimension.setText(Float.toString(canvasWidth) + "m, " + Float.toString(canvasHeight) + "m");

                newDimension(canvasWidth, canvasHeight);
                canvasH.postDelayed(this, 10);
            }
        };
        canvasR.run();


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


        // Thread for pinging obstacle's position in meters
        final Handler swordPosHandler = new Handler();
        Runnable swordPosRunnable = new Runnable() {
            @Override
            public void run() {
                if (node != null) {

                    swordx = node.getSwordPosX();
                    swordy = node.getSwordPosY();
                    swordz = node.getSwordPosZ();

                    //Log.i("QuadPos", Double.toString(quadx) + "\t" + Double.toString(quady) + "\t\t" + Double.toString(quadz));
                }
                swordPosHandler.postDelayed(this, 0);
            }
        };
        swordPosRunnable.run();


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


        // set quad size
        final ImageView quad = (ImageView) findViewById(R.id.quad);
        quad.setMaxHeight((int)(screenHeight * .1));
        quad.setMaxWidth((int)(screenWidth * .1));

        // show real-time location of the quad
        final Handler handler2 = new Handler();
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                if(pref.getBoolean("quad", false) == true) {
                    quad.setVisibility(View.VISIBLE);
                }
                else if(pref.getBoolean("quad", false) == false) {
                    quad.setVisibility((View.INVISIBLE));
                }
                //Log.i("vis", Boolean.toString(pref.getBoolean("quad",false)));

                quad.setX(quadXToPixel() + quad.getWidth()/2);
                quad.setY(quadYToPixel() + quad.getHeight()/2);

                handler2.postDelayed(this, 0);
            }
        };
        runnable2.run();


        // set obstacle size
        final ImageView sword = (ImageView) findViewById(R.id.obstacle);
        sword.setMaxHeight((int)(screenHeight * .12));
        sword.setMaxWidth((int)(screenWidth * .12));

        // show location of the obstacle
        final Handler handler3 = new Handler();
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                sword.setX(swordXToPixel() + sword.getWidth()/2);
                sword.setY(swordYToPixel() + sword.getHeight()/2);

                if(pref.getBoolean("sword", false) == true) {
                    sword.setVisibility(View.VISIBLE);
                }

                handler3.postDelayed(this, 0);
            }
        };
        runnable3.run();

    }

    public void newDimension(float w, float h) {
        if (h > w) {
            float scale = h/w;
            canvasSize.getLayoutParams().height = (int) (screenHeight * 0.75);
            canvasSize.getLayoutParams().width = (int) (canvasSize.getLayoutParams().height / scale);
        }
        else if (w > h) {
            float scale = w/h;
            canvasSize.getLayoutParams().width = (int) (screenWidth * .95);
            canvasSize.getLayoutParams().height = (int) (canvasSize.getLayoutParams().width / scale);
        }
    }

    public float quadXToPixel() {
        float normX = (float) quadx / 3;
        float transX = normX * customCanvas.getWidth();
        float xCoord = transX + customCanvas.getCenterX();
        return xCoord;
    }

    public float quadYToPixel() {
        float normY = (float) quady / 5;
        float transY = normY * customCanvas.getHeight();
        float yCoord = (-transY + customCanvas.getCenterY());

        return yCoord;
    }


    public float swordXToPixel() {
        float normX = (float) swordx / 3;
        float transX = normX * customCanvas.getWidth();
        float xCoord = transX + customCanvas.getCenterX();

        return xCoord;
    }

    public float swordYToPixel() {
        float normY = (float) swordy / 5;
        float transY = normY * customCanvas.getHeight();
        float yCoord = (-transY + customCanvas.getCenterY());

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

        else if (id == R.id.resize_canvas) {
            startActivity(new Intent(MainActivity.this, CanvasSizeActivity.class));
        }

        else if (id == R.id.checkbox) {
            startActivity(new Intent(MainActivity.this, ObjectsActivity.class));
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