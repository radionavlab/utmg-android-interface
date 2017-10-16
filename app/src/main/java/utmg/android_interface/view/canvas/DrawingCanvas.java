package utmg.android_interface.view.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import utmg.android_interface.model.entity.Obstacle;
import utmg.android_interface.model.util.Point3;
import utmg.android_interface.view.entitiyView.QuadView;

/**
 * Unknown creator. Modified by Tucker Haydon on 10/15/2017
 */

public class DrawingCanvas extends AbstractCanvas {

    private List<QuadView> quadViews;

    public DrawingCanvas(
            final Context context,
            final AttributeSet attrs,
            final View.OnTouchListener onTouchListener) {
        super(context, attrs, onTouchListener);

        // quads=(ArrayList<Quad>)DataShare.retrieve("quads");
        // obstacles=(ArrayList<Obstacle>)DataShare.retrieve("obstacles");
    }

    @Override
    protected void onDraw(
            final Canvas canvas) {
        for(QuadView quadView: quadViews) {
            quadView.drawTrajectory(canvas);
        }
    }

    @Override
    public void clearCanvas() {
        // TODO: How do I clear this?
    }

    public void addQuadView(
            final QuadView quadView) {
        this.quadViews.add(quadView);
    }

}
