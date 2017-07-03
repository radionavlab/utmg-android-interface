package utmg.android_interface;

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
import geometry_msgs.PoseStamped;
import geometry_msgs.TransformStamped;
import nav_msgs.Path;

public class ROSNodeMain extends AbstractNodeMain implements NodeMain {

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

    private ArrayList<Time> tes1;
    private ArrayList<Time> tes2;
    private ArrayList<Time> tes3;

    private boolean publishToggle1 = false;
    private boolean publishToggle2 = false;
    private boolean publishToggle3 = false;

    private int seq1 = 0;
    private int seq2 = 0;
    private int seq3 = 0;

    private static final String TAG = ROSNodeMain.class.getSimpleName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("utmg_android");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        Context appCon = MainActivity.getContextOfApplication();

        pref = appCon.getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

        // publisher definitions
        final Publisher<geometry_msgs.PoseArray> publisherTrajectory1 = connectedNode.newPublisher(GraphName.of("PosControl/Trajectory/Quad1"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherTrajectory2 = connectedNode.newPublisher(GraphName.of("PosControl/Trajectory/Quad2"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherTrajectory3 = connectedNode.newPublisher(GraphName.of("PosControl/Trajectory/Quad3"), PoseArray._TYPE);

        final Publisher<geometry_msgs.PoseArray> publisherWaypoints1 = connectedNode.newPublisher(GraphName.of("PosControl/Waypoints/Quad1"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherWaypoints2 = connectedNode.newPublisher(GraphName.of("PosControl/Waypoints/Quad2"), PoseArray._TYPE);
        final Publisher<geometry_msgs.PoseArray> publisherWaypoints3 = connectedNode.newPublisher(GraphName.of("PosControl/Waypoints/Quad3"), PoseArray._TYPE);

        final Publisher<geometry_msgs.PoseArray> publisherObstacles1 = connectedNode.newPublisher(GraphName.of("PosControl/Obstacles"), PoseArray._TYPE);

        final Publisher<nav_msgs.Path> publisherPath1 = connectedNode.newPublisher(GraphName.of("PosControl/Path/Quad1"), Path._TYPE);
        final Publisher<nav_msgs.Path> publisherPath2 = connectedNode.newPublisher(GraphName.of("PosControl/Path/Quad2"), Path._TYPE);
        final Publisher<nav_msgs.Path> publisherPath3 = connectedNode.newPublisher(GraphName.of("PosControl/Path/Quad3"), Path._TYPE);

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

                if (publishToggle1 || publishToggle2 || publishToggle3) {

                    // trajectory publisher
                    geometry_msgs.PoseArray mPoseArray1 = publisherTrajectory1.newMessage();
                    geometry_msgs.PoseArray mWaypointArray1 = publisherWaypoints1.newMessage();
                    geometry_msgs.PoseArray mPoseArray2 = publisherTrajectory2.newMessage();
                    geometry_msgs.PoseArray mWaypointArray2 = publisherWaypoints2.newMessage();
                    geometry_msgs.PoseArray mPoseArray3 = publisherTrajectory3.newMessage();
                    geometry_msgs.PoseArray mWaypointArray3 = publisherWaypoints3.newMessage();

                    nav_msgs.Path mPath1 = publisherPath1.newMessage();
                    nav_msgs.Path mPath2 = publisherPath2.newMessage();
                    nav_msgs.Path mPath3 = publisherPath3.newMessage();

                    // configure for trajectory or waypoint mode
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


                        mPath1.getHeader().setFrameId("world");
                        mPath1.getHeader().setSeq(seq1);
                        mPath1.getHeader().setStamp(new Time());

                        mPath2.getHeader().setFrameId("world");
                        mPath2.getHeader().setSeq(seq2);
                        mPath2.getHeader().setStamp(new Time());

                        mPath3.getHeader().setFrameId("world");
                        mPath3.getHeader().setSeq(seq3);
                        mPath3.getHeader().setStamp(new Time());

                    } else if (pref.getInt("mode", 0) == 1) {
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


                    ArrayList<Pose> poses1 = new ArrayList<>();
                    ArrayList<Pose> poses2 = new ArrayList<>();
                    ArrayList<Pose> poses3 = new ArrayList<>();

                    ArrayList<PoseStamped> poseStamped1 = new ArrayList<>();
                    ArrayList<PoseStamped> poseStamped2 = new ArrayList<>();
                    ArrayList<PoseStamped> poseStamped3 = new ArrayList<>();


                    // package each trajectory/waypoint into ROS message and send for quad1 ////////
                    if (xes1 == null || yes1 == null || zes1 == null || tes1 == null) {
                    } else {
                        for (int i = 0; i < xes1.size(); i++) {
                            geometry_msgs.Pose mPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                            geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                            mPoint.setX(xes1.get(i));
                            mPoint.setY(yes1.get(i));
                            mPoint.setZ(zes1.get(i));
                            mPose.setPosition(mPoint);
                            poses1.add(mPose);

                            if (pref.getInt("mode", 0) == 0) {
                                geometry_msgs.PoseStamped mPoseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
                                mPoseStamped.getHeader().setStamp(tes1.get(i));
                                mPoseStamped.setPose(mPose);
                                poseStamped1.add(mPoseStamped);
                            }
                        }

                        if (pref.getInt("mode", 0) == 0) {
                            mPoseArray1.setPoses(poses1);
                            mPath1.setPoses(poseStamped1);

                            if (publishToggle1) {
                                publisherTrajectory1.publish(mPoseArray1);
                                publisherPath1.publish(mPath1);

                                seq1 = seq1 + 1;
                            }
                            publishToggle1 = false;

                        } else if (pref.getInt("mode", 0) == 1) {
                            mWaypointArray1.setPoses(poses1);

                            if (publishToggle1) {
                                publisherWaypoints1.publish(mWaypointArray1);

                                seq1 = seq1 + 1;
                            }

                            publishToggle1 = false;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////


                    // package each trajectory/waypoint into ROS message and send for quad2 ////////
                    if (xes2 == null || yes2 == null || zes2 == null || tes2 == null) {
                    } else {
                        for (int i = 0; i < xes2.size(); i++) {
                            geometry_msgs.Pose mPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                            geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                            mPoint.setX(xes2.get(i));
                            mPoint.setY(yes2.get(i));
                            mPoint.setZ(zes2.get(i));
                            mPose.setPosition(mPoint);
                            poses2.add(mPose);

                            if (pref.getInt("mode", 0) == 0) {
                                geometry_msgs.PoseStamped mPoseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
                                mPoseStamped.getHeader().setStamp(tes2.get(i));
                                mPoseStamped.setPose(mPose);
                                poseStamped2.add(mPoseStamped);
                            }
                        }

                        if (pref.getInt("mode", 0) == 0) {
                            mPoseArray2.setPoses(poses2);
                            mPath2.setPoses(poseStamped2);

                            if (publishToggle2) {
                                publisherTrajectory2.publish(mPoseArray2);
                                publisherPath2.publish(mPath2);

                                seq2 = seq2 + 1;
                            }

                            publishToggle2 = false;
                        } else if (pref.getInt("mode", 0) == 1) {
                            mWaypointArray2.setPoses(poses2);

                            if (publishToggle2) {
                                publisherWaypoints2.publish(mWaypointArray2);

                                seq2 = seq2 + 1;
                            }

                            publishToggle2 = false;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////


                    // package each trajectory/waypoint into ROS message and send for quad3 ////////
                    if (xes3 == null || yes3 == null || zes3 == null || tes3 == null) {
                    } else {
                        for (int i = 0; i < xes3.size(); i++) {
                            geometry_msgs.Pose mPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                            geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                            mPoint.setX(xes3.get(i));
                            mPoint.setY(yes3.get(i));
                            mPoint.setZ(zes3.get(i));
                            mPose.setPosition(mPoint);
                            poses3.add(mPose);

                            if (pref.getInt("mode", 0) == 0) {
                                geometry_msgs.PoseStamped mPoseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
                                mPoseStamped.getHeader().setStamp(tes3.get(i));
                                mPoseStamped.setPose(mPose);
                                poseStamped3.add(mPoseStamped);
                            }
                        }

                        if (pref.getInt("mode", 0) == 0) {
                            mPoseArray3.setPoses(poses3);
                            mPath3.setPoses(poseStamped3);

                            if (publishToggle3) {
                                publisherTrajectory3.publish(mPoseArray3);

                                seq3 = seq3 + 1;
                            }

                            publishToggle3 = false;
                        } else if (pref.getInt("mode", 0) == 1) {
                            mWaypointArray3.setPoses(poses3);

                            if (publishToggle3) {
                                publisherWaypoints3.publish(mWaypointArray3);

                                seq3 = seq3 + 1;
                            }

                            publishToggle3 = false;
                        }
                    }
                    ////////////////////////////////////////////////////////////////////////////////


                    // obstacle publisher //////////////////////////////////////////////////////////
                    if (pref.getBoolean("obstaclePublish", false)) {
                        geometry_msgs.PoseArray mPoseArrayObstacles = publisherObstacles1.newMessage();
                        mPoseArrayObstacles.getHeader().setFrameId("world");
                        mPoseArrayObstacles.getHeader().setSeq(seq1);
                        mPoseArrayObstacles.getHeader().setStamp(new Time());
                        ArrayList<Pose> posesObstacles = new ArrayList<>();

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
                    ////////////////////////////////////////////////////////////////////////////////


                    // listeners ///////////////////////////////////////////////////////////////////
                    Subscriber<TransformStamped> subscriberQuad1 = connectedNode.newSubscriber("vicon/Dragonfly/Dragonfly", geometry_msgs.TransformStamped._TYPE);
                    subscriberQuad1.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                        @Override
                        public void onNewMessage(geometry_msgs.TransformStamped message) {
                            quad1.setX(message.getTransform().getTranslation().getX());
                            quad1.setY(message.getTransform().getTranslation().getY());
                            quad1.setZ(message.getTransform().getTranslation().getZ());

                        }
                    });

                    Subscriber<TransformStamped> subscriberQuad2 = connectedNode.newSubscriber("vicon/crazyflie1/crazyflie1", geometry_msgs.TransformStamped._TYPE);
                    subscriberQuad2.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                        @Override
                        public void onNewMessage(geometry_msgs.TransformStamped message) {
                            quad2.setX(message.getTransform().getTranslation().getX());
                            quad2.setY(message.getTransform().getTranslation().getY());
                            quad2.setZ(message.getTransform().getTranslation().getZ());

                        }
                    });

                    Subscriber<TransformStamped> subscriberQuad3 = connectedNode.newSubscriber("vicon/crazyflie2/crazyflie2", geometry_msgs.TransformStamped._TYPE);
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
                    ////////////////////////////////////////////////////////////////////////////////

                    // go to sleep for one second TODO for live mode reduce this time!
                    Thread.sleep(1000);
                }
            }
        };
        connectedNode.executeCancellableLoop(loop);

    }

    // MainActivity FAB sends vector of coordinates to here
    void setTraj1(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z, ArrayList<Time> t) {
        xes1 = x;
        yes1 = y;
        zes1 = z;
        tes1 = t;
        publishToggle1 = true;
        Log.i("Traj1","Arrays transferred to node");
    }

    void setTraj2(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z, ArrayList<Time> t) {
        xes2 = x;
        yes2 = y;
        zes2 = z;
        tes2 = t;
        publishToggle2 = true;
        Log.i("Traj2","Arrays transferred to node");
    }

    void setTraj3(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z, ArrayList<Time> t) {
        xes3 = x;
        yes3 = y;
        zes3 = z;
        tes3 = t;
        publishToggle3 = true;
        Log.i("Traj3","Arrays transferred to node");
    }
}
