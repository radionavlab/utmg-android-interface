package utmg.android_interface.controller.canvas;

import utmg.android_interface.model.util.Altitude;
import android.util.Log;

import utmg.android_interface.model.util.POI;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.Point4;
import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class DrawingStartTouchHandler implements IStartTouchHandler {

    private final Trajectory trajectory;
    private final Altitude altitude;
    private final POI poi;

//    private final Point4 POI;

    public DrawingStartTouchHandler(
            final Trajectory trajectory,
            final POI poi,
            final Altitude altitude) {
        this.trajectory = trajectory;
        this.poi = poi;
        this.altitude = altitude;
    }



    @Override
    public void handle(
            final float pixelX,
            final float pixelY,
            final AbstractCanvas canvas) {



        float w = (float) Math.atan(poi.y-pixelY / poi.x-pixelX);


        // Started drawing a new trajectory. Must reset old trajectory.
        trajectory.clear();

        // Convert the points from pixels to meters
        final float meterX = canvas.toMetersX(pixelX);
        final float meterY = canvas.toMetersY(pixelY);

        // Start the new trajectory by appending the start point
        trajectory.addPoint(new Point4(meterX, meterY, altitude.value, w));

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
