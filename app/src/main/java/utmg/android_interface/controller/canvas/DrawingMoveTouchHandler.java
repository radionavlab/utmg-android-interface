package utmg.android_interface.controller.canvas;

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

    private final POI poi;

    public DrawingMoveTouchHandler(
            final Trajectory trajectory,
            final POI poi) {
        this.trajectory = trajectory;
        this.poi = poi;


    }

    @Override
    public void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas) {


        float w = (float) Math.atan(poi.y-y/poi.x-x);


        // TODO: Why am I asking canvas for time?
        // Append the movement point
        trajectory.addPoint(new Point4(x, y, trajectory.getAltitude(), w, canvas.getCurrentTime()));

        // TODO: Implement some sort of tolerance. No need to add a point for every movement.
        // TODO: Prevent trajectory from leaving arena

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
