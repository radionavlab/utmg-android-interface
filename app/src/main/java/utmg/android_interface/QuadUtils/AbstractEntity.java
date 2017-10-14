package utmg.android_interface.QuadUtils;

/**
 * Created by jtb20 on 7/20/2017. Modified by Tucker Haydon on 10/14/2017.
 */

/**
 * Abstract class that defines various object visible in the app.
 * TODO: Understand this better and document it.
 */
public abstract class AbstractEntity {

    /* TODO: What is this? */
    protected int seq;

    /* TODO: What is this? */
    protected boolean publish;

    /* The location of this entity */
    protected Point3 location;

    /**
     * Constructor.
     * @param initialLocation The initial location of the entity
     */
    public AbstractEntity(
            final Point3 initialLocation) {
        this.location = initialLocation;
        this.seq = 0;
        this.publish = false;
    }

    /**
     * Constructor. Constructs an entity at the origin.
     */
    public AbstractEntity() {
        this(Point3.origin());
    }

    /**
     * Increments the seq by one
     */
    public void incrementSeq() {
        this.seq++;
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
     * Sets the publish variable
     * @param publish The new value of publish
     */
    public void setPublish(
            final boolean publish) {
        this.publish = publish;
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
     * Returns the value of publish
     * @return The value of publish
     */
    public boolean getPublish() {
        return publish;
    }

    /**
     * Returns the value of seq
     * @return The value of seq
     */
    public int getSeq(){
        return seq;
    }

    /**
     * Returns the value of location
     * @return The value of location
     */
    public Point3 getLocation(){
        return location;
    }
}
