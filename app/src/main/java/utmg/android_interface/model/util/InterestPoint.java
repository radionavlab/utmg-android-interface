package utmg.android_interface.model.util;

/**
 * Created by tuckerhaydon on 12/7/17.
 */

public class InterestPoint {
    public float x, y;

    public InterestPoint() {
        this.x = Float.NaN;
        this.y = Float.NaN;
    }

    /**
     * Determines if the point is set. The default is NaN. If either value is NaN, the point is not defined.
     * @return
     */
    public boolean exists() {
        return this.x == this.x && this.y == this.y;
    }

}
