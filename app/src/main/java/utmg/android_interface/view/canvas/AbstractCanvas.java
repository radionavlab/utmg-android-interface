package utmg.android_interface.view.canvas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.ros.message.Time;

import utmg.android_interface.model.util.Point3;

/***
 * Created by James on 7/27/2017. Modified by Tucker Haydon on 10/15/2017.
 */

public abstract class AbstractCanvas extends View {
    private Bitmap bitmap;
    protected Canvas canvas;
    private SharedPreferences preferences;

    private static final int DEFAULT_WIDTH_PIXELS = 1000;
    private static final int DEFAULT_HEIGHT_PIXELS = 1000;

    private static final float DEFAULT_WIDTH_METERS = 5.0f;
    private static final float DEFAULT_HEIGHT_METERS = 5.0f;

    private static final float CENTER_X_METERS = 0f;
    private static final float CENTER_Y_METERS = 0f;

    public AbstractCanvas(
            final Context context,
            final AttributeSet attrs) {
        super(context, attrs);

        this.preferences = context.getSharedPreferences("Pref", 0);
        this.bitmap = Bitmap.createBitmap(DEFAULT_WIDTH_PIXELS, DEFAULT_HEIGHT_PIXELS, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(this.bitmap);
    }

    /**
     * Returns the x coordinate of the center pixel on the screen
     *
     * @return The x coordinate of the center pixel on the screen
     */
    public final int getPixelCenterX() {
        return this.bitmap.getWidth() / 2;
    }

    /**
     * Returns the y coordinate of the center pixel on the screen
     *
     * @return The y coordinate of the center pixel on the screen
     */
    public final int getPixelCenterY() {
        return this.bitmap.getHeight() / 2;
    }

    /**
     * Maps a dimension from one coordinate space to another
     * @param dimension The value to be mapped
     * @param originalCoordinateCenter The center of the original space
     * @param originalCoordinateSpan The span of the original space
     * @param newCoordinateCenter The center of the new space
     * @param newCoordinateSpan The span of the new space
     * @return A dimension mapped to a new space
     */
    private float mapDimension(
            final float dimension,
            final float originalCoordinateCenter,
            final float originalCoordinateSpan,
            final float newCoordinateCenter,
            final float newCoordinateSpan) {
        return (dimension - originalCoordinateCenter) / originalCoordinateSpan * newCoordinateSpan + newCoordinateCenter;
    }

    /**
     * Converts a point from pixel units to meter units. Does not change the time or z coordinate.
     * TODO: Should z be changed?
     *
     * @param point A Point3
     * @return A point in meter units
     */
    public final Point3 toMeters(
            final Point3 point) {
        return new Point3(pixelsToMetersX(point.x), toMetersY(point.y), point.z, point.t);
    }

    /**
     * Maps an x-coordinate from pixel coordinate space to meter coordinate space
     * @param x The x coordinate of a pixel
     * @return The x coordinate in meters
     */
    public final float pixelsToMetersX(
            final float x) {
        return this.mapDimension(
                x,
                this.getPixelCenterX(),
                this.bitmap.getWidth(),
                CENTER_X_METERS,
                this.preferences.getFloat("width", DEFAULT_WIDTH_METERS));
    }

    /**
     * Maps an y-coordinate from pixel coordinate space to meter coordinate space
     * @param y The x coordinate of a pixel
     * @return The y coordinate in meters
     */
    public final float toMetersY(
            final float y) {
        return this.mapDimension(
                y,
                this.getPixelCenterY(),
                this.bitmap.getHeight(),
                CENTER_Y_METERS,
                this.preferences.getFloat("height", DEFAULT_HEIGHT_METERS));
    }

    /**
     * Converts a point to meters to a point in pixels. Does not change the time or z coordinate.
     * TODO: Should the Z value be changed?
     *
     * @param meterPoint A point in meter units
     * @return A point in pixel units
     */
    public final Point3 toPixels(
            final Point3 meterPoint) {
        return new Point3(toPixelsX(meterPoint.x), toPixelsY(meterPoint.y), meterPoint.z, meterPoint.t);
    }

    /**
     * Maps an x-coordinate from meters coordinate space to pixels coordinate space
     * @param x The x coordinate in meters
     * @return The x coordinate in pixels
     */
    public final float toPixelsX(
            final float x) {
        return this.mapDimension(
                x,
                CENTER_X_METERS,
                this.preferences.getFloat("width", DEFAULT_WIDTH_METERS),
                this.getPixelCenterX(),
                this.bitmap.getWidth());
    }

    /**
     * Maps a y-coordinate from meters coordinate space to pixels coordinate space
     * @param y The y coordinate in meters
     * @return The y coordinate in pixels
     */
    public final float toPixelsY(
            final float y) {
        return this.mapDimension(
                y,
                CENTER_Y_METERS,
                this.preferences.getFloat("width", DEFAULT_HEIGHT_METERS),
                this.getPixelCenterY(),
                this.bitmap.getHeight());
    }

    /**
     * Determines the current ROS time from the current system time
     * TODO: Why is thise here. Doesn't seem to belong
     *
     * @return The current ROS time
     */
    public final Time getCurrentTime() {
        // TODO: Where is the reference time?
        final long currentTimeMillis = System.currentTimeMillis();

        final Time time = new Time();
        time.secs = (int) (currentTimeMillis / 1000);
        time.nsecs = (int) ((currentTimeMillis % 1000) * 1000000);

        return time;
    }

    /**
     * This is called when the canvas size changes, perhaps due to a screen rotation
     *
     * @param newWidth  The new canvas width
     * @param newHeight The new canvas height
     * @param oldWidth  The old canvas width
     * @param oldHeight The old canvas height
     */
    @Override
    protected final void onSizeChanged(
            final int newWidth,
            final int newHeight,
            final int oldWidth,
            final int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);

        // your Canvas will draw onto the defined Bitmap
        this.bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(bitmap);
    }

    /**
     * This is called when the view should render its content.
     *
     * @param canvas The canvas object to render the content to.
     */
    @Override
    protected void onDraw(
            final Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * Clear the canvas.
     */
    public abstract void clearCanvas();
}
