package utmg.android_interface.controller.canvas.drawingHandlers;

import utmg.android_interface.controller.canvas.abstractHandlers.IMoveTouchHandler;
import utmg.android_interface.model.util.Altitude;
import utmg.android_interface.model.util.InterestPoint;
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
    final InterestPoint interestPoint;

    public DrawingMoveTouchHandler(
            final Trajectory trajectory,
            final Altitude altitude,
            final InterestPoint interestPoint) {
        this.trajectory = trajectory;
        this.altitude = altitude;
        this.interestPoint = interestPoint;
    }

    @Override
    public void handle(
            final float pixelX,
            final float pixelY,
            final AbstractCanvas canvas) {

        // Convert the points from pixels to meters
        final float meterX = canvas.toMetersX(pixelX);
        final float meterY = canvas.toMetersY(pixelY);

        // Calculate the angle to the interest point
        double yaw;
        if(!this.interestPoint.exists()) {
            yaw = Float.NaN;
        } else {
            yaw = Math.atan2(interestPoint.y - pixelY, interestPoint.x - pixelX);
        }

        // Append the movement point
        trajectory.addPoint(new Point4(meterX, meterY, this.altitude.value, (float)yaw));

        // TODO: Implement some sort of tolerance. No need to add a point for every movement.
        // TODO: Prevent trajectory from leaving arena

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
