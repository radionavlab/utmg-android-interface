package utmg.android_interface.controller.canvas;

import android.util.Log;

import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class DrawingStartTouchHandler implements IStartTouchHandler {

    private final Trajectory trajectory;
    private final Point3 POI;

    public DrawingStartTouchHandler(
            final Trajectory trajectory,
            final  Point3 POI) {
        this.trajectory = trajectory;
        this.POI =  POI;
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
