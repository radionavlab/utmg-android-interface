package utmg.android_interface.view.entityView;

import android.graphics.Canvas;
import android.graphics.Paint;

import utmg.android_interface.model.util.InterestPoint;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 12/7/17.
 */

public class InterestPointView extends AbstractEntityView {

    private final InterestPoint interestPoint;
    private final Paint paint;

    public InterestPointView(
            final InterestPoint interestPoint,
            final Paint paint) {
        this.interestPoint = interestPoint;
        this.paint = paint;
    }

    @Override
    public void draw(
            final Canvas canvas) {
        final float radius = 20;

        // Draw target center
        canvas.drawCircle(interestPoint.x, interestPoint.y, radius, paint);

        // Draw target lines
        canvas.drawLine(interestPoint.x, interestPoint.y - 1.5f*radius, interestPoint.x, interestPoint.y + 1.5f*radius, paint);
        canvas.drawLine(interestPoint.x - 1.5f*radius, interestPoint.y, interestPoint.x + 1.5f*radius, interestPoint.y, paint);
    }
}
