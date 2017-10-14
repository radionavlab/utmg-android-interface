package utmg.android_interface.QuadUtils;

/**
 * Created by jtb20 on 7/20/2017.
 */

public class Obstacle extends AbstractEntity {
    float size;
    public Obstacle(String name){
        super(name);
        size=1;
    }
    public Obstacle(String name, float size){
        super(name);
        this.size=size;
    }
    public void setSize(float size){
        this.size=size;
    }
    public float getSize(){
        return size;
    }
}
