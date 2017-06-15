package utmg.android_interface;

import android.app.Application;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

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

    private static CanvasView hackyCanvas;

    private static Path mPath1;
    private static Path mPath2;
    private static Path mPath3;

    private static Paint mPaint1;
    private static Paint mPaint2;
    private static Paint mPaint3;

    public DataShare() {
        hackyCanvas = null;

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

    public static void setCanvas(CanvasView c) {
        hackyCanvas = c;
    }

    public static CanvasView getHackyCanvas() {
        return hackyCanvas;
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
                return mPath1;
        }
    }

}
