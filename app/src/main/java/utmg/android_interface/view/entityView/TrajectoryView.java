package utmg.android_interface.view.entityView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.util.List;

import utmg.android_interface.model.util.Point3;
import utmg.android_interface.model.util.Point4;
import utmg.android_interface.model.util.Trajectory;
import utmg.android_interface.view.canvas.AbstractCanvas;

/**
 * Created by tuckerhaydon on 10/15/17.
 */

/**
 * View class for a trajectory.
 */
public class TrajectoryView extends AbstractEntityView{

    private final Trajectory trajectory;
    private final Paint paint;
    private final AbstractCanvas drawingCanvas;

    public TrajectoryView(
            final Trajectory trajectory,
            final Paint paint,
            final AbstractCanvas drawingCanvas) {
        this.trajectory = trajectory;
        this.paint = paint;
        this.drawingCanvas = drawingCanvas;
    }

    @Override
    public void draw(
            final Canvas canvas) {
        canvas.drawPath(this.composePath(), this.paint);
        drawArrows(canvas);
    }

    private void drawArrows(
            final Canvas canvas) {

        final float arrowLength = 50;

        Point4 lastPoint = this.trajectory.size() == 0 ? null : this.trajectory.getPoints().get(0);
        for(int i = 0; i < this.trajectory.getPoints().size(); i++) {

            final Point4 point = this.trajectory.getPoints().get(i);

            {
                // Only draw an arrow every 50 pixels
                final float X = this.drawingCanvas.toPixelsX(point.x);
                final float Y = this.drawingCanvas.toPixelsY(point.y);
                final float lastX = this.drawingCanvas.toPixelsX(lastPoint.x);
                final float lastY = this.drawingCanvas.toPixelsY(lastPoint.y);
                if (Math.sqrt(Math.pow((X - lastX), 2) + Math.pow((Y - lastY), 2)) < 50.0) {
                    continue;
                } else {
                    lastPoint = point;
                }
            }

            // If the angle doesn't exist, don't draw anything.
            if(point.w != point.w) {
                continue;
            }

            final float startX = this.drawingCanvas.toPixelsX(point.x);
            final float startY = this.drawingCanvas.toPixelsY(point.y);
            final float angle = point.w;
            final float endX = startX + arrowLength * (float)Math.cos(angle);
            final float endY = startY + arrowLength * (float)Math.sin(angle);

            // Draw arrow body
            {
                final Paint p = new Paint();
                p.setColor(Color.DKGRAY);
                p.setStrokeWidth(5);
                canvas.drawLine(startX, startY, endX, endY, p);
            }

            // Draw arrowhead
            {
                final Paint p = new Paint();
                p.setColor(Color.MAGENTA);
                p.setStrokeWidth(5);
                p.setStyle(Paint.Style.FILL);

                final float arrowHeadLength = 15;

                // Arrow head points
                float x1, x2, x3, y1, y2, y3;
                x1 = endX;
                y1 = endY;

                float theta = angle + (float)Math.PI/4;

                x2 = x1 - arrowHeadLength * (float)Math.cos(theta);
                y2 = y1 - arrowHeadLength * (float)Math.sin(theta);

                theta = angle - (float)Math.PI/4;

                x3 = x1 - arrowHeadLength * (float)Math.cos(theta);
                y3 = y1 - arrowHeadLength * (float)Math.sin(theta);


                canvas.drawLine(startX, startY, endX, endY, p);
                Path path = new Path();
                path.moveTo(x1, y1);
                path.lineTo(x2, y2);
                path.lineTo(x3, y3);
                path.close();

                canvas.drawPath(path, p);
            }
        }
    }

    private Path composePath() {
        final List<Point4> pathPoints = this.trajectory.getPoints();

        // If there are no points, return an empty path
        if(pathPoints.size() == 0) {
            return new Path();
        }

        final Path path = new Path();

        // Set the first point in the path
        final Point4 initialPointMeters = pathPoints.get(0);
        path.moveTo(this.drawingCanvas.toPixelsX(initialPointMeters.x), this.drawingCanvas.toPixelsY(initialPointMeters.y));


        // Add the rest of the points to the path
        for(int i = 1; i < pathPoints.size(); i++) {
            final Point4 metersPoint = pathPoints.get(i);
            path.lineTo(this.drawingCanvas.toPixelsX(metersPoint.x), this.drawingCanvas.toPixelsY(metersPoint.y));
        }
        return path;
    }
}
