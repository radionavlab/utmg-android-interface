package utmg.android_interface.controller.canvas;

import android.graphics.Color;
import android.graphics.Paint;

import utmg.android_interface.activity.MainActivity;
import utmg.android_interface.model.util.POI;
import utmg.android_interface.view.canvas.AbstractCanvas;
import utmg.android_interface.view.canvas.DrawingCanvas;
import utmg.android_interface.view.entityView.POIView;

/**
 * Created by Paige on 11/2/2017.
 */




public class POIStartTouchHandler implements IStartTouchHandler {
    private final POI poi;
    private final MainActivity activity;
    private DrawingCanvas canvas;

    public POIStartTouchHandler(
            POI poi,
            final MainActivity activity,
            DrawingCanvas canvas) {
        this.poi = poi;
        this.activity = activity;
        this.canvas = canvas;
    }

    @Override
    public void handle(float x, float y, AbstractCanvas canvas) {
        this.poi.x = x;
        this.poi.y = y;

        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);


        this.canvas.addEntityView(new POIView(poi, paint));
        activity.initCanvasHandlers();
    }
}
