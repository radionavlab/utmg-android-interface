package utmg.android_interface.model.entity;

/**
 * Created by jtb20 on 7/20/2017. Modified by Tucker Haydon on 10/14/2017.
 */

import utmg.android_interface.model.util.Point3;

/**
 * An obstacle naively modelled as a sphere with a constant radius.
 */
public class Obstacle extends AbstractEntity {

    /* Float representing the size of the object modelled as a sphere with radius r in meters. */
    private float radius;

    /* Name of the obstacle */
    private final String name;

    /**
     * Constructor.
     * @param name The name of the object
     * @param radius The radius of the object in meters.
     */
    public Obstacle(
            final String name,
            final Point3 center,
            final float radius) {
        super(center);
        this.name = name;
        this.radius = radius;
    }

    public void setRadius(
            final float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return this.radius;
    }
}
