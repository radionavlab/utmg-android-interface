package utmg.android_interface.model.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtb20 on 7/20/2017. Modified by Tucker Haydon on 10/14/2017.
 */

/**
 * Class that represents a trajectory a quad should follow in 3-space. Data is represented as a list of Point3 objects.
 */
public class Trajectory {

    /* List of Point3 objects representing the trajectory in 3-space */
    private List<Point3> points;

    /* Altitude specified by slider in meters */
    private float altitude;

    /**
     * Constructor.
     * @param points A list of Point3 objects representing the trajectory.
     */
    public Trajectory(
            final List<Point3> points){
        this.points = points;
        this.altitude = 0;
    }

    /**
     * Default constructor. Initializes trajectory as an empty ArrayList.
     */
    public Trajectory(){
        this(new ArrayList<>());
    }

    /**
     * Static factory method that generates an empty trajectory.
     * @return A trajectory with no points.
     */
    public static Trajectory noTrajectory() {
        return new Trajectory();
    }

    /**
     * Returns the list of points representing the trajectory
     * @return The list of pointrs representing the trajectory
     */
    public List<Point3> getPoints() {
        return points;
    }

    /**
     * Appends a point onto the end of the list
     * @param point A point to be appended to the end of the list
     */
    public void addPoint(Point3 point){
        points.add(point);
    }

    /**
     * Returns the number of points in the trajectory
     * @return The number of points in the trajectory
     */
    public int size(){
        return points.size();
    }

    /**
     * Sets the current altitude of the trajectory.
     * @param altitude
     */
    public void setAltitude(
            final float altitude) {
        this.altitude = altitude;
    }

    /**
     * Returns the current altitude of the trajectory.
     * @return
     */
    public float getAltitude() {
        return this.altitude;
    }

    /**
     * Clears all of the points from the trajectory.
     */
    public void clear() {
        this.points = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Trajectory{" +
                "points=" + points +
                '}';
    }
}
