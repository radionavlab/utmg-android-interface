package utmg.android_interface;

import android.util.Log;

import org.ros.concurrent.CancellableLoop;
import org.ros.internal.message.RawMessage;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import std_msgs.Float32MultiArray;
import std_msgs.MultiArrayDimension;
import std_msgs.MultiArrayLayout;

public class TrajectoryArrayPublisherNode extends AbstractNodeMain implements NodeMain {

    private static final String TAG = TrajectoryArrayPublisherNode.class.getSimpleName();

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("SimplePublisher/TimeLoopNode");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final Publisher<std_msgs.String> publisher = connectedNode.newPublisher(GraphName.of("time"), std_msgs.String._TYPE);

        final Publisher<std_msgs.Float32MultiArray> publisher1 = connectedNode.newPublisher(GraphName.of("android_quad_trajectory"), Float32MultiArray._TYPE);

        final CancellableLoop loop = new CancellableLoop() {
            @Override
            protected void loop() throws InterruptedException {
                // retrieve current system time
                String time = new SimpleDateFormat("HH:mm:ss").format(new Date());

                Log.i(TAG, "publishing the current time: " + time);

                // create and publish a simple string message
                std_msgs.String str = publisher.newMessage();
                str.setData("The current time is: " + time);
                publisher.publish(str);

                // trajectory publisher
                std_msgs.Float32MultiArray xyTraj = publisher1.newMessage();

                //MultiArrayLayout multiArrayLayout = new MultiArrayLayout();
                //xyTraj.setLayout();

                // go to sleep for one second
                Thread.sleep(1000);
            }
        };
        connectedNode.executeCancellableLoop(loop);
    }





    public class Arrayer implements MultiArrayLayout {

        List<MultiArrayDimension> dims =  new ArrayList<MultiArrayDimension>();
        int offset = 0;

        public List<MultiArrayDimension> getDim() {

            return dims;
        }

        public void setDim(List<MultiArrayDimension> var1) {


        }

        public int getDataOffset() {

            return offset;

        }

         public void setDataOffset(int var1) {

             offset = var1;

        }

        public RawMessage toRawMessage() {

            return null;
        }


    }

}
