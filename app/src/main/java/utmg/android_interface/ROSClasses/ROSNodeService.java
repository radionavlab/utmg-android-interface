package utmg.android_interface.ROSClasses;

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

import geometry_msgs.Point;
import geometry_msgs.Pose;
import geometry_msgs.PoseArray;
import geometry_msgs.PoseStamped;
import mav_trajectory_generation_ros.PVAJS;
import mav_trajectory_generation_ros.minSnapStamped;
import mav_trajectory_generation_ros.minSnapStampedRequest;
import mav_trajectory_generation_ros.minSnapStampedResponse;
import nav_msgs.Path;
import utmg.android_interface.Canvases.DrawingCanvas;
import utmg.android_interface.DataShare;
import utmg.android_interface.Activities.MainActivity;
import utmg.android_interface.DefaultCallback;
import utmg.android_interface.QuadUtils.Point3;
import utmg.android_interface.QuadUtils.Quad;
import utmg.android_interface.QuadUtils.Trajectory;

public class ROSNodeService extends AbstractNodeMain implements NodeMain {

    SharedPreferences pref;
    boolean preview;

    private int seq;

    DefaultCallback callback;

    private static final String TAG = ROSNodeService.class.getSimpleName();
    public ROSNodeService(){
        super();
        seq=0;
        preview=false;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("utmg_android_service");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        Context appCon = MainActivity.getContextOfApplication();

        pref = appCon.getSharedPreferences("Pref", 0);



        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
            // package each trajectory/waypoint into ROS message and send for quad1 ////////
            if (preview){
                DrawingCanvas canvasView = (DrawingCanvas)DataShare.retrieve("canvasView");
                ArrayList<Quad> quads = (ArrayList<Quad>)DataShare.retrieve("quads");
                for(Quad q: quads) {
                    if(q.getTrajectory().size()==0) {//no points drawn for specific quad
                        callback.onFinished(false);
                        continue;
                    }
                    // trajectory publisher
                    PoseArray poseArray = connectedNode.getTopicMessageFactory().newFromType(PoseArray._TYPE);
                    Path path = connectedNode.getTopicMessageFactory().newFromType(Path._TYPE);

                    // configure for trajectory or waypoint mode
                    if (pref.getInt("mode", 0) == 0) {
                        poseArray.getHeader().setFrameId("world");
                        poseArray.getHeader().setSeq(seq);
                        poseArray.getHeader().setStamp(new Time());

                        path.getHeader().setFrameId("world");
                        path.getHeader().setSeq(seq++);
                        path.getHeader().setStamp(new Time());

                    } else if (pref.getInt("mode", 0) == 1) {

                    }

                    //Add points from trajectory to array of stamped poses
                    ArrayList<PoseStamped> poseStampedArray = new ArrayList<>();
                    Trajectory toSend = compressArrays(q.getTrajectory());
                    for (Point3 p: toSend.getPoints()) {
                        Pose pose = connectedNode.getTopicMessageFactory().newFromType(Pose._TYPE);
                        geometry_msgs.Point point = connectedNode.getTopicMessageFactory().newFromType(geometry_msgs.Point._TYPE);
                        point.setX(p.getX());
                        point.setY(p.getY());
                        point.setZ(p.getZ());
                        PoseStamped poseStamped = connectedNode.getTopicMessageFactory().newFromType((PoseStamped._TYPE));
                        poseStamped.getHeader().setStamp(p.getTime());
                        poseStamped.setPose(pose);
                        poseStampedArray.add(poseStamped);
                    }

                    if (pref.getInt("mode", 0) == 0) {
                        path.setPoses(poseStampedArray);
                        final ServiceClient<minSnapStampedRequest, minSnapStampedResponse> serviceClient;
                        try {
                            // TODO might have to change the first argument to something else... not sure what
                            serviceClient = connectedNode.newServiceClient("minSnapStamped", minSnapStamped._TYPE);
                        } catch (ServiceNotFoundException e) {
                            throw new RosRuntimeException(e);
                        }

                        final minSnapStampedRequest request = serviceClient.newMessage();
                        request.setWaypoints(path);
                        serviceClient.call(request, new QuadServiceResponseListener<minSnapStampedResponse>(q.getName(),callback));


                    } else if (pref.getInt("mode", 0) == 1) {// TODO: 7/27/2017 lol this should be fixed

                    }
                }
                preview=false;
            }

            // go to sleep for one second TODO for live mode reduce this time!
            Thread.sleep(10);


            }
        };
        connectedNode.executeCancellableLoop(loop);
    }

    // MainActivity Preview sends vector of coordinates to here
    public void getOptimizedTrajectories(DefaultCallback dc) {
        callback=dc;
        preview=true;
    }

    public Trajectory compressArrays(Trajectory old) {
        float siddarth = 1;
        float x1, x2 = 0;
        float y1, y2 = 0;
        float slope1 = 0;
        float slope2 = 0;
        Trajectory compressed=new Trajectory();
        compressed.addPoint(old.getPoints().get(0));
        for(int i=1;i<old.size()-1;i++) {

            x1 = old.getPoints().get(i).getX();
            x2 = old.getPoints().get(i+1).getX();
            y1 = old.getPoints().get(i).getY();
            y2 = old.getPoints().get(i+1).getY();
//                    slope1 = (y2 - y1)/(x2 - x1);
            slope1 = (float)Math.atan2((y2 - y1),(x2 - x1));
            if(Math.abs(slope1 - slope2) > siddarth) {
                compressed.addPoint(new Point3(x1,y1,old.getPoints().get(i).getZ(),old.getPoints().get(i).getTime()));
                slope2 = slope1;
            }
            i++;
        }
        compressed.addPoint(old.getPoints().get(old.size()-1));
        return compressed;
    }
    class QuadServiceResponseListener<MessageType> implements ServiceResponseListener<minSnapStampedResponse>{
        String name;
        DefaultCallback callback;
        public QuadServiceResponseListener(String name, DefaultCallback callback){
            this.name=name;
            this.callback=callback;
        }
        @Override
        public void onSuccess(minSnapStampedResponse response) {
            Log.i("ROSNodeService", "Received serviced Path. Size: " + Double.toString(response.getFlatStates().getPVAJSArray().size()));
            Trajectory traj = new Trajectory();
            List<PVAJS> dataVec = response.getFlatStates().getPVAJSArray();
            for(PVAJS data: dataVec){
                Point p = data.getPos();
                Time t = new Time((int)Math.round(data.getTime()/1000),(int)Math.round(data.getTime()%1000*Math.pow(10,6)));
                traj.addPoint(new Point3((float)p.getX(),(float)p.getY(),(float)p.getZ(),t));
            }
            ArrayList<Quad> quads = (ArrayList<Quad>)DataShare.retrieve("quads");
            for(Quad q: quads){
                if(q.getName().equals(name)){
                    q.setOptimizedTrajectory(traj);
                    break;
                }
            }
            callback.onFinished(true);
        }

        @Override
        public void onFailure(RemoteException e) {
            callback.onFinished(false);
            e.printStackTrace();
            throw new RosRuntimeException(e);
        }
    }

}