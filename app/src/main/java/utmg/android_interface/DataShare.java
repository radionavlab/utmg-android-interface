package utmg.android_interface;

import android.app.Application;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import org.ros.node.ConnectedNode;

import java.util.ArrayList;

import geometry_msgs.PoseArray;

// sharing data among multiple classes
public class DataShare extends Application {

    private static Thing quad1 = new Thing();
    private static Thing quad2 = new Thing();
    private static Thing quad3 = new Thing();

    private static Thing sword = new Thing();
    private static Thing obstacle1 = new Thing();
    private static Thing obstacle2 = new Thing();

    private static android.graphics.Path mPath1;
    private static android.graphics.Path mPath2;
    private static android.graphics.Path mPath3;

    private static Paint mPaint1;
    private static Paint mPaint2;
    private static Paint mPaint3;

    private static ArrayList<org.ros.message.Time> mTime1;
    private static ArrayList<org.ros.message.Time> mTime2;
    private static ArrayList<org.ros.message.Time> mTime3;

    private static ArrayList<Float> xPixel1;
    private static ArrayList<Float> xPixel2;
    private static ArrayList<Float> xPixel3;

    private static ArrayList<Float> yPixel1;
    private static ArrayList<Float> yPixel2;
    private static ArrayList<Float> yPixel3;

    private static ArrayList<Float> xCompressed1;
    private static ArrayList<Float> xCompressed2;
    private static ArrayList<Float> xCompressed3;

    private static ArrayList<Float> yCompressed1;
    private static ArrayList<Float> yCompressed2;
    private static ArrayList<Float> yCompressed3;

    private static int seekLoc1;
    private static int seekLoc2;
    private static int seekLoc3;

    private static nav_msgs.Path servicedPath1;
    private static nav_msgs.Path servicedPath2;
    private static nav_msgs.Path servicedPath3;

    private static boolean playBackState;
    private static boolean serviceState; // TODO get rid of this junk

    private static float mainCanvasHeight;
    private static float mainCanvasWidth;

    public DataShare() {

        quad1.setQuadColour(Color.parseColor("#FF1900"));
        quad2.setQuadColour(Color.parseColor("#009E3A"));
        quad3.setQuadColour(Color.parseColor("#225CCF"));

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

    public static void setPaint(int i, Paint p) {
        switch (i) {
            case 1:
                mPaint1 = p;
                break;
            case 2:
                mPaint2 = p;
                break;
            case 3:
                mPaint3 = p;
                break;
            default:
        }
    }

    public static Paint getPaint(int i) {
        switch (i) {
            case 1:
                return mPaint1;
            case 2:
                return mPaint2;
            case 3:
                return mPaint3;
            default:
                return null;
        }
    }

    public static void setPath(int i, android.graphics.Path p) {
        switch (i) {
            case 1:
                mPath1 = p;
                break;
            case 2:
                mPath2 = p;
                break;
            case 3:
                mPath3 = p;
                break;
            default:
        }
    }

    public static android.graphics.Path getPath(int i) {
        switch (i) {
            case 1:
                return mPath1;
            case 2:
                return mPath2;
            case 3:
                return mPath3;
            default:
                return null;
        }
    }

    public static void setCurrentTime (int i, ArrayList<org.ros.message.Time> t) {
        switch (i) {
            case 1:
                mTime1 = t;
                break;
            case 2:
                mTime2 = t;
                break;
            case 3:
                mTime3 = t;
                break;
            default:
        }
    }

    public static ArrayList<org.ros.message.Time> getCurrentTime (int i) {
        switch (i) {
            case 1:
                return mTime1;
            case 2:
                return mTime2;
            case 3:
                return mTime3;
            default:
                return null;
        }
    }

    public static void setXPixelVec (int i, ArrayList<Float> p) {
        switch (i) {
            case 1:
                xPixel1 = p;
                break;
            case 2:
                xPixel2 = p;
                break;
            case 3:
                xPixel3 = p;
                break;
            default:
        }
    }

    public static ArrayList<Float> getXPixelVec (int i) {
        switch (i) {
            case 1:
                return xPixel1;
            case 2:
                return xPixel2;
            case 3:
                return xPixel3;
            default:
                return null;
        }
    }

    public static void setYPixelVec (int i, ArrayList<Float> p) {
        switch (i) {
            case 1:
                yPixel1 = p;
                break;
            case 2:
                yPixel2 = p;
                break;
            case 3:
                yPixel3 = p;
                break;
            default:
        }
    }

    public static ArrayList<Float> getYPixelVec (int i) {
        switch (i) {
            case 1:
                return yPixel1;
            case 2:
                return yPixel2;
            case 3:
                return yPixel3;
            default:
                return null;
        }
    }

    public static void setSeekLoc(int i, int x) {
        switch (i) {
            case 1:
                seekLoc1 = x;
                break;
            case 2:
                seekLoc2 = x;
                break;
            case 3:
                seekLoc3 = x;
                break;
            default:
        }
    }

    public static int getSeekLoc(int i) {
        switch (i) {
            case 1:
                return seekLoc1;
            case 2:
                return seekLoc2;
            case 3:
                return seekLoc3;
            default:
                return 0;
        }
    }

    public static void setServicedPath(int i, nav_msgs.Path p) {
        switch (i) {
            case 1:
                servicedPath1 = p;
                break;
            case 2:
                servicedPath2 = p;
                break;
            case 3:
                servicedPath3 = p;
                break;
            default:
        }
    }

    public static nav_msgs.Path getServicedPath(int i) {
        switch (i) {
            case 1:
                return servicedPath1;
            case 2:
                return servicedPath2;
            case 3:
                return servicedPath3;
            default:
                return null;
        }
    }

    public static void setPlayBackState(boolean x) {
        playBackState = x;
    }

    public static boolean getPlayBackState() {
        return playBackState;
    }

    public static boolean setServiceState(boolean d) {
        serviceState = d;
        return serviceState;
    }

    public static boolean getServiceState() {
        return serviceState;
    }

    public static void setMainCanvasHeight(float h) {
        mainCanvasHeight = h;
    }

    public static float getMainCanvasHeight () {
        return mainCanvasHeight;
    }

    public static void setMainCanvasWidth(float w) {
        mainCanvasWidth = w;
    }

    public static float getMainCanvasWidth () {
        return mainCanvasWidth;
    }

    public static void setXCompressedVec (int i, ArrayList<Float> p) {
        switch (i) {
            case 1:
                xCompressed1 = p;
                break;
            case 2:
                xCompressed2 = p;
                break;
            case 3:
                xCompressed3 = p;
                break;
            default:
        }
    }

    public static ArrayList<Float> getXCompressedVec (int i) {
        switch (i) {
            case 1:
                return xCompressed1;
            case 2:
                return xCompressed2;
            case 3:
                return xCompressed3;
            default:
                return null;
        }
    }

    public static void setYCompressedVec (int i, ArrayList<Float> p) {
        switch (i) {
            case 1:
                yCompressed1 = p;
                break;
            case 2:
                yCompressed2 = p;
                break;
            case 3:
                yCompressed3 = p;
                break;
            default:
        }
    }

    public static ArrayList<Float> getYCompressedVec (int i) {
        switch (i) {
            case 1:
                return yCompressed1;
            case 2:
                return yCompressed2;
            case 3:
                return yCompressed3;
            default:
                return null;
        }
    }

}