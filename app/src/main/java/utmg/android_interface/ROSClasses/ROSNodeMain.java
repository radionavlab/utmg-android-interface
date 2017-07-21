package utmg.android_interface.ROSClasses;

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
import std_msgs.Header;
import utmg.android_interface.DataShare;
import utmg.android_interface.Activities.MainActivity;
import utmg.android_interface.QuadUtils.Obstacle;
import utmg.android_interface.QuadUtils.Point3;
import utmg.android_interface.QuadUtils.Quad;

public class ROSNodeMain extends AbstractNodeMain implements NodeMain {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private ArrayList<Quad> quads;
    private ArrayList<Obstacle> obstacles;
    private String teamName;

    private static final String TAG = ROSNodeMain.class.getSimpleName();


    public ROSNodeMain(String teamName){
        super();
        quads=new ArrayList<>();
        obstacles=new ArrayList<>();
    }
    public ROSNodeMain(String teamName,ArrayList<Quad> quads,ArrayList<Obstacle> obstacles){
        this.quads=quads;
        this.obstacles=obstacles;
    }


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
        final Publisher<geometry_msgs.PoseArray> publisherTrajectory = connectedNode.newPublisher(GraphName.of("PosControl/Trajectory/Quad1"), PoseArray._TYPE);

        final Publisher<geometry_msgs.PoseArray> publisherWaypoints = connectedNode.newPublisher(GraphName.of("PosControl/Waypoints/Quad1"), PoseArray._TYPE);

        final Publisher<geometry_msgs.PoseArray> publisherObstacles = connectedNode.newPublisher(GraphName.of("PosControl/Obstacles"), PoseArray._TYPE);

        final Publisher<nav_msgs.Path> publisherPath = connectedNode.newPublisher(GraphName.of("PosControl/Path/Quad1"), Path._TYPE);

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

            for (Quad q : quads) {//TODO: What happens if I add or edit a quad while this is looping?
                if (q.getPublish()) {
                    // trajectory publisher
                    final geometry_msgs.PoseArray poseArray = publisherTrajectory.newMessage();
                    geometry_msgs.PoseArray waypointArray = publisherWaypoints.newMessage();

                    nav_msgs.Path path = publisherPath.newMessage();

                    // configure for trajectory or waypoint mode
                    if (pref.getInt("mode", 0) == 0) {
//                            Header defaultHeader = new Header(){//not really possible because implementing a rawmessage is a bitch
//                                int seq;
//                                String frameId;
//                                Time stamp;
//                                public int getSeq(){return seq;}
//                                public String getFrameId(){return frameId;}
//                                public Time getStamp(){return stamp;}
//                                public void setSeq(int seq){this.seq=seq;}
//                                public void setFrameId(String frameId){this.frameId=frameId;}
//                                public void setStamp(Time stamp){this.stamp=stamp;}
//                                public RawMessage toRawMessage(){
//                                    return poseArray.getHeader().toRawMessage();
//                                }
//                            };
                        Header poseHeader = poseArray.getHeader();
                        poseHeader.setFrameId("world");
                        poseHeader.setSeq(q.getSeq());
                        poseHeader.setStamp(new Time());

                        Header pathHeader = path.getHeader();
                        pathHeader.setFrameId("world");
                        pathHeader.setSeq(q.getSeq());
                        pathHeader.setStamp(new Time());

                    } else if (pref.getInt("mode", 0) == 1) {
                        Header waypointHeader = waypointArray.getHeader();
                        waypointHeader.setFrameId("world");
                        waypointHeader.setSeq(q.getSeq());
                        waypointHeader.setStamp(new Time());
                    }


                    ArrayList<Pose> poses = new ArrayList<>();

                    ArrayList<PoseStamped> posesStamped = new ArrayList<>();

                    // package each trajectory/waypoint into ROS message and send for quad1 ////////
                    for (Point3 p : q.getTrajectory().getPoints()) {
                        geometry_msgs.Point point = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                        point.setX(p.getX());
                        point.setY(p.getY());
                        point.setZ(p.getZ());

                        geometry_msgs.Pose pose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                        pose.setPosition(point);
                        poses.add(pose);

                        if (pref.getInt("mode", 0) == 0) {
                            geometry_msgs.PoseStamped poseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
                            poseStamped.getHeader().setStamp(p.getTime());
                            poseStamped.setPose(pose);
                            posesStamped.add(poseStamped);
                        }
                    }

                    if (pref.getInt("mode", 0) == 0) {//TODO: I don't quite understand what's going on here with the modes
                        poseArray.setPoses(poses);
                        path.setPoses(posesStamped);
                        publisherTrajectory.publish(poseArray);
                        publisherPath.publish(path);

                    } else if (pref.getInt("mode", 0) == 1) {
                        waypointArray.setPoses(poses);
                        publisherWaypoints.publish(waypointArray);
                    }
                    q.incSeq();
                    q.setPublish(false);
                }
                //update the quad
                Subscriber<TransformStamped> subscriberQuad1 = connectedNode.newSubscriber("vicon/"+teamName+"/"+q.getName(), geometry_msgs.TransformStamped._TYPE);
                subscriberQuad1.addMessageListener(new MessageListener<geometry_msgs.TransformStamped>() {
                    @Override
                    public void onNewMessage(geometry_msgs.TransformStamped message) {
                        quad1.setX(message.getTransform().getTranslation().getX());
                        quad1.setY(message.getTransform().getTranslation().getY());
                        quad1.setZ(message.getTransform().getTranslation().getZ());

                    }
                });
            }

            if (pref.getBoolean("obstaclePublish", false)) {
                geometry_msgs.PoseArray mPoseArrayObstacles = publisherObstacles.newMessage();
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
                for(Obstacle obs: obstacles) {
                    geometry_msgs.Pose obsPose = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
                    geometry_msgs.Point mPoint1 = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                    mPoint1.setX(obstacle1.getX());
                    mPoint1.setY(obstacle1.getY());
                    mPoint1.setZ(obstacle1.getZ());
                    mPose1.setPosition(mPoint1);
                    posesObstacles.add(mPose1);
                }
                // add and publish
                mPoseArrayObstacles.setPoses(posesObstacles);
                publisherObstacles1.publish(mPoseArrayObstacles);
            }
            ////////////////////////////////////////////////////////////////////////////////


            // listeners ///////////////////////////////////////////////////////////////////



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

            // go to sleep for one second TODO for live mode reduce this time! lol
            Thread.sleep(1000);
        }};
        connectedNode.executeCancellableLoop(loop);
    }

    // MainActivity FAB sends vector of coordinates to here
    void sendQuad(String name) {
        for(Quad q: quads){
            if(q.getName().equals(name)){
                q.setPublish(true);
                Log.i("Trajectory","Quad "+q.name+" set to publish");
            }
        }

    }

    void setTraj2(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z, ArrayList<Time> t) {
        xes2 = x;
        yes2 = y;
        zes2 = z;
        tes2 = t;
        publishToggle2 = true;
        Log.i("Traj2","Arrays transferred to nodeMain");
    }

    void setTraj3(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z, ArrayList<Time> t) {
        xes3 = x;
        yes3 = y;
        zes3 = z;
        tes3 = t;
        publishToggle3 = true;
        Log.i("Traj3","Arrays transferred to nodeMain");
    }
}
