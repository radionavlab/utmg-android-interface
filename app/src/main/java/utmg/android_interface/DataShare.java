package utmg.android_interface;

import android.app.Application;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import org.ros.node.ConnectedNode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import geometry_msgs.PoseArray;

public class DataShare {
    /*
        quads instanceof ArrayList<Quad>
        obstacles instanceof ArrayList<Obstacle>
        sword instanceof Sword
        teamName instanceof String
        masterUri instanceof java.net.URI
     */



    static Map<String, WeakReference<Object>> data = new HashMap<String, WeakReference<Object>>();

    public static void save(String id, Object object) {
        data.put(id, new WeakReference<Object>(object));
    }

    public static Object retrieve(String id) {
        WeakReference<Object> objectWeakReference = data.get(id);
        return objectWeakReference.get();
    }
}
