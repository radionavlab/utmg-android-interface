package utmg.android_interface;

/**
 * Created by Nidhi on 4/28/17.
 */

public class zObject {

    public float z;

    public void setZ(float value) {
        this.z = value;
    }

    public float getZ() {
        return z;
    }

    public static zObject getInstance() {
        return zobj;
    }

    static zObject zobj = new zObject();

}
