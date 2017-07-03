package utmg.android_interface;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.message.MessageListener;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.util.ArrayList;

import app_pathplanner_interface.PathPlanner;
import app_pathplanner_interface.PathPlannerRequest;
import app_pathplanner_interface.PathPlannerResponse;
import geometry_msgs.Pose;
import geometry_msgs.PoseArray;
import geometry_msgs.PoseStamped;
import geometry_msgs.TransformStamped;
import nav_msgs.Path;

public class ROSNodeService extends AbstractNodeMain implements NodeMain {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private ArrayList<Float> xes1;
    private ArrayList<Float> yes1;
    private ArrayList<Float> zes1;

    private ArrayList<Time> tes1;

    private boolean serviceToggle1 = false;

    private int seq1 = 0;

    private static final String TAG = ROSNodeService.class.getSimpleName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("utmg_android_service");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        Context appCon = MainActivity.getContextOfApplication();

        pref = appCon.getSharedPreferences("Pref", 0);
        prefEditor = pref.edit();

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

                if(serviceToggle1 && pref.getBoolean("servicePublish",false)) {

                    // trajectory publisher
                    PoseArray mPoseArray1 = connectedNode.getTopicMessageFactory().newFromType(PoseArray._TYPE);
                    Path mPath1 = connectedNode.getTopicMessageFactory().newFromType(Path._TYPE);

                    // configure for trajectory or waypoint mode
                    if (pref.getInt("mode", 0) == 0) {
                        mPoseArray1.getHeader().setFrameId("world");
                        mPoseArray1.getHeader().setSeq(seq1);
                        mPoseArray1.getHeader().setStamp(new Time());

                        mPath1.getHeader().setFrameId("world");
                        mPath1.getHeader().setSeq(seq1);
                        mPath1.getHeader().setStamp(new Time());

                    } else if (pref.getInt("mode", 0) == 1) {

                    }

                    ArrayList<Pose> poses1 = new ArrayList<>();
                    ArrayList<PoseStamped> poseStamped1 = new ArrayList<>();

                    // package each trajectory/waypoint into ROS message and send for quad1 ////////
                    if (xes1 == null || yes1 == null || zes1 == null || tes1 == null) {
                    } else {
                        for (int i = 0; i < xes1.size(); i++) {
                            Pose mPose = connectedNode.getTopicMessageFactory().newFromType(Pose._TYPE);
                            geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                            mPoint.setX(xes1.get(i));
                            mPoint.setY(yes1.get(i));
                            mPoint.setZ(zes1.get(i));
                            mPose.setPosition(mPoint);
                            poses1.add(mPose);

                            if (pref.getInt("mode", 0) == 0) {
                                PoseStamped mPoseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
                                mPoseStamped.getHeader().setStamp(tes1.get(i));
                                mPoseStamped.setPose(mPose);
                                poseStamped1.add(mPoseStamped);
                            }
                        }

                        if (pref.getInt("mode", 0) == 0) {
                            mPoseArray1.setPoses(poses1);
                            mPath1.setPoses(poseStamped1);

                            // service client definition
                            final ServiceClient<PathPlannerRequest, PathPlannerResponse> serviceClient;
                            try {
                                serviceClient = connectedNode.newServiceClient("app_pathplanner", PathPlanner._TYPE);
                            } catch (ServiceNotFoundException e) {
                                throw new RosRuntimeException(e);
                            }

                            final PathPlannerRequest request = serviceClient.newMessage();
                            request.setInput(mPoseArray1);
                            serviceClient.call(request, new ServiceResponseListener<PathPlannerResponse>() {
                                @Override
                                public void onSuccess(PathPlannerResponse response) {
                                    DataShare.setServicedPath(1, response.getOutput());
                                    Log.i("ROSNodeService", "Received serviced Path.");
                                    //connectedNode.getLog().info(
                                    //        String.format("%d + %d = %d", request.getA(), request.getB(), response.getSum()));
                                }

                                @Override
                                public void onFailure(RemoteException e) {
                                    throw new RosRuntimeException(e);
                                }
                            });


                        } else if (pref.getInt("mode", 0) == 1) {

                        }
                    }

                    // go to sleep for one second TODO for live mode reduce this time!
                    Thread.sleep(1000);

                }
                serviceToggle1 = false;
            }
        };
        connectedNode.executeCancellableLoop(loop);

    }

    // MainActivity Preview sends vector of coordinates to here
    void setTraj1(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z, ArrayList<Time> t) {
        xes1 = x;
        yes1 = y;
        zes1 = z;
        tes1 = t;
        serviceToggle1 = true;
        Log.i("Traj1","Arrays transferred to nodeMain");
    }
}