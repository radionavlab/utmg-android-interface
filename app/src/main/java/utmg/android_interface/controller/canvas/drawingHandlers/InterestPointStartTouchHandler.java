package utmg.android_interface.controller.canvas.drawingHandlers;

import utmg.android_interface.activity.MainActivity;
import utmg.android_interface.controller.canvas.abstractHandlers.IStartTouchHandler;
import utmg.android_interface.model.util.InterestPoint;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 12/7/17.
 */

public class InterestPointStartTouchHandler implements IStartTouchHandler {

    private final InterestPoint interestPoint;
    private final MainActivity activity;

    public InterestPointStartTouchHandler(
            final InterestPoint interestPoint,
            final MainActivity activity) {
        this.interestPoint = interestPoint;
        this.activity = activity;
    }

    @Override
    public void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas) {
        this.interestPoint.x = x;
        this.interestPoint.y = y;

        activity.initCanvasHandlers();
    }
}
