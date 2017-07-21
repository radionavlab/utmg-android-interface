package utmg.android_interface.QuadUtils;

import org.ros.message.Time;

/**
 * Created by jtb20 on 7/20/2017.
 */

public class Point3 {
    private float x,y,z;
    private Time t;
    public Point3(float x, float y, float z, Time t){
        this.x=x;
        this.y=y;
        this.z=z;
        this.t=t;
    }
    public Point3(float x, float y, float z){
        this.x=x;
        this.y=y;
        this.z=z;
        t=new Time(Time.fromMillis(System.currentTimeMillis()));
    }
    public float getX(){return x;}
    public float getY(){return y;}
    public float getZ(){return z;}
    public Time getTime(){return t;}
    public void setX(float x){this.x=x;}
    public void setY(float y){this.y=y;}
    public void setZ(float z){this.z=z;}
    public void offset(float dx, float dy, float dz){
        x+=dx;
        y+=dy;
        z+=dz;
    }
    public float distanceTo(Point3 other){
        return (float)Math.sqrt(Math.pow(other.getX()-x,2)+Math.pow(other.getY()-y,2)+Math.pow(other.getZ()-z,2));
    }
    public void setTime(Time time){this.t=time;}
}
