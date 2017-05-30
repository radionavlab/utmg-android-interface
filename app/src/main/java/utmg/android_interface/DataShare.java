package utmg.android_interface;

import android.app.Application;
import android.graphics.Color;
import java.util.ArrayList;

public class DataShare extends Application {

    // so purty
    // it does stuff to things

    private static Thing quad1 = new Thing();
    private static Thing quad2 = new Thing();
    private static Thing quad3 = new Thing();

    private static Thing sword = new Thing();
    private static Thing obstacle1 = new Thing();
    private static Thing obstacle2 = new Thing();

    ArrayList<Float> xCoordVec1 = new ArrayList<>();
    ArrayList<Float> yCoordVec1 = new ArrayList<>();
    ArrayList<Float> zCoordVec1 = new ArrayList<>();
    ArrayList<Float> xWaypoint1 = new ArrayList<>();
    ArrayList<Float> yWaypoint1 = new ArrayList<>();

    ArrayList<Float> xCoordVec2 = new ArrayList<>();
    ArrayList<Float> yCoordVec2 = new ArrayList<>();
    ArrayList<Float> zCoordVec2 = new ArrayList<>();
    ArrayList<Float> xWaypoint2 = new ArrayList<>();
    ArrayList<Float> yWaypoint2 = new ArrayList<>();

    ArrayList<Float> xCoordVec3 = new ArrayList<>();
    ArrayList<Float> yCoordVec3 = new ArrayList<>();
    ArrayList<Float> zCoordVec3 = new ArrayList<>();
    ArrayList<Float> xWaypoint3 = new ArrayList<>();

    public DataShare() {

        quad1.setQuadColour(Color.RED);
        quad2.setQuadColour(Color.GREEN);
        quad3.setQuadColour(Color.BLUE);

    }

    public static Thing getInstance(String str) {

        switch (str) {
            case "quad1":
                return quad1;
            case "quad2":
                return quad2;
            case "quad3":
                return quad3;
            case "sword":
                return sword;
            case "obstacle1":
                return obstacle1;
            case "obstacle2":
                return obstacle2;
            default:
                return null;
        }

    }

}
