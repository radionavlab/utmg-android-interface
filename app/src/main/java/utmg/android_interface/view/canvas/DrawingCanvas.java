package utmg.android_interface.view.canvas;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import utmg.android_interface.view.entitiyView.TrajectoryView;

/**
 * Unknown creator. Modified by Tucker Haydon on 10/15/2017
 */

public class DrawingCanvas extends AbstractCanvas {

    private List<TrajectoryView> trajectoryViews = new ArrayList<>();

    public DrawingCanvas(
            final Context context,
            final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(
            final Canvas canvas) {
        super.onDraw(canvas);
        for(TrajectoryView trajectoryView: trajectoryViews) {
            trajectoryView.draw(canvas);
        }
    }

    @Override
    public void clearCanvas() {
        // TODO: How do I clear this?
    }

    public void addTrajectoryView(
            final TrajectoryView trajectoryView) {
        this.trajectoryViews.add(trajectoryView);
    }

}
