package utmg.android_interface;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;
import org.ros.concurrent.CancellableLoop;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import nav_msgs.Path;

public class ROSNodePublish extends AbstractNodeMain implements NodeMain {

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;

    private boolean publishToggle1 = false;
    private boolean publishToggle2 = false;
    private boolean publishToggle3 = false;

    nav_msgs.Path path1;
    nav_msgs.Path path2;
    nav_msgs.Path path3;

    private int seq1 = 0;
    private int seq2 = 0;
    private int seq3 = 0;

    private static final String TAG = ROSNodePublish.class.getSimpleName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("utmg_android_publish");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {

        Context appCon = MainActivity.getContextOfApplication();

        final Publisher<nav_msgs.Path> publisherPath1 = connectedNode.newPublisher(GraphName.of("PosControl/Path/Quad1"), Path._TYPE);
        final Publisher<nav_msgs.Path> publisherPath2 = connectedNode.newPublisher(GraphName.of("PosControl/Path/Quad2"), Path._TYPE);
        final Publisher<nav_msgs.Path> publisherPath3 = connectedNode.newPublisher(GraphName.of("PosControl/Path/Quad3"), Path._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {

                if (publishToggle1 || publishToggle2 || publishToggle3) {

                    // package each trajectory/waypoint into ROS message and send for quad1 ////////
                    if (path1 != null) {

                        path1.getHeader().setFrameId("world");
//                        path1.getHeader().setSeq(seq1);

//                        if (pref.getInt("mode", 0) == 0) {

                            if (publishToggle1) {
                                publisherPath1.publish(path1);
                                Log.i("ROSNodePublish", "Published Path 1 to ROS.");

                                seq1 = seq1 + 1;
                            }
                            publishToggle1 = false;

//                        } else if (pref.getInt("mode", 0) == 1) { }
                    }
                    ////////////////////////////////////////////////////////////////////////////////


                    // package each trajectory/waypoint into ROS message and send for quad2 ////////
                    if (path2 != null) {

                        path2.getHeader().setFrameId("world");
//                        path2.getHeader().setSeq(seq2);

//                        if (pref.getInt("mode", 0) == 0) {

                        if (publishToggle2) {
                            publisherPath2.publish(path2);
                            Log.i("ROSNodePublish", "Published Path 2 to ROS.");

                            seq2 = seq2 + 1;
                        }
                        publishToggle2 = false;

//                        } else if (pref.getInt("mode", 0) == 2) { }
                    }
                    ////////////////////////////////////////////////////////////////////////////////


                    // package each trajectory/waypoint into ROS message and send for quad3 ////////
                    if (path3 != null) {

                        path3.getHeader().setFrameId("world");
//                        path3.getHeader().setSeq(seq3);

//                        if (pref.getInt("mode", 0) == 0) {

                        if (publishToggle3) {
                            publisherPath3.publish(path3);
                            Log.i("ROSNodePublish", "Published Path 3 to ROS.");

                            seq3 = seq3 + 1;
                        }
                        publishToggle3 = false;

//                        } else if (pref.getInt("mode", 0) == 3) { }
                    }
                    ////////////////////////////////////////////////////////////////////////////////

                    // go to sleep for one second TODO for live mode reduce this time!
                    Thread.sleep(1000);
                }
            }
        };
        connectedNode.executeCancellableLoop(loop);

    }

    // MainActivity FAB sends vector of coordinates to here
    void setPath1(nav_msgs.Path p) {
        path1 = p;
        publishToggle1 = true;
        Log.i("ROSNodePublish","Path 1 transferred to NodePublish");
    }

    void setPath2(nav_msgs.Path p) {
        path2 = p;
        publishToggle2 = true;
        Log.i("ROSNodePublish","Path 2 transferred to NodePublish");
    }

    void setPath3(nav_msgs.Path p) {
        path3 = p;
        publishToggle3 = true;
        Log.i("ROSNodePublish","Path 3 transferred to NodePublish");
    }
}
