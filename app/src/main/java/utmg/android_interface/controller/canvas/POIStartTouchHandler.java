package utmg.android_interface.controller.canvas;

import utmg.android_interface.activity.MainActivity;
import utmg.android_interface.model.util.POI;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by Paige on 11/2/2017.
 */




public class POIStartTouchHandler implements IStartTouchHandler {
    private final POI poi;
    private final MainActivity activity;

    public POIStartTouchHandler(
            POI poi,
            final MainActivity activity) {
        this.poi = poi;
        this.activity = activity;
    }

    @Override
    public void handle(float x, float y, AbstractCanvas canvas) {
        this.poi.x = x;
        this.poi.y = y;


        activity.initCanvasHandlers();
    }
}
