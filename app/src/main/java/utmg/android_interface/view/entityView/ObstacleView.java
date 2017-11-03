package utmg.android_interface.view.entityView;

import android.graphics.Canvas;
import android.graphics.Paint;

import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 11/3/17.
 */

public class ObstacleView extends AbstractEntityView {

    private final Obstacle obstacle;
    private final Paint paint;
    private final AbstractCanvas drawingCanvas;

    public ObstacleView(
            final Obstacle obstacle,
            final Paint paint,
            final AbstractCanvas drawingCanvas) {
        this.obstacle = obstacle;
        this.paint = paint;
        this.drawingCanvas = drawingCanvas;
    }

    @Override
    public void draw(
            final Canvas canvas) {
        canvas.drawCircle(
                obstacle.getLocation().x,
                obstacle.getLocation().y,
                obstacle.getRadius(),
                this.paint);
    }
}
