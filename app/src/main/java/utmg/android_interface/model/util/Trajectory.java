package utmg.android_interface.model.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtb20 on 7/20/2017. Modified by Tucker Haydon on 10/14/2017.
 */

/**
 * Class that represents a trajectory a quad should follow in 3-space. Data is represented as a list of Point3 objects in unit meters.
 * TODO: The trajectory class is the wrong class to maintain the current altitude of the slider
 */
public class Trajectory {

    /* List of Point3 objects representing the trajectory in 3-space */
    private List<Point4> points;

    /* A name to identify the trajectory */
    public final String name;

    /**
     * Constructor.
     * @param points A list of Point3 objects representing the trajectory.
     */
    public Trajectory(
            final List<Point4> points,
            final String name){
        this.points = points;
        this.name = name;
    }

    /**
     * Default constructor. Initializes trajectory as an empty ArrayList.
     */
    public Trajectory(
            final String name){
        this(new ArrayList<>(), name);
    }

    /**
     * Returns the list of points representing the trajectory
     * @return The list of pointrs representing the trajectory
     */
    public List<Point4> getPoints() {
        return points;
    }

    /**
     * Appends a point onto the end of the list
     * @param point A point to be appended to the end of the list
     */
    public void addPoint(Point4 point){
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
     * Clears all of the points from the trajectory.
     */
    public void clear() {
        this.points = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Trajectory{" +
                "points=" + points +
                ", name='" + name + '\'' +
                '}';
    }
}
