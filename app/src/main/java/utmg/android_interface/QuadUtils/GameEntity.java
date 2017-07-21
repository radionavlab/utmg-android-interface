package utmg.android_interface.QuadUtils;

/**
 * Created by jtb20 on 7/20/2017.
 */

public class GameEntity {
    String name;
    int seq;
    boolean publish;
    Point3 location;

    public GameEntity(String name, Point3 location){
        this.location=location;
    }
    public GameEntity(String name){
        this.name=name;
    }

    public void setName(String name){
        this.name=name;
    }
    public void incSeq(){
        seq++;
    }
    public void setLocation(Point3 newLoc){
        location=newLoc;
    }
    public void setLocation(float x, float y, float z){
        location.setPoint(x,y,z);
    }
    public void setPublish(boolean publish){
        this.publish=publish;
    }
    public void offsetLocation(float dx, float dy, float dz){
        location.offset(dx,dy,dz);
    }

    public boolean getPublish(){
        return publish;
    }
    public String getName(){return name;}
    public int getSeq(){
        return seq;
    }
    public Point3 getLocation(){
        return location;
    }
}
