package utmg.android_interface.QuadUtils;

import org.ros.message.Time;

/**
 * Created by jtb20 on 7/20/2017. Modified by Tucker Haydon on 10/14/2017.
 */

/**
 * Immutable data structure containing a 3-tuple coordinate and a time of creation.
 */
public class Point3 {

    /* Coordinate in 3-space */
    public final float x, y, z;

    /* Time of creation */
    public final Time t;

    /**
     * Constructor
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param t time of creation
     */
    public Point3(
            final float x,
            final float y,
            final float z,
            final Time t) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.t = t;
    }

    /**
     * Constructor. Constructs ROS time from current system time.
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public Point3(
            final float x,
            final float y,
            final float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        t = new Time(Time.fromMillis(System.currentTimeMillis()));
    }

    /**
     * Creates new point at the origin with the current time.
     * @return A new point at the origin with the current time.
     */
    public static Point3 origin() {
        return new Point3(0, 0, 0);
    }

    /**
     * Constructs and returns a point that is offset from the current point spacially but at the same moment in time.
     * @param dx
     * @param dy
     * @param dz
     * @return A point that is offset from the current point spacially but at the same moment in time.
     */
    public Point3 offset(
            final float dx,
            final float dy,
            final float dz) {

        return new Point3(
                this.x + dx,
                this.y + dy,
                this.z + dz,
                this.t);
    }

    /**
     * Calculates the spacial distance between this point and another point.
     * @param other
     * @return
     */
    public float distanceTo(
            final Point3 other) {
        return (float) (Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2) + Math.pow(other.z - this.z, 2)));
    }
}
