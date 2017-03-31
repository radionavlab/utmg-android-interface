package utmg.android_interface;

//import org.apache.commons.logging.Log;
import android.util.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.util.ArrayList;

import geometry_msgs.Pose;
import geometry_msgs.PoseArray;

import geometry_msgs.TransformStamped;

public class ROSNode extends AbstractNodeMain implements NodeMain {

    private ArrayList<Float> xes;
    private ArrayList<Float> yes;

    private double quadx = 0;
    private double quady = 0;
    private double quadz = 0;

    private static final String TAG = ROSNode.class.getSimpleName();

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

                xyTraj.getHeader().setFrameId("world");
                xyTraj.getHeader().setSeq(0);
                xyTraj.getHeader().setStamp(new Time());

                ArrayList<Pose> poses = new ArrayList<>();

                if (xes == null || yes == null) {
                    //Log.i("Traj","Null arrays");
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

                    publisher1.publish(xyTraj);

                }


                // listener

                //final Log log = connectedNode.getLog();
                Subscriber<TransformStamped> subscriberQuad = connectedNode.newSubscriber("vicon/Quad7/Quad7", geometry_msgs.TransformStamped._TYPE);

                subscriberQuad.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        quadx = message.getTransform().getTranslation().getX();
                        quady = message.getTransform().getTranslation().getY();
                        quadz = message.getTransform().getTranslation().getZ();

                        //Log.i("QuadPos", Double.toString(quadx) + "\t\t" + Double.toString(quady) + "\t\t" + Double.toString(quadz));

                    }
                });

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

    double getQuadPosX() { return quadx; }

    double getQuadPosY() { return quady; }

    double getQuadPosZ() { return quadz; }

}
