package utmg.android_interface.model.util;

/**
 * Created by tuckerhaydon on 12/7/17.
 */

public class Point4 extends Point3 {

    public final float w;

    public Point4(
            final float x,
            final float y,
            final float z,
            final float w,
            final long t) {
        super(x, y, z, t);
        this.w = w;
    }

    public Point4(
            final float x,
            final float y,
            final float z,
            final float w) {
        super(x, y, z);
        this.w = w;
    }

    @Override
    public String toString() {
        return "Point4{" +
                "w=" + w +
                '}';
    }
}

