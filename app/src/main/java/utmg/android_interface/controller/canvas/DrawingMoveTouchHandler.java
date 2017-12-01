package utmg.android_interface.controller.canvas;

import utmg.android_interface.model.util.Altitude;
import utmg.android_interface.model.util.POI;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.Point4;
import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class DrawingMoveTouchHandler implements IMoveTouchHandler {

    private final Trajectory trajectory;
    private final Altitude altitude;
    private final POI poi;

    public DrawingMoveTouchHandler(
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

        // Convert the points from pixels to meters
        final float meterX = canvas.toMetersX(pixelX);
        final float meterY = canvas.toMetersY(pixelY);


        float w = (float) Math.atan2((canvas.toMetersY(poi.y)-meterY),(canvas.toMetersX(poi.x)-meterX));


        // TODO: Why am I asking canvas for time?
        // Append the movement point
        trajectory.addPoint(new Point4(meterX, meterY, this.altitude.value, w));

        // TODO: Implement some sort of tolerance. No need to add a point for every movement.
        // TODO: Prevent trajectory from leaving arena

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
