package utmg.android_interface.controller.canvas.obstacleHandlers;

import java.util.List;

import utmg.android_interface.activity.MainActivity;
import utmg.android_interface.controller.canvas.abstractHandlers.IEndTouchHandler;
import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.SelectedObstacle;
import utmg.android_interface.view.canvas.AbstractCanvas;
import utmg.android_interface.view.canvas.DrawingCanvas;

/**
 * Created by tuckerhaydon on 11/3/17.
 */

public class AddObstacleEndTouchHandler implements IEndTouchHandler {

    private final SelectedObstacle selectedObstacle;
    private final List<Obstacle> obstacles;
    private final DrawingCanvas drawingCanvas;
    private final MainActivity activity;

    public AddObstacleEndTouchHandler(
            final SelectedObstacle selectedObstacle,
            final List<Obstacle> obstacles,
            final DrawingCanvas canvas,
            final MainActivity activity) {
        this.selectedObstacle = selectedObstacle;
        this.obstacles = obstacles;
        this.drawingCanvas = canvas;
        this.activity = activity;
    }

    @Override
    public void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas) {
        final Point3 obstacleCenter = selectedObstacle.obstacle.getLocation();
        final float newRadius = (float) Math.sqrt(Math.pow((x - obstacleCenter.x),2) + Math.pow((y - obstacleCenter.y),2));
        selectedObstacle.obstacle.setRadius(newRadius);
        obstacles.add(selectedObstacle.obstacle);

        this.drawingCanvas.invalidate();

        activity.initCanvasHandlers();

    }
}
