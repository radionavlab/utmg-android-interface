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
import geometry_msgs.Quaternion;
import geometry_msgs.TransformStamped;
import nav_msgs.Path;

public class ROSNodeService extends AbstractNodeMain implements NodeMain {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private ArrayList<Float> xes1;
    private ArrayList<Float> yes1;
    private ArrayList<Float> zes1;
    private ArrayList<Time> tes1;
    private int seq1 = 0;
    private boolean serviceToggle1 = false;

    private ArrayList<Float> xes2;
    private ArrayList<Float> yes2;
    private ArrayList<Float> zes2;
    private ArrayList<Time> tes2;
    private int seq2 = 0;
    private boolean serviceToggle2 = false;

    private ArrayList<Float> xes3;
    private ArrayList<Float> yes3;
    private ArrayList<Float> zes3;
    private ArrayList<Time> tes3;
    private int seq3 = 0;
    private boolean serviceToggle3 = false;

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

                if(serviceToggle1 && pref.getBoolean("serviceToggle",false)) {

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
                            Quaternion quat = connectedNode.getTopicMessageFactory().newFromType(Quaternion._TYPE);
                            quat.setW(tes1.get(i).toSeconds());
                            //Log.i("Times", "" + tes1.get(i).toSeconds());
                            mPose.setOrientation(quat);
                            poses1.add(mPose);

//                            if (pref.getInt("mode", 0) == 0) {
//                                PoseStamped mPoseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
//                                mPoseStamped.getHeader().setStamp(tes1.get(i));
//                                mPoseStamped.setPose(mPose);
//                                poseStamped1.add(mPoseStamped);
//                            }
                        }

