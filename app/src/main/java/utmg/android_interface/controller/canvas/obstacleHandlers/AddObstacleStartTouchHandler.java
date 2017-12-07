package utmg.android_interface.controller.canvas.obstacleHandlers;

import android.graphics.Color;
import android.graphics.Paint;

import utmg.android_interface.controller.canvas.abstractHandlers.IStartTouchHandler;
import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.SelectedObstacle;
import utmg.android_interface.view.canvas.AbstractCanvas;
import utmg.android_interface.view.canvas.DrawingCanvas;
import utmg.android_interface.view.entityView.ObstacleView;

/**
 * Created by tuckerhaydon on 11/3/17.
 */

public class AddObstacleStartTouchHandler implements IStartTouchHandler {

    private final SelectedObstacle selectedObstacle;
    private final DrawingCanvas drawingCanvas;

    public AddObstacleStartTouchHandler(
            final SelectedObstacle selectedObstacle,
            final DrawingCanvas drawingCanvas) {
        this.selectedObstacle = selectedObstacle;
        this.drawingCanvas = drawingCanvas;
    }

    @Override
    public void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas) {
        this.selectedObstacle.obstacle = new Obstacle("", new Point3(x, y, 0), 0);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        this.drawingCanvas.addEntityView(new ObstacleView(selectedObstacle.obstacle, paint, this.drawingCanvas));
        this.drawingCanvas.invalidate();
    }
}
