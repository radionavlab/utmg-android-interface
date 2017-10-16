package utmg.android_interface.view.entitiyView;

import android.graphics.Canvas;
import android.graphics.Paint;

import utmg.android_interface.model.entity.Quad;

/**
 * Created by Tucker Haydon on 10/14/17.
 */
public class QuadView extends AbstractEntityView {

    private final Quad quad;
    private final Paint paint;

    public QuadView(
            final Quad quad,
            final int color) {
        this.quad = quad;

        /* Paint parameters */
        paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4f);
    }

    @Override
    public void draw(
            final Canvas canvas) {
        new TrajectoryView(quad.getTrajectory(), this.paint).draw(canvas);
        // TODO: Draw current quad position
    }
}
