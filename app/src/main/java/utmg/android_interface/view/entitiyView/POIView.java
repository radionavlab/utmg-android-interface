package utmg.android_interface.view.entitiyView;

import android.graphics.Canvas;
import android.graphics.Paint;

import utmg.android_interface.model.util.POI;

/**
 * Created by pwhitt24 on 11/3/17.
 */

public class POIView extends AbstractEntityView{

    private final POI poi;
    private final Paint paint;


    public POIView(POI poi, Paint paint) {
        this.poi = poi;
        this.paint = paint;
    }




    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(
                poi.x,
                poi.y,
                14,
                paint);

        canvas.drawLine(
                poi.x-17,
                poi.y,
                poi.x+17,
                poi.y,
                paint);

        canvas.drawLine(
                poi.x,
                poi.y-17,
                poi.x,
                poi.y+17,
                paint);
    }


}
