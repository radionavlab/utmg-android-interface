package utmg.android_interface.controller.button;

import android.view.View;

import java.util.List;

import utmg.android_interface.activity.MainActivity;
import utmg.android_interface.controller.canvas.AddObstacleEndTouchHandler;
import utmg.android_interface.controller.canvas.AddObstacleMoveTouchHandler;
import utmg.android_interface.controller.canvas.AddObstacleStartTouchHandler;
import utmg.android_interface.controller.canvas.OnTouchEventDispatcher;
import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.model.util.SelectedObstacle;
import utmg.android_interface.view.canvas.DrawingCanvas;

/**
 * Created by tuckerhaydon on 11/3/17.
 */

public class AddObstacleButtonHandler implements View.OnClickListener {

    private final DrawingCanvas drawingCanvas;
    private final List<Obstacle> obstacles;
    private final SelectedObstacle selectedObstacle;
    private final MainActivity activity;

    public AddObstacleButtonHandler(
            final DrawingCanvas drawingCanvas,
            final List<Obstacle> obstacles,
            final SelectedObstacle selectedObstacle,
            final MainActivity activity) {
        this.drawingCanvas = drawingCanvas;
        this.obstacles = obstacles;
        this.selectedObstacle = selectedObstacle;
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
        drawingCanvas.setOnTouchListener(new OnTouchEventDispatcher(
                drawingCanvas,
                new AddObstacleStartTouchHandler(selectedObstacle, drawingCanvas),
                new AddObstacleMoveTouchHandler(selectedObstacle, drawingCanvas),
                new AddObstacleEndTouchHandler(selectedObstacle, obstacles, drawingCanvas, activity)));

    }
}
