package utmg.android_interface.view.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import utmg.android_interface.view.entitiyView.AbstractEntityView;
import utmg.android_interface.view.entitiyView.TrajectoryView;

/**
 * Unknown creator. Modified by Tucker Haydon on 10/15/2017
 */

public class DrawingCanvas extends AbstractCanvas {

    private List<AbstractEntityView> entityViews = new ArrayList<>();

    public DrawingCanvas(
            final Context context) {
        super(context);
    }

    @Override
    protected void onDraw(
            final Canvas canvas) {
        super.onDraw(canvas);
        for(AbstractEntityView entityView: entityViews) {
            entityView.draw(canvas);
        }
    }

    public void addEntityView(
            final AbstractEntityView entityView) {
        this.entityViews.add(entityView);
    }
}
