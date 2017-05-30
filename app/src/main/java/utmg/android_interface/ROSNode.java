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

import geometry_msgs.TransformStamped;

public class ROSNode extends AbstractNodeMain implements NodeMain {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private ArrayList<Float> xes1;
    private ArrayList<Float> yes1;
    private ArrayList<Float> zes1;

    private ArrayList<Float> xes2;
    private ArrayList<Float> yes2;
    private ArrayList<Float> zes2;

    private ArrayList<Float> xes3;
    private ArrayList<Float> yes3;
    private ArrayList<Float> zes3;

    private boolean publishToggle1 = false;
    private boolean publishToggle2 = false;
    private boolean publishToggle3 = false;

    private int seq1 = 0;
    private int seq2 = 0;
    private int seq3 = 0;

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

        final Publisher<geometry_msgs.PoseArray> publisherTrajectory1 = connectedNode.newPublisher(GraphName.of("PosControl/Trajectory"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherObstacles1 = connectedNode.newPublisher(GraphName.of("PosControl/Obstacles"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherWaypoints1 = connectedNode.newPublisher(GraphName.of("PosControl/Waypoints"), PoseArray._TYPE);

        final Publisher<geometry_msgs.PoseArray> publisherTrajectory2 = connectedNode.newPublisher(GraphName.of("PosControl/Trajectory"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherObstacles2 = connectedNode.newPublisher(GraphName.of("PosControl/Obstacles"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherWaypoints2 = connectedNode.newPublisher(GraphName.of("PosControl/Waypoints"), PoseArray._TYPE);

        final Publisher<geometry_msgs.PoseArray> publisherTrajectory3 = connectedNode.newPublisher(GraphName.of("PosControl/Trajectory"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherObstacles3 = connectedNode.newPublisher(GraphName.of("PosControl/Obstacles"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherWaypoints3 = connectedNode.newPublisher(GraphName.of("PosControl/Waypoints"), PoseArray._TYPE);
        //final Publisher<nav_msgs.Path> publisherPath = connectedNode.newPublisher(GraphName.of("android_quad_trajectory"), Path._TYPE);

        // local instantiation of objects
        final Thing quad1 = DataShare.getInstance("quad1");
        final Thing quad2 = DataShare.getInstance("quad2");
        final Thing quad3 = DataShare.getInstance("quad3");
        final Thing sword = DataShare.getInstance("sword");
        final Thing obstacle1 = DataShare.getInstance("obstacle1");
        final Thing obstacle2 = DataShare.getInstance("obstacle2");

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
                geometry_msgs.PoseArray mPoseArray1 = publisherTrajectory1.newMessage();
                geometry_msgs.PoseArray mWaypointArray1 = publisherWaypoints1.newMessage();
                geometry_msgs.PoseArray mPoseArray2 = publisherTrajectory2.newMessage();
                geometry_msgs.PoseArray mWaypointArray2 = publisherWaypoints2.newMessage();
                geometry_msgs.PoseArray mPoseArray3 = publisherTrajectory3.newMessage();
                geometry_msgs.PoseArray mWaypointArray3 = publisherWaypoints3.newMessage();

                //nav_msgs.Path xyPath = publisherPath.newMessage();

                if (pref.getInt("mode", 0) == 0) {
                    mPoseArray1.getHeader().setFrameId("world");
                    mPoseArray1.getHeader().setSeq(seq1);
                    mPoseArray1.getHeader().setStamp(new Time());

                    mPoseArray2.getHeader().setFrameId("world");
                    mPoseArray2.getHeader().setSeq(seq1);
                    mPoseArray2.getHeader().setStamp(new Time());

                    mPoseArray3.getHeader().setFrameId("world");
                    mPoseArray3.getHeader().setSeq(seq1);
                    mPoseArray3.getHeader().setStamp(new Time());
                }
                else if (pref.getInt("mode", 0) == 1) {
                    mWaypointArray1.getHeader().setFrameId("world");
                    mWaypointArray1.getHeader().setSeq(seq1);
                    mWaypointArray1.getHeader().setStamp(new Time());

                    mWaypointArray2.getHeader().setFrameId("world");
                    mWaypointArray2.getHeader().setSeq(seq1);
                    mWaypointArray2.getHeader().setStamp(new Time());

                    mWaypointArray3.getHeader().setFrameId("world");
                    mWaypointArray3.getHeader().setSeq(seq1);
                    mWaypointArray3.getHeader().setStamp(new Time());
                }

//                xyPath.getHeader().setFrameId("world");
//                xyPath.getHeader().setSeq(0);
//                xyPath.getHeader().setStamp(new Time());

                ArrayList<Pose> poses1 = new ArrayList<>();
                ArrayList<Pose> poses2 = new ArrayList<>();
                ArrayList<Pose> poses3 = new ArrayList<>();


                // package each trajectory/waypoint into ROS message and send for quad1
                if (xes1 == null || yes1 == null || zes1 == null) { } else {
                    for (int i = 0; i < xes1.size(); i++) {
                        geometry_msgs.Pose mPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);

                        mPoint.setX(xes1.get(i));
                        mPoint.setY(yes1.get(i));
                        mPoint.setZ(zes1.get(i));

                        mPose.setPosition(mPoint);

                        poses1.add(mPose);
                    }

                    if (pref.getInt("mode", 0) == 0) {
                        mPoseArray1.setPoses(poses1);

                        if (publishToggle1 == true) {
                            publisherTrajectory1.publish(mPoseArray1);

                            seq1 = seq1 + 1;
                        }

                        publishToggle1 = false;
                    }
                    else if (pref.getInt("mode", 0) == 1) {
                        mWaypointArray1.setPoses(poses1);

                        if (publishToggle1 == true) {
                            publisherWaypoints1.publish(mWaypointArray1);

                            seq1 = seq1 + 1;
                        }

                        publishToggle1 = false;
                    }
                }



                // package each trajectory/waypoint into ROS message and send for quad2
                if (xes2 == null || yes2 == null || zes2 == null) { } else {
                    for (int i = 0; i < xes2.size(); i++) {
                        geometry_msgs.Pose mPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);

                        mPoint.setX(xes2.get(i));
                        mPoint.setY(yes2.get(i));
                        mPoint.setZ(zes2.get(i));

                        mPose.setPosition(mPoint);

                        poses2.add(mPose);
                    }

                    if (pref.getInt("mode", 0) == 0) {
                        mPoseArray2.setPoses(poses2);

                        if (publishToggle2 == true) {
                            publisherTrajectory2.publish(mPoseArray2);

                            seq2 = seq2 + 1;
                        }

                        publishToggle2 = false;
                    }
                    else if (pref.getInt("mode", 0) == 1) {
                        mWaypointArray2.setPoses(poses2);

                        if (publishToggle2 == true) {
                            publisherWaypoints2.publish(mWaypointArray2);

                            seq2 = seq2 + 1;
                        }

                        publishToggle2 = false;
                    }
                }




                // package each trajectory/waypoint into ROS message and send for quad3
                if (xes3 == null || yes3 == null || zes3 == null) { } else {
                    for (int i = 0; i < xes3.size(); i++) {
                        geometry_msgs.Pose mPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);

                        mPoint.setX(xes3.get(i));
                        mPoint.setY(yes3.get(i));
                        mPoint.setZ(zes3.get(i));

                        mPose.setPosition(mPoint);

                        poses3.add(mPose);
                    }

                    if (pref.getInt("mode", 0) == 0) {
                        mPoseArray3.setPoses(poses3);

                        if (publishToggle3 == true) {
                            publisherTrajectory3.publish(mPoseArray3);

                            seq3 = seq3 + 1;
                        }

                        publishToggle3 = false;
                    }
                    else if (pref.getInt("mode", 0) == 1) {
                        mWaypointArray3.setPoses(poses3);

                        if (publishToggle3 == true) {
                            publisherWaypoints3.publish(mWaypointArray3);

                            seq3 = seq3 + 1;
                        }

                        publishToggle3 = false;
                    }
                }





                // obstacle publisher //////////////////////////////////////////////////////////////
                if (pref.getBoolean("obstaclePublish", false) == true) {
                    geometry_msgs.PoseArray mPoseArrayObstacles = publisherObstacles1.newMessage();
                    mPoseArrayObstacles.getHeader().setFrameId("world");
                    mPoseArrayObstacles.getHeader().setSeq(seq1);
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
                    publisherObstacles1.publish(mPoseArrayObstacles);
                }
                ////////////////////////////////////////////////////////////////////////////////////







                // listener
                Subscriber<TransformStamped> subscriberQuad1 = connectedNode.newSubscriber("vicon/Dragonfly/Dragonfly", geometry_msgs.TransformStamped._TYPE);
                subscriberQuad1.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        quad1.setX(message.getTransform().getTranslation().getX());
                        quad1.setY(message.getTransform().getTranslation().getY());
                        quad1.setZ(message.getTransform().getTranslation().getZ());

                    }
                });

                // TODO CHANGE ROSTOPIC
                Subscriber<TransformStamped> subscriberQuad2 = connectedNode.newSubscriber("vicon/Dragonfly/Dragonfly", geometry_msgs.TransformStamped._TYPE);
                subscriberQuad2.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        quad2.setX(message.getTransform().getTranslation().getX());
                        quad2.setY(message.getTransform().getTranslation().getY());
                        quad2.setZ(message.getTransform().getTranslation().getZ());

                    }
                });

                // TODO CHANGE ROSTOPIC
                Subscriber<TransformStamped> subscriberQuad3 = connectedNode.newSubscriber("vicon/Dragonfly/Dragonfly", geometry_msgs.TransformStamped._TYPE);
                subscriberQuad3.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        quad3.setX(message.getTransform().getTranslation().getX());
                        quad3.setY(message.getTransform().getTranslation().getY());
                        quad3.setZ(message.getTransform().getTranslation().getZ());

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

                // go to sleep for one second TODO for live mode reduce this time!
                Thread.sleep(1000);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

    // MainActivity FAB sends vector of coordinates to here
    void setTraj1(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
        xes1 = x;
        yes1 = y;
        zes1 = z;

        publishToggle1 = true;

        Log.i("Traj1","Arrays transferred to node");
    }

    void setTraj2(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
        xes2 = x;
        yes2 = y;
        zes2 = z;

        publishToggle2 = true;

        Log.i("Traj2","Arrays transferred to node");
    }

    void setTraj3(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z) {
        xes3 = x;
        yes3 = y;
        zes3 = z;

        publishToggle3 = true;

        Log.i("Traj3","Arrays transferred to node");
    }

}
