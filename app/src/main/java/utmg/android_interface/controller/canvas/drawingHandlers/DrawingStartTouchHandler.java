package utmg.android_interface.controller.canvas.drawingHandlers;

import utmg.android_interface.controller.canvas.abstractHandlers.IStartTouchHandler;
import utmg.android_interface.model.util.Altitude;
import utmg.android_interface.model.util.InterestPoint;
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
    private final InterestPoint interestPoint;

    public DrawingStartTouchHandler(
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

        // Started drawing a new trajectory. Must reset old trajectory.
        trajectory.clear();

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

        // Start the new trajectory by appending the start point
        trajectory.addPoint(new Point4(meterX, meterY, altitude.value, (float)yaw));

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
