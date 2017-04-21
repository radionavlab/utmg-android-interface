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
import nav_msgs.Path;

import geometry_msgs.TransformStamped;

public class ROSNode extends AbstractNodeMain implements NodeMain {

    private ArrayList<Float> xes;
    private ArrayList<Float> yes;

    private double quadx = 0;
    private double quady = 0;
    private double quadz = 0;

    private double swordx = 0;
    private double swordy = 0;
    private double swordz = 0;

    private boolean publishToggle = false;

    private int seq = 0;

    private static final String TAG = ROSNode.class.getSimpleName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("utmg_android");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        //final Publisher<std_msgs.String> publisher = connectedNode.newPublisher(GraphName.of("time"), std_msgs.String._TYPE);

        final Publisher<geometry_msgs.PoseArray> publisher1 = connectedNode.newPublisher(GraphName.of("android_quad_trajectory"), PoseArray._TYPE);
        //final Publisher<nav_msgs.Path> publisherPath = connectedNode.newPublisher(GraphName.of("android_quad_trajectory"), Path._TYPE);

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
                geometry_msgs.PoseArray mPoseArray = publisher1.newMessage();
                //nav_msgs.Path xyPath = publisherPath.newMessage();

                mPoseArray.getHeader().setFrameId("world");
                mPoseArray.getHeader().setSeq(seq);
                mPoseArray.getHeader().setStamp(new Time());

//                xyPath.getHeader().setFrameId("world");
//                xyPath.getHeader().setSeq(0);
//                xyPath.getHeader().setStamp(new Time());

                ArrayList<Pose> poses = new ArrayList<>();

                if (xes == null || yes == null) {
                    //Log.i("Traj","Null arrays");
                } else {
                    for (int i = 0; i < xes.size(); i++) {
                        geometry_msgs.Pose mPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);

                        mPoint.setX(xes.get(i));
                        mPoint.setY(yes.get(i));
                        mPoint.setZ(1);

                        mPose.setPosition(mPoint);

                        poses.add(mPose);
                    }

                    mPoseArray.setPoses(poses);

                    if (publishToggle == true) {
                        publisher1.publish(mPoseArray);

                        seq = seq + 1;
                    }

                    publishToggle = false;

                }


                // listener

                //final Log log = connectedNode.getLog();
                Subscriber<TransformStamped> subscriberQuad = connectedNode.newSubscriber("vicon/Quad7/Quad7", geometry_msgs.TransformStamped._TYPE);

                // rotated vicon -90 degrees
                subscriberQuad.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        quadx = message.getTransform().getTranslation().getX();
                        quady = message.getTransform().getTranslation().getY();
                        quadz = message.getTransform().getTranslation().getZ();

                    }
                });


                Subscriber<TransformStamped> subscriberSword = connectedNode.newSubscriber("vicon/sword/sword", geometry_msgs.TransformStamped._TYPE);

                subscriberSword.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        swordx = message.getTransform().getTranslation().getX();
                        swordy = message.getTransform().getTranslation().getY();
                        swordz = message.getTransform().getTranslation().getZ();

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

        publishToggle = true;

        Log.i("Traj","Arrays transferred to node");
    }

    double getQuadPosX() { return quadx; }

    double getQuadPosY() { return quady; }

    double getQuadPosZ() { return quadz; }


    double getSwordPosX() { return swordx; }

    double getSwordPosY() { return swordy; }

    double getSwordPosZ() { return swordz; }

}
