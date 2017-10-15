package utmg.android_interface.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ros.android.AppCompatRosActivity;
import org.ros.concurrent.CancellableLoop;
import org.ros.concurrent.DefaultScheduledExecutorService;
import org.ros.internal.node.DefaultNode;
import org.ros.master.client.MasterStateClient;
import org.ros.master.client.SystemState;
import org.ros.master.client.TopicType;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.io.IOException;
import java.util.List;

import utmg.android_interface.DataShare;
import utmg.android_interface.R;

/**
 * Created by James on 7/25/2017.
 */

public class SplashActivity extends AppCompatRosActivity {

    ProgressBar progressBar;
    TextView progressTextView;
    public SplashActivity(){super("MainActivity", "MainActivity");}
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) this.findViewById(R.id.splash_progress_bar);
        progressTextView = (TextView) this.findViewById(R.id.splash_text_view);
    }
    public void updateUI(final int percent, final String message){
        this.runOnUiThread(new Runnable() {
            public void run() {
                progressBar.setProgress(percent);
                progressTextView.setText(message);
            }
        });
    }
    public void initCompleted() {
        System.out.println("Thread completed");
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
    public void init(NodeMainExecutor nodeMainExecutor){
        try {
            updateUI(25,"Connecting to Socket");
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            updateUI(50,"Socket Verified");
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());

            //x.setNodeName()
            DefaultNode node = new DefaultNode(nodeConfiguration, null, new DefaultScheduledExecutorService());
            node.executeCancellableLoop(new CancellableLoop() {
                @Override
                protected void loop() throws InterruptedException {
                    Thread.sleep(10);
                }
            });
            updateUI(75,"Reading Topics");
            MasterStateClient msc = new MasterStateClient(node,getMasterUri());
            SystemState ss = msc.getSystemState();
            List<TopicType> list  = msc.getTopicTypes();
            updateUI(100,"Topics read, starting application");
            // TODO: 7/25/2017 Parse topic list and add everything to DataShare
            DataShare.save("masterUri",getMasterUri());
            initCompleted();
        } catch (IOException e) {
            // Socket problem
            Log.e("MainActivity", "socket error trying to get networking information from the master uri");
        }
    }
}
