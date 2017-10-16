package utmg.android_interface.controller;

import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

public interface IStartTouchHandler {

    void handle(
            final float x,
            final float y,
            final AbstractCanvas canvas
            );
}
