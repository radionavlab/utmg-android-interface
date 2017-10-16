package utmg.android_interface.view.entitiyView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.List;

import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.Trajectory;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

/**
 * View class for a trajectory.
 */
public class TrajectoryView extends AbstractEntityView{

    private final Trajectory trajectory;
    private final Paint paint;

    public TrajectoryView(
            final Trajectory trajectory,
            final Paint paint) {
        this.trajectory = trajectory;
        this.paint = paint;
    }

    @Override
    public void draw(
            final Canvas canvas) {
        canvas.drawPath(this.composePath(), this.paint);
    }

    private Path composePath() {
        final Path path = new Path();
        final List<Point3> pathPoints = this.trajectory.getPoints();

        // Set the first point in the path
        final Point3 initialPoint = pathPoints.get(0);
        path.moveTo(initialPoint.x, initialPoint.y);

        for(int i = 1; i < pathPoints.size(); i++) {
            final Point3 point = pathPoints.get(0);
            path.lineTo(point.x, point.y);
        }

        return path;
    }
}
