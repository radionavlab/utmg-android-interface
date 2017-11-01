package utmg.android_interface.controller.canvas;

import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class DrawingStartTouchHandler implements IStartTouchHandler {

    private final Trajectory trajectory;

    public DrawingStartTouchHandler(
            final Trajectory trajectory) {
        this.trajectory = trajectory;
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

        // Start the new trajectory by appending the start point
        trajectory.addPoint(new Point3(meterX, meterY, trajectory.getAltitude()));

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
