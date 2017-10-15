package utmg.android_interface.QuadUtils;

import utmg.android_interface.model.AbstractEntity;
import utmg.android_interface.model.Point3;

/**
 * Created by jtb20 on 7/20/2017.
 */

public class Sword extends AbstractEntity {
    public Sword(String name){
        super(name);
    }
    public Sword(String name, Point3 location){
        super(name,location);
    }
}
