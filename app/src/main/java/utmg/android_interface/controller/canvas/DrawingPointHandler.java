package utmg.android_interface.controller.canvas;


import utmg.android_interface.model.util.Point;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by pwhitt24 on 10/25/17.
 */

public class DrawingPointHandler implements IStartTouchHandler {

    private final Point point;

    public DrawingPointHandler(
            final Point point) {
        this.point = point;
    }


    @Override
    public void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas){


        //Selected new point. Must reset old point.
        point.clear();


        //Obtain the point
        point.addPoint(new Point3(x, y, point.getAltitude(), canvas.getCurrentTime()));

        canvas.invalidate();
    }


}


