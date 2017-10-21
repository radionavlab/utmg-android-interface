package utmg.android_interface.controller.button;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class ClearTrajectoryButtonHandler implements View.OnClickListener {

    private final AbstractCanvas canvas;
    private final Trajectory trajectory;

    public ClearTrajectoryButtonHandler(
            final Trajectory trajectory,
            final AbstractCanvas canvas) {
        this.canvas = canvas;
        this.trajectory = trajectory;
    }

    @Override
    public void onClick(
            final View view) {
        // Clear the trajectory
        trajectory.clear();

        Log.i("Info", "Clearing Trajectory:" + trajectory.name);

        // Mark the canvas as invalid to force redraw
        canvas.invalidate();

        // Display a snackbar at the bottom of the screen
        Snackbar.make(view, "Cleared trajectory.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
