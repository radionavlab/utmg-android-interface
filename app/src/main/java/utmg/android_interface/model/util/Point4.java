package utmg.android_interface.model.util;

import org.ros.message.Time;

import java.util.ArrayList;


/**
 * Created by pwhitt24 on 10/25/17.
 */

public class Point4 {
    //3-space coordinates and yaw
    public final float x, y, z, w;

    //time of creation
    public final Time t;

    /**
     * Constructor
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @param w yaw
     * @param t time of creation
     */

    
    public Point4(
            final float x,
            final float y,
            final float z,
            final float w,
            final Time t) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        this.t = t;
    }

    /**
     * Constructor. Constructs ROS time from current system time.
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     */
    public Point4(
            final float x,
            final float y,
            final float z,
            final float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        t = new Time(Time.fromMillis(System.currentTimeMillis()));
    }


    /**
     * Creates new point at the origin with the current time.
     * @return A new point at the origin with the current time.
     */
    public static Point4 origin() {
        return new Point4(0, 0, 0, 0);
    }


    /**
     * Calculates the spacial distance between this point and another point.
     * @param other
     * @return
     */
    public float distanceTo(
            final Point4 other) {
        return (float) (Math.sqrt(Math.pow(other.x - this.x, 2) + Math.pow(other.y - this.y, 2) + Math.pow(other.z - this.z, 2)));
    }

    @Override
    public String toString() {
        return "Point4{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                ", t=" + t +
                '}';
    }


}