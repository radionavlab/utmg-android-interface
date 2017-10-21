package utmg.android_interface.controller;

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
            final float x,
            final float y,
            final AbstractCanvas canvas) {

        // Started drawing a new trajectory. Must reset old trajectory.
        trajectory.clear();

        // TODO: Why am I asking canvas for time?
        // Start the new trajectory by appending the start point
        trajectory.addPoint(new Point3(x, y, trajectory.getAltitude(), canvas.getCurrentTime()));

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
