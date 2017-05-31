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


    public DataShare() {

//        quad1.setQuadColour(Color.RED);
//        quad2.setQuadColour(Color.GREEN);
//        quad3.setQuadColour(Color.BLUE);

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
