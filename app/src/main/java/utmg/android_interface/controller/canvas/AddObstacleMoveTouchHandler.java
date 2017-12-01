package utmg.android_interface.controller.canvas;

import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.SelectedObstacle;
import utmg.android_interface.view.canvas.AbstractCanvas;
import utmg.android_interface.view.canvas.DrawingCanvas;

/**
 * Created by tuckerhaydon on 11/3/17.
 */

public class AddObstacleMoveTouchHandler implements IMoveTouchHandler {

    private final SelectedObstacle selectedObstacle;
    private final DrawingCanvas drawingCanvas;

    public AddObstacleMoveTouchHandler(
            SelectedObstacle selectedObstacle, DrawingCanvas drawingCanvas) {
        this.selectedObstacle = selectedObstacle;
        this.drawingCanvas = drawingCanvas;
    }

    @Override
    public void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas) {

        final Point3 obstacleCenter = selectedObstacle.obstacle.getLocation();
        final float newRadius = (float) Math.sqrt(Math.pow((x - obstacleCenter.x),2) + Math.pow((y - obstacleCenter.y),2));
        selectedObstacle.obstacle.setRadius(newRadius);

        drawingCanvas.invalidate();

    }
}
