package utmg.android_interface.controller.canvas;

import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public interface IEndTouchHandler {
    void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas
    );
}
