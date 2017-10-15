package utmg.android_interface.model.entity;

/**
 * Created by jtb20 on 7/20/2017. Modified by Tucker Haydon on 10/14/2017.
 */

import utmg.android_interface.model.util.Point3;

/**
 * Abstract class that defines various object visible in the app. Each entity has a location.
 */
public abstract class AbstractEntity {

    /* The location of this entity */
    protected Point3 location;

    /**
     * Constructor.
     * @param initialLocation The initial location of the entity
     */
    public AbstractEntity(
            final Point3 initialLocation) {
        this.location = initialLocation;
    }

    /**
     * Constructor. Constructs an entity at the origin.
     */
    public AbstractEntity() {
        this(Point3.origin());
    }

    /**
     * Updates the location of the entity. Keeps the time the same.
     * @param newLocation The new location of the entity
     */
    public void setLocation(
            final Point3 newLocation) {
        this.location = newLocation;
    }

    /**
     * Updates the location of the entity. Keeps the time the same.
     * @param x The new x location
     * @param y The new y location.
     * @param z The new z location.
     */
    public void setLocation(
            final float x,
            final float y,
            final float z) {
        this.setLocation(new Point3(x, y, z, this.location.t));
    }

    /**
     * Moves the location of the entity by some amount. Keeps the time the same.
     * @param dx Amount to move x by
     * @param dy Amount to move y by
     * @param dz Amount to move z by
     */
    public void offsetLocation(
            final float dx,
            final float dy,
            final float dz) {
        this.location = location.offset(dx, dy, dz);
    }

    /**
     * Returns the value of location
     * @return The value of location
     */
    public Point3 getLocation(){
        return location;
    }
}
