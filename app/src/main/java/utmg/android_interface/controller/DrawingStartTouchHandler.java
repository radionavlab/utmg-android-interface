package utmg.android_interface.controller;

import utmg.android_interface.model.entity.Quad;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class DrawingStartTouchHandler implements IStartTouchHandler {

    private final Quad quad;

    public DrawingStartTouchHandler(
            final Quad quad) {
        this.quad = quad;
    }

    @Override
    public void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas) {

        // Started drawing a new trajectory. Must reset old trajectory.
        quad.clearTrajectory();

        // TODO: Z should not be zero and why am I asking canvas for time?
        // Start the new trajectory by appending the start point
        quad.addPoint(new Point3(x, y, 0, canvas.getCurrentTime()));

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
