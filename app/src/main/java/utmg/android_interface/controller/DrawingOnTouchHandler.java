package utmg.android_interface.controller;

import android.view.MotionEvent;
import android.view.View;

import utmg.android_interface.canvas.DrawingCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public class DrawingOnTouchHandler implements View.OnTouchListener {

    private final DrawingCanvas canvas;

    public DrawingOnTouchHandler(
            final DrawingCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public boolean onTouch(
            final View view,
            final MotionEvent event) {

        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                canvas.startTouch(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                canvas.moveTouch(x, y);
                break;
            case MotionEvent.ACTION_UP:
                canvas.endTouch(x, y);
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
