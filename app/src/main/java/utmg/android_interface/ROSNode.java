package utmg.android_interface;

//import org.apache.commons.logging.Log;
import android.content.SharedPreferences;
import android.content.Context;
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

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private ArrayList<Float> xes;
    private ArrayList<Float> yes;
    private ArrayList<Float> zes;

    private boolean publishToggle = false;

    private int seq = 0;

    private static final String TAG = ROSNode.class.getSimpleName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("utmg_android");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        Context appCon = MainActivity.getContextOfApplication();

        pref = appCon.getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        //final Publisher<std_msgs.String> publisher = connectedNode.newPublisher(GraphName.of("time"), std_msgs.String._TYPE);

        final Publisher<geometry_msgs.PoseArray> publisher1 = connectedNode.newPublisher(GraphName.of("PosControl/Trajectory"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisher2 = connectedNode.newPublisher(GraphName.of("PosControl/Obstacles"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisher3 = connectedNode.newPublisher(GraphName.of("PosControl/Waypoints"), PoseArray._TYPE);
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
                geometry_msgs.PoseArray mWaypointArray = publisher3.newMessage();

                //nav_msgs.Path xyPath = publisherPath.newMessage();

                if (pref.getInt("mode", 0) == 0) {
                    mPoseArray.getHeader().setFrameId("world");
                    mPoseArray.getHeader().setSeq(seq);
                    mPoseArray.getHeader().setStamp(new Time());
                }
                else if (pref.getInt("mode", 0) == 1) {
                    mWaypointArray.getHeader().setFrameId("world");
                    mWaypointArray.getHeader().setSeq(seq);
                    mWaypointArray.getHeader().setStamp(new Time());
                }

//                xyPath.getHeader().setFrameId("world");
//                xyPath.getHeader().setSeq(0);
//                xyPath.getHeader().setStamp(new Time());

                ArrayList<Pose> poses = new ArrayList<>();

                // local instantiation of objects
                final Thing quad1 = DataShare.getInstance("quad1");
                final Thing sword = DataShare.getInstance("sword");
                final Thing obstacle1 = DataShare.getInstance("obstacle1");
                final Thing obstacle2 = DataShare.getInstance("obstacle2");

                //Log.i("RosNode",obstacle1.getX() + "\t" + obstacle1.getY());

                if (xes == null || yes == null || zes == null) {
                    //Log.i("Traj","Null arrays");
                } else {
                    for (int i = 0; i < xes.size(); i++) {
                        geometry_msgs.Pose mPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);

                        mPoint.setX(xes.get(i));
                        mPoint.setY(yes.get(i));
                        mPoint.setZ(zes.get(i));

                        mPose.setPosition(mPoint);

                        poses.add(mPose);
                    }

                    if (pref.getInt("mode", 0) == 0) {
                        mPoseArray.setPoses(poses);

                        if (publishToggle == true) {
                            publisher1.publish(mPoseArray);

                            seq = seq + 1;
                        }

                        publishToggle = false;
                    }
                    else if (pref.getInt("mode", 0) == 1) {
                        mWaypointArray.setPoses(poses);

                        if (publishToggle == true) {
                            publisher3.publish(mWaypointArray);

                            seq = seq + 1;
                        }

                        publishToggle = false;
                    }




                    if (pref.getBoolean("obstaclePublish", false) == true) {
                        // obstacle publisher //////////////////////////////////////////////////////////////
                        geometry_msgs.PoseArray mPoseArrayObstacles = publisher2.newMessage();
                        mPoseArrayObstacles.getHeader().setFrameId("world");
                        mPoseArrayObstacles.getHeader().setSeq(seq);
                        mPoseArrayObstacles.getHeader().setStamp(new Time());
                        ArrayList<Pose> posesObstacles = new ArrayList<>();
                        // TODO add in loop to cover all obstacles
                        geometry_msgs.Pose mPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                        mPoint.setX(sword.getX());
                        mPoint.setY(sword.getY());
                        mPoint.setZ(sword.getZ());
                        mPose.setPosition(mPoint);
                        posesObstacles.add(mPose);
                        // obstacle 1
                        geometry_msgs.Pose mPose1 = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        geometry_msgs.Point mPoint1 = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                        mPoint1.setX(obstacle1.getX());
                        mPoint1.setY(obstacle1.getY());
                        mPoint1.setZ(obstacle1.getZ());
                        mPose1.setPosition(mPoint1);
                        posesObstacles.add(mPose1);
                        // obstacle 2
                        geometry_msgs.Pose mPose2 = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        geometry_msgs.Point mPoint2 = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                        mPoint2.setX(obstacle2.getX());
                        mPoint2.setY(obstacle2.getY());
                        mPoint2.setZ(obstacle2.getZ());
                        mPose2.setPosition(mPoint2);
                        posesObstacles.add(mPose2);
                        // add and publish
                        mPoseArrayObstacles.setPoses(posesObstacles);
                        publisher2.publish(mPoseArrayObstacles);


                    }

                ////////////////////////////////////////////////////////////////////////////////////

                }



                // listener
                Subscriber<TransformStamped> subscriberQuad = connectedNode.newSubscriber("vicon/Dragonfly/Dragonfly", geometry_msgs.TransformStamped._TYPE);
                subscriberQuad.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        quad1.setX(message.getTransform().getTranslation().getX());
                        quad1.setY(message.getTransform().getTranslation().getY());
                        quad1.setZ(message.getTransform().getTranslation().getZ());

                    }
                });


                Subscriber<TransformStamped> subscriberSword = connectedNode.newSubscriber("vicon/sword/sword", geometry_msgs.TransformStamped._TYPE);
                subscriberSword.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        sword.setX(message.getTransform().getTranslation().getX());
                        sword.setY(message.getTransform().getTranslation().getY());
                        sword.setZ(message.getTransform().getTranslation().getZ());
                    }
                });


                Subscriber<TransformStamped> subscriberObstacle1 = connectedNode.newSubscriber("vicon/Obstacle1/Obstacle1", geometry_msgs.TransformStamped._TYPE);
                subscriberObstacle1.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        obstacle1.setX(message.getTransform().getTranslation().getX());
                        obstacle1.setY(message.getTransform().getTranslation().getY());
                        obstacle1.setZ(message.getTransform().getTranslation().getZ());
                    }
                });


                Subscriber<TransformStamped> subscriberObstacle2 = connectedNode.newSubscriber("vicon/Obstacle2/Obstacle2", geometry_msgs.TransformStamped._TYPE);
                subscriberObstacle2.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        obstacle2.setX(message.getTransform().getTranslation().getX());
                        obstacle2.setY(message.getTransform().getTranslation().getY());
                        obstacle2.setZ(message.getTransform().getTranslation().getZ());
                    }
                });

                // go to sleep for one second
                Thread.sleep(1000);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

    void setTraj(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
        xes = x;
        yes = y;
        zes = z;

        publishToggle = true;

        Log.i("Traj","Arrays transferred to node");
    }

}
