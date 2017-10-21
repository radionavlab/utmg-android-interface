package utmg.android_interface.controller.button;

import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.List;

import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class ClearAllTrajectoriesButtonHandler implements OnClickListener {

    private final List<Trajectory> trajectories;
    private final AbstractCanvas canvas;

    public ClearAllTrajectoriesButtonHandler(
            final List<Trajectory> trajectories,
            final AbstractCanvas canvas) {
        this.trajectories = trajectories;
        this.canvas = canvas;
    }

    @Override
    public void onClick(
            final View view) {
        trajectories.forEach(Trajectory::clear);
        canvas.invalidate();

        // Display a snackbar at the bottom of the screen
        Snackbar.make(view, "Cleared all trajectories.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
