package utmg.android_interface.controller;

import android.support.design.widget.Snackbar;
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
            final AbstractCanvas canvas,
            final Trajectory trajectory) {
        this.canvas = canvas;
        this.trajectory = trajectory;
    }

    @Override
    public void onClick(
            final View view) {
        trajectory.clear();
        canvas.invalidate();

        // Display a snackbar at the bottom of the screen
        Snackbar.make(view, "Cleared trajectory.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