                        if (pref.getInt("mode", 0) == 0) {
                            mPoseArray1.setPoses(poses1);
                            //mPath1.setPoses(poseStamped1);

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
                                    Log.i("ROSNodeService", "Received serviced Path. Size: " + Double.toString(response.getOutput().getPoses().size()));
                                    DataShare.setServicedPath(1, response.getOutput());
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
                    Thread.sleep(10);

                }
                serviceToggle1 = false;

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                if(serviceToggle2 && pref.getBoolean("serviceToggle",false)) {

                    // trajectory publisher
                    PoseArray mPoseArray2 = connectedNode.getTopicMessageFactory().newFromType(PoseArray._TYPE);
                    Path mPath2 = connectedNode.getTopicMessageFactory().newFromType(Path._TYPE);

                    // configure for trajectory or waypoint mode
                    if (pref.getInt("mode", 0) == 0) {
                        mPoseArray2.getHeader().setFrameId("world");
                        mPoseArray2.getHeader().setSeq(seq2);
                        mPoseArray2.getHeader().setStamp(new Time());

                        mPath2.getHeader().setFrameId("world");
                        mPath2.getHeader().setSeq(seq2);
                        mPath2.getHeader().setStamp(new Time());

                    } else if (pref.getInt("mode", 0) == 1) {

                    }

                    ArrayList<Pose> poses2 = new ArrayList<>();
                    ArrayList<PoseStamped> poseStamped2 = new ArrayList<>();

                    // package each trajectory/waypoint into ROS message and send for quad1 ////////
                    if (xes2 == null || yes2 == null || zes2 == null || tes2 == null) {
                    } else {
                        for (int i = 0; i < xes2.size(); i++) {
                            Pose mPose = connectedNode.getTopicMessageFactory().newFromType(Pose._TYPE);
                            geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                            mPoint.setX(xes2.get(i));
                            mPoint.setY(yes2.get(i));
                            mPoint.setZ(zes2.get(i));
                            mPose.setPosition(mPoint);
                            Quaternion quat = connectedNode.getTopicMessageFactory().newFromType(Quaternion._TYPE);
                            quat.setW(tes2.get(i).toSeconds());
                            //Log.i("Times", "" + tes1.get(i).toSeconds());
                            mPose.setOrientation(quat);
                            poses2.add(mPose);

//                            if (pref.getInt("mode", 0) == 0) {
//                                PoseStamped mPoseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
//                                mPoseStamped.getHeader().setStamp(tes1.get(i));
//                                mPoseStamped.setPose(mPose);
//                                poseStamped1.add(mPoseStamped);
//                            }
                        }

                        if (pref.getInt("mode", 0) == 0) {
                            mPoseArray2.setPoses(poses2);
                            //mPath1.setPoses(poseStamped1);

                            // service client definition
                            final ServiceClient<PathPlannerRequest, PathPlannerResponse> serviceClient;
                            try {
                                serviceClient = connectedNode.newServiceClient("app_pathplanner", PathPlanner._TYPE);
                            } catch (ServiceNotFoundException e) {
                                throw new RosRuntimeException(e);
                            }

                            final PathPlannerRequest request = serviceClient.newMessage();
                            request.setInput(mPoseArray2);
                            serviceClient.call(request, new ServiceResponseListener<PathPlannerResponse>() {
                                @Override
                                public void onSuccess(PathPlannerResponse response) {
                                    Log.i("ROSNodeService", "Received serviced Path. Size: " + Double.toString(response.getOutput().getPoses().size()));
                                    DataShare.setServicedPath(2, response.getOutput());
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
                    Thread.sleep(10);

                }
                serviceToggle2 = false;

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                if(serviceToggle3 && pref.getBoolean("serviceToggle",false)) {

                    // trajectory publisher
                    PoseArray mPoseArray3 = connectedNode.getTopicMessageFactory().newFromType(PoseArray._TYPE);
                    Path mPath3 = connectedNode.getTopicMessageFactory().newFromType(Path._TYPE);

                    // configure for trajectory or waypoint mode
                    if (pref.getInt("mode", 0) == 0) {
                        mPoseArray3.getHeader().setFrameId("world");
                        mPoseArray3.getHeader().setSeq(seq3);
                        mPoseArray3.getHeader().setStamp(new Time());

                        mPath3.getHeader().setFrameId("world");
                        mPath3.getHeader().setSeq(seq3);
                        mPath3.getHeader().setStamp(new Time());

                    } else if (pref.getInt("mode", 0) == 1) {

                    }

                    ArrayList<Pose> poses3 = new ArrayList<>();
                    ArrayList<PoseStamped> poseStamped3 = new ArrayList<>();

                    // package each trajectory/waypoint into ROS message and send for quad1 ////////
                    if (xes3 == null || yes3 == null || zes3 == null || tes3 == null) {
                    } else {
                        for (int i = 0; i < xes3.size(); i++) {
                            Pose mPose = connectedNode.getTopicMessageFactory().newFromType(Pose._TYPE);
                            geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                            mPoint.setX(xes3.get(i));
                            mPoint.setY(yes3.get(i));
                            mPoint.setZ(zes3.get(i));
                            mPose.setPosition(mPoint);
                            Quaternion quat = connectedNode.getTopicMessageFactory().newFromType(Quaternion._TYPE);
                            quat.setW(tes3.get(i).toSeconds());
                            //Log.i("Times", "" + tes1.get(i).toSeconds());
                            mPose.setOrientation(quat);
                            poses3.add(mPose);

//                            if (pref.getInt("mode", 0) == 0) {
//                                PoseStamped mPoseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
//                                mPoseStamped.getHeader().setStamp(tes1.get(i));
//                                mPoseStamped.setPose(mPose);
//                                poseStamped1.add(mPoseStamped);
//                            }
                        }

                        if (pref.getInt("mode", 0) == 0) {
                            mPoseArray3.setPoses(poses3);
                            //mPath1.setPoses(poseStamped1);

                            // service client definition
                            final ServiceClient<PathPlannerRequest, PathPlannerResponse> serviceClient;
                            try {
                                serviceClient = connectedNode.newServiceClient("app_pathplanner", PathPlanner._TYPE);
                            } catch (ServiceNotFoundException e) {
                                throw new RosRuntimeException(e);
                            }

                            final PathPlannerRequest request = serviceClient.newMessage();
                            request.setInput(mPoseArray3);
                            serviceClient.call(request, new ServiceResponseListener<PathPlannerResponse>() {
                                @Override
                                public void onSuccess(PathPlannerResponse response) {
                                    Log.i("ROSNodeService", "Received serviced Path. Size: " + Double.toString(response.getOutput().getPoses().size()));
                                    DataShare.setServicedPath(3, response.getOutput());
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
                    Thread.sleep(10);

                }
                serviceToggle3 = false;



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
        int i = 0;
        // while(i < tes1.size()){
        //     Log.i("Times in array", ""+tes1.get(i));
        //       i++;
        //   }
        serviceToggle1 = true;
        Log.i("ROSNodeService","Arrays transferred from MainActivity to nodeService.");
    }

    // MainActivity Preview sends vector of coordinates to here
    void setTraj2(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z, ArrayList<Time> t) {
        xes2 = x;
        yes2 = y;
        zes2 = z;
        tes2 = t;
        int i = 0;
        // while(i < tes1.size()){
        //     Log.i("Times in array", ""+tes1.get(i));
        //       i++;
        //   }
        serviceToggle2 = true;
        Log.i("ROSNodeService","Arrays transferred from MainActivity to nodeService.");
    }

    // MainActivity Preview sends vector of coordinates to here
    void setTraj3(ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z, ArrayList<Time> t) {
        xes3 = x;
        yes3 = y;
        zes3 = z;
        tes3 = t;
        int i = 0;
        // while(i < tes1.size()){
        //     Log.i("Times in array", ""+tes1.get(i));
        //       i++;
        //   }
        serviceToggle3 = true;
        Log.i("ROSNodeService","Arrays transferred from MainActivity to nodeService.");
    }
}