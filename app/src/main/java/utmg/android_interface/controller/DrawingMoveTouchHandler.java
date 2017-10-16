package utmg.android_interface.controller;

import utmg.android_interface.model.entity.Quad;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class DrawingMoveTouchHandler implements IMoveTouchHandler {

    private final Quad quad;

    public DrawingMoveTouchHandler(
            final Quad quad) {
        this.quad = quad;
    }

    @Override
    public void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas) {

        // TODO: Z should not be zero and why am I asking canvas for time?
        // Append the movement point
        quad.addPoint(new Point3(x, y, 0, canvas.getCurrentTime()));

        // TODO: Implement some sort of tolerance. No need to add a point for every movement.

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
