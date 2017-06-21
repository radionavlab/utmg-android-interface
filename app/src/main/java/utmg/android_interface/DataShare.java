package utmg.android_interface;

import android.app.Application;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import java.sql.Time;
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

    private static Path mPath1;
    private static Path mPath2;
    private static Path mPath3;

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
                return mPaint1;
        }
    }

    public static void setPath(int i, Path p) {
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

    public static Path getPath(int i) {
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

}
