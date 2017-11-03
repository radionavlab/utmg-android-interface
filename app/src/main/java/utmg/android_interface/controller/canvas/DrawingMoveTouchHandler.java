package utmg.android_interface.controller.canvas;

import utmg.android_interface.model.util.Altitude;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class DrawingMoveTouchHandler implements IMoveTouchHandler {

    private final Trajectory trajectory;
    private final Altitude altitude;

    public DrawingMoveTouchHandler(
            final Trajectory trajectory,
            final Altitude altitude) {
        this.trajectory = trajectory;
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

        // Append the movement point
        trajectory.addPoint(new Point3(meterX, meterY, this.altitude.value));

        // TODO: Implement some sort of tolerance. No need to add a point for every movement.
        // TODO: Prevent trajectory from leaving arena

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
