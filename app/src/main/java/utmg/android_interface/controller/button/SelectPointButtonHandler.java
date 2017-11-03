package utmg.android_interface.controller.button;

import android.view.View;

import utmg.android_interface.controller.canvas.IEndTouchHandler;
import utmg.android_interface.controller.canvas.IMoveTouchHandler;
import utmg.android_interface.controller.canvas.IStartTouchHandler;
import utmg.android_interface.controller.canvas.OnTouchEventDispatcher;
import utmg.android_interface.controller.canvas.POIStartTouchHandler;
import utmg.android_interface.model.util.POI;
import utmg.android_interface.view.canvas.AbstractCanvas;
import utmg.android_interface.view.canvas.DrawingCanvas;

/**
 * Created by pwhitt24 on 10/25/17.
 */

public class SelectPointButtonHandler implements View.OnClickListener {
    private final DrawingCanvas canvas;
    private final POI poi;

    public SelectPointButtonHandler(
            final DrawingCanvas canvas,
            final POI poi) {
        this.canvas = canvas;
        this.poi = poi;
    }

    @Override
    public void onClick(
            final View view) {
        canvas.setOnTouchListener(new OnTouchEventDispatcher(
                this.canvas,
                new POIStartTouchHandler(this.poi),
                new IMoveTouchHandler() {
                    @Override
                    public void handle(float x, float y, AbstractCanvas canvas) {
                        //do nothing
                    }
                },
                new IEndTouchHandler() {
                    @Override
                    public void handle(float x, float y, AbstractCanvas canvas) {
                        //do nothing
                    }
                }

        ));

    }
}

