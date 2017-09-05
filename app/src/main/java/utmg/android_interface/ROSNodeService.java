package utmg.android_interface;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import java.util.ArrayList;
import java.util.List;

import geometry_msgs.Accel;
import geometry_msgs.Pose;
import geometry_msgs.PoseArray;
import geometry_msgs.PoseStamped;
import app_pathplanner_interface.PVATrajectoryStamped;
import app_pathplanner_interface.PVAStamped;
import geometry_msgs.Twist;
import geometry_msgs.Vector3;
import mav_trajectory_generation_ros.PVAJS;
import mav_trajectory_generation_ros.minSnapStamped;
import mav_trajectory_generation_ros.minSnapStampedRequest;
import mav_trajectory_generation_ros.minSnapStampedResponse;
import nav_msgs.Path;

public class ROSNodeService extends AbstractNodeMain implements NodeMain {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private ArrayList<Float> xes;
    private ArrayList<Float> yes;
    private ArrayList<Float> zes;
    private ArrayList<Time> tes;

    private boolean serviceToggle = false;

    private int seq = 0;

    private int quadNumber;

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

                if(serviceToggle && pref.getBoolean("serviceToggle",false)) {

                    // trajectory publisher
                    PoseArray mPoseArray1 = connectedNode.getTopicMessageFactory().newFromType(PoseArray._TYPE);
                    Path mPath1 = connectedNode.getTopicMessageFactory().newFromType(Path._TYPE);
                    final PVATrajectoryStamped mPVATrajectory = connectedNode.getTopicMessageFactory().newFromType(PVATrajectoryStamped._TYPE);

                    // configure for trajectory or waypoint mode
                    if (pref.getInt("mode", 0) == 0) {
                        mPoseArray1.getHeader().setFrameId("world");
                        mPoseArray1.getHeader().setSeq(seq);
                        mPoseArray1.getHeader().setStamp(new Time());

                        mPath1.getHeader().setFrameId("world");
                        mPath1.getHeader().setSeq(seq);
                        mPath1.getHeader().setStamp(new Time());

                        mPVATrajectory.getHeader().setFrameId("world");
                        mPVATrajectory.getHeader().setSeq(seq);
                        mPVATrajectory.getHeader().setStamp(new Time());

                    } else if (pref.getInt("mode", 0) == 1) {

                    }

//                    ArrayList<Pose> poses1 = new ArrayList<>();
                    ArrayList<PoseStamped> poseStamped1 = new ArrayList<>();

                    // package each trajectory/waypoint into ROS message and send for quad1 ////////
                    if (xes == null || yes == null || zes == null || tes == null) {
                    } else {
                        
                        for (int i = 0; i < xes.size(); i++) {
                            Pose mPose = connectedNode.getTopicMessageFactory().newFromType(Pose._TYPE);
                            geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                            mPoint.setX(xes.get(i));
                            mPoint.setY(yes.get(i));
                            mPoint.setZ(zes.get(i));
                            mPose.setPosition(mPoint);
//                            poses1.add(mPose);

//                            if (pref.getInt("mode", 0) == 0) {
                            PoseStamped mPoseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
                            mPoseStamped.getHeader().setStamp(tes.get(i));
                            mPoseStamped.setPose(mPose);
                            poseStamped1.add(mPoseStamped);
//                            }

                        }

                        if (pref.getInt("mode", 0) == 0) {
//                            mPoseArray1.setPoses(poses1);
                            mPath1.setPoses(poseStamped1);

                            String minSnapOptTime = "minSnapOptTime";
                            String minSnap = "minSnap";
                            String serviceName = minSnapOptTime;//TODO
                            Log.i("ROSNodeService", serviceName);
                            // service client definition
                            final ServiceClient<minSnapStampedRequest, minSnapStampedResponse> serviceClient;
                            try {
                                // TODO might have to change the first argument to something else... not sure what
                                serviceClient = connectedNode.newServiceClient(serviceName, minSnapStamped._TYPE);
                            } catch (ServiceNotFoundException e) {
                                throw new RosRuntimeException(e);
                            }

                            final minSnapStampedRequest request = serviceClient.newMessage();
                            request.setWaypoints(mPath1);
                            serviceClient.call(request, new ServiceResponseListener<minSnapStampedResponse>() {
                                @Override
                                public void onSuccess(minSnapStampedResponse response) {
                                    Log.i("ROSNodeService", "Received serviced Path. Size: " + Double.toString(response.getFlatStates().getPVAJSArray().size()));

                                    // TODO might be buggy!
                                    //convert path planner format to nav_msgs/Path
                                    ArrayList<PVAStamped> listPVA = new ArrayList<PVAStamped>();
                                    List<PVAJS> tempVec = response.getFlatStates().getPVAJSArray();
                                    ArrayList tempPath = new ArrayList();
                                    Path tempServicedPath =  connectedNode.getTopicMessageFactory().newFromType(Path._TYPE);

                                    for (int i = 0; i < tempVec.size(); i++) {
                                        PVAStamped mPVAStamped = connectedNode.getTopicMessageFactory().newFromType(PVAStamped._TYPE);
                                        Pose mPose = connectedNode.getTopicMessageFactory().newFromType(Pose._TYPE);
                                        Twist mTwist = connectedNode.getTopicMessageFactory().newFromType(Twist._TYPE);
                                        Accel mAccel = connectedNode.getTopicMessageFactory().newFromType(Accel._TYPE);

                                        geometry_msgs.Point mPoint = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                                        Vector3 mVel = connectedNode.getTopicMessageFactory().newFromType(Twist._TYPE);
                                        Vector3 mAc = connectedNode.getTopicMessageFactory().newFromType(Accel._TYPE);

                                        mPoint.setX(tempVec.get(i).getPos().getX());
                                        mPoint.setY(tempVec.get(i).getPos().getY());
                                        mPoint.setZ(tempVec.get(i).getPos().getZ());
                                        mPose.setPosition(mPoint);
                                        //mPose.setPosition(tempVec.get(i).getPos()); // TODO if this doesn't work, uncomment above 4 lines, and comment this

                                        mVel.setX(tempVec.get(i).getVel().getX());
                                        mVel.setY(tempVec.get(i).getVel().getY());
                                        mVel.setZ(tempVec.get(i).getVel().getZ());
                                        mTwist.setLinear(mVel);

                                        mAc.setX(tempVec.get(i).getAcc().getX());
                                        mAc.setY(tempVec.get(i).getAcc().getY());
                                        mAc.setZ(tempVec.get(i).getAcc().getZ());
                                        mAccel.setLinear(mAc);

                                        PoseStamped mPoseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
                                        mPoseStamped.getHeader().setStamp(new Time()); // TODO fix time stamps
                                        mPoseStamped.setPose(mPose);
                                        tempPath.add(mPoseStamped);

                                        mPVAStamped.setPos(mPose);
                                        mPVAStamped.setVel(mTwist);
                                        mPVAStamped.setAcc(mAccel);
                                        listPVA.add(mPVAStamped);
                                    }

                                    tempServicedPath.setPoses(tempPath);
                                    mPVATrajectory.setPva(listPVA);

                                    DataShare.setServicedPath(quadNumber, tempServicedPath); // TODO change first argument for whichever serviced path requested

                                    //connectedNode.getLog().info(
                                    //        String.format("%d + %d = %d", request.getA(), request.getB(), response.getSum()));
                                }

                                @Override
                                public void onFailure(RemoteException e) {// SIDDARTH what does this mean
                                    throw new RosRuntimeException(e);// where the error for multiple quads is being thrown
                                }
                            });


                        } else if (pref.getInt("mode", 0) == 1) {

                        }
                    }

                    // go to sleep for one second TODO for live mode reduce this time!
                    Thread.sleep(10);

                }
                serviceToggle = false;
            }
        };
        connectedNode.executeCancellableLoop(loop);

    }

    // MainActivity Preview sends vector of coordinates to here
    void setTraj(int i,ArrayList<Float> x, ArrayList<Float> y, ArrayList<Float> z, ArrayList<Time> t) {
        xes = x;
        yes = y;
        zes = z;
        tes = t;
        serviceToggle = true;
        quadNumber = i;
        Log.i("ROSNodeService","Arrays transferred from MainActivity to nodeService.");
    }

}