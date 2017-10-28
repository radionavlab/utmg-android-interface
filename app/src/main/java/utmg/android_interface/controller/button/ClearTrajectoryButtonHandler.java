package utmg.android_interface.controller.button;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class ClearTrajectoryButtonHandler implements View.OnClickListener {

    private final AbstractCanvas canvas;
    private final Trajectory trajectory;
    private final Context context;

    public ClearTrajectoryButtonHandler(
            final Trajectory trajectory,
            final AbstractCanvas canvas,
            Context context) {
        this.canvas = canvas;
        this.trajectory = trajectory;
        this.context = context;
    }

    @Override
    public void onClick(
            final View view) {
        // Clear the trajectory
        trajectory.clear();

        // Mark the canvas as invalid to force redraw
        canvas.invalidate();

        // Display a toast at the bottom of the screen
        Toast.makeText(this.context, "Cleared trajectory.", Toast.LENGTH_SHORT).show();

    }
}
