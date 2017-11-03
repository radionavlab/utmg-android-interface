package utmg.android_interface.controller.canvas;

import android.util.Log;

import utmg.android_interface.model.util.POI;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.Point4;
import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class DrawingStartTouchHandler implements IStartTouchHandler {

    private final Trajectory trajectory;

    private final POI poi;

//    private final Point4 POI;

    public DrawingStartTouchHandler(
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



        float w = (float) Math.atan(poi.y-y / poi.x-x);


        // Started drawing a new trajectory. Must reset old trajectory.
        trajectory.clear();

        // TODO: Why am I asking canvas for time?
        // Start the new trajectory by appending the start point
        trajectory.addPoint(new Point4(x, y, trajectory.getAltitude(), w, canvas.getCurrentTime()));

        // Invalidate the canvas to force redraw
        canvas.invalidate();
    }
}
