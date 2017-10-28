package utmg.android_interface.controller.button;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/21/17.
 */

public class ClearAllTrajectoriesButtonHandler implements OnClickListener {

    private final List<Trajectory> trajectories;
    private final AbstractCanvas canvas;
    private final Context context;

    public ClearAllTrajectoriesButtonHandler(
            final List<Trajectory> trajectories,
            final AbstractCanvas canvas,
            final Context context) {
        this.trajectories = trajectories;
        this.canvas = canvas;
        this.context = context;
    }

    @Override
    public void onClick(
            final View view) {
        trajectories.forEach(Trajectory::clear);
        canvas.invalidate();

        // Display a toast at the bottom of the screen
        Toast.makeText(this.context, "Cleared all trajectories.", Toast.LENGTH_SHORT).show();
    }
}
