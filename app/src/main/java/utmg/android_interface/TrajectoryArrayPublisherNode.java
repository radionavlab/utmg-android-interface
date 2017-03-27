package utmg.android_interface;

import android.support.annotation.NonNull;
import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.internal.message.RawMessage;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import geometry_msgs.Pose;
import geometry_msgs.PoseArray;

import std_msgs.Float32MultiArray;
import std_msgs.MultiArrayDimension;
import std_msgs.MultiArrayLayout;

public class TrajectoryArrayPublisherNode extends AbstractNodeMain implements NodeMain {

    private ArrayList<Float> xes;
    private ArrayList<Float> yes;

    private static final String TAG = TrajectoryArrayPublisherNode.class.getSimpleName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("utmg_android");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        //final Publisher<std_msgs.String> publisher = connectedNode.newPublisher(GraphName.of("time"), std_msgs.String._TYPE);

        final Publisher<geometry_msgs.PoseArray> publisher1 = connectedNode.newPublisher(GraphName.of("android_quad_trajectory"), PoseArray._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                // retrieve current system time
                //String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

                //Log.i(TAG, "publishing the current time: " + time);

                // create and publish a simple string message
                //std_msgs.String str = publisher.newMessage();
                //str.setData("The current time is: " + time);
                //publisher.publish(str);


                // trajectory publisher
                geometry_msgs.PoseArray xyTraj = publisher1.newMessage();

                xyTraj.getHeader().setFrameId("android_xy");
                xyTraj.getHeader().setSeq(0);
                xyTraj.getHeader().setStamp(new Time());

                ArrayList<Pose> poses = new ArrayList<>();

                if (xes == null || yes == null) {
                    Log.i("Traj","Null arrays");
                } else {
                    for (int i = 0; i < xes.size(); i++) {
                        geometry_msgs.Pose msg = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        geometry_msgs.Point temp = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);

                        temp.setX(xes.get(i));
                        temp.setY(yes.get(i));
                        temp.setZ(1);

                        msg.setPosition(temp);

                        poses.add(msg);
                    }

                    xyTraj.setPoses(poses);

                }

                //publisher.publish(str);

                publisher1.publish(xyTraj);


                // go to sleep for one second
                Thread.sleep(1000);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

    void setTraj(ArrayList<Float> x, ArrayList<Float> y) {
        xes = x;
        yes = y;

        Log.i("Traj","Arrays transferred to node");
    }

}
