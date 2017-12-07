package utmg.android_interface.controller.button;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import utmg.android_interface.model.util.SelectedObstacle;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by pwhitt24 on 12/6/17.
 */

public class ClearObstacleButtonHandler implements View.OnClickListener {

    private final AbstractCanvas canvas;
    private final SelectedObstacle selectedObstacle;
    private final Context context;

    public ClearTrajectoryButtonHandler(
            final SelectedObstacle selectedObstacle,
            final AbstractCanvas canvas,
            Context context) {
        this.canvas = canvas;
        this.selectedObstacle = selectedObstacle;
        this.context = context;
    }

    @Override
    public void onClick(
            final View view) {
        // Clear the trajectory
        selectedObstacle.clear();

        // Mark the canvas as invalid to force redraw
        canvas.invalidate();

        // Display a toast at the bottom of the screen
        Toast.makeText(this.context, "Cleared obstacle.", Toast.LENGTH_SHORT).show();

    }


}
