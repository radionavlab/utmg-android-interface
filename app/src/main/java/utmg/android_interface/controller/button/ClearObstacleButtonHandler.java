package utmg.android_interface.controller.button;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.model.util.SelectedObstacle;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by pwhitt24 on 12/6/17.
 */

public class ClearObstacleButtonHandler implements View.OnClickListener {

    private final AbstractCanvas canvas;
    private final List<Obstacle> obstacles;
    private final Context context;



    public ClearObstacleButtonHandler(
            final List<Obstacle> obstacles,
            final AbstractCanvas canvas,
            Context context) {
        this.canvas = canvas;
        this.obstacles = obstacles;
        this.context = context;
    }

    @Override
    public void onClick(
            final View view) {

        obstacles.forEach(Obstacle::clear);
        canvas.invalidate();

        // Display a toast at the bottom of the screen
        Toast.makeText(this.context, "Cleared all obstacles.", Toast.LENGTH_SHORT).show();

    }


}
