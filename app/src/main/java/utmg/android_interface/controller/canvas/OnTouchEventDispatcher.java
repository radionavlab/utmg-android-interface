package utmg.android_interface.controller.canvas;

import android.view.MotionEvent;
import android.view.View;

import utmg.android_interface.controller.canvas.IEndTouchHandler;
import utmg.android_interface.controller.canvas.IMoveTouchHandler;
import utmg.android_interface.controller.canvas.IStartTouchHandler;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class OnTouchEventDispatcher implements View.OnTouchListener {

    private final AbstractCanvas canvas;
    private final IStartTouchHandler startTouchHandler;
    private final IMoveTouchHandler moveTouchHandler;
    private final IEndTouchHandler endTouchHandler;

    public OnTouchEventDispatcher(
            final AbstractCanvas canvas,
            final IStartTouchHandler startTouchHandler,
            final IMoveTouchHandler moveTouchHandler,
            final IEndTouchHandler endTouchHandler) {
        this.canvas = canvas;
        this.startTouchHandler = startTouchHandler;
        this.moveTouchHandler = moveTouchHandler;
        this.endTouchHandler = endTouchHandler;
    }

    @Override
    public boolean onTouch(
            final View view,
            final MotionEvent event) {

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouchHandler.handle(x, y, canvas);
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouchHandler.handle(x, y, canvas);
                break;
            case MotionEvent.ACTION_UP:
                endTouchHandler.handle(x, y, canvas);
                break;
            default:
                // Do nothing
                break;
        }

        // Redraw
        canvas.invalidate();

        // Event has been handled.
        return true;
    }
}
