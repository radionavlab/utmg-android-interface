package utmg.android_interface.controller.button;

import android.view.View;

import utmg.android_interface.activity.MainActivity;
import utmg.android_interface.controller.canvas.abstractHandlers.OnTouchEventDispatcher;
import utmg.android_interface.controller.canvas.drawingHandlers.InterestPointEndTouchHandler;
import utmg.android_interface.controller.canvas.drawingHandlers.InterestPointMoveTouchHandler;
import utmg.android_interface.controller.canvas.drawingHandlers.InterestPointStartTouchHandler;
import utmg.android_interface.model.util.InterestPoint;
import utmg.android_interface.view.canvas.DrawingCanvas;

/**
 * Created by tuckerhaydon on 12/7/17.
 */

public class AddInterestPointButtonHandler implements View.OnClickListener {

    private final DrawingCanvas canvas;
    private final MainActivity activity;
    private final InterestPoint interestPoint;

    public AddInterestPointButtonHandler(
            final DrawingCanvas canvas,
            final InterestPoint interestPoint,
            final MainActivity activity
            ) {
        this.canvas = canvas;
        this.activity = activity;
        this.interestPoint = interestPoint;
    }


    @Override
    public void onClick(
            final View view) {
        this.canvas.setOnTouchListener(new OnTouchEventDispatcher(
                this.canvas,
                new InterestPointStartTouchHandler(interestPoint, activity),
                new InterestPointMoveTouchHandler(),
                new InterestPointEndTouchHandler()
        ));
    }
}
