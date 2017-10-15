package utmg.android_interface.model;

import java.util.ArrayList;
import java.util.List;

import utmg.android_interface.QuadUtils.Trajectory;

/**
 * Created by tuckerhaydon on 10/14/17.
 */

/**
 * Model class of a quadcopter. A quadcopter has a name and a trajectory it must follow. It inherits a position attribute from AbstractEntity
 */
public class Quad extends AbstractEntity {

    /* A unique name for the quad */
    private final String name;

    /* The trajectory the quad should follow */
    Trajectory trajectory;

    /* An optimized trajectory a quad should follow. */
    Trajectory optimizedTrajectory;

    /**
     * Constructor.
     * @param name A unique name for the quad
     * @param trajectory A trajectory for the quad to follow
     */
    public Quad(
            final String name,
            final Trajectory trajectory) {
        super();
        this.name = name;
        this.trajectory = trajectory;
        optimizedTrajectory = Trajectory.noTrajectory();
    }

    /**
     * Constructor
     * @param name A unique name for the quad
     * @param points A list of Point3 representing a trajectory for the quad to follow
     */
    public Quad(
            final String name,
            final List<Point3> points) {
        this(name, new Trajectory(points));
    }

    /**
     * Constructor. Trajectory is empty.
     * @param name A unique name for the quad.
     */
    public Quad(
            final String name) {
        this(name, Trajectory.noTrajectory());
    }

    /**
     * Clears the trajectory.
     */
    public void clearTrajectory() {
        this.trajectory = Trajectory.noTrajectory();
    }

    /**
     * Set the optimizedTrajectory variable to the parameter value
     * @param optimizedTrajectory A trajectory
     */
    public void setOptimizedTrajectory(
            final Trajectory optimizedTrajectory) {
        this.optimizedTrajectory = optimizedTrajectory;
    }

    /**
     * Appends a Point3 to the end of the trajectory
     * @param point A Point3 to be appended
     */
    public void addPoint(
            final Point3 point){
        this.trajectory.addPoint(point);
    }

}
