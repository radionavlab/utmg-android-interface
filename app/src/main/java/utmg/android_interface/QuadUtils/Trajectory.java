package utmg.android_interface.QuadUtils;

import java.util.ArrayList;

/**
 * Created by jtb20 on 7/20/2017.
 */

public class Trajectory {
    ArrayList<Point3> points;
    public Trajectory(ArrayList<Point3> points){
        this.points=points;
    }
    public Trajectory(){
        points=new ArrayList<>();
    }
    public void setPoints(ArrayList<Point3> points){
        this.points=points;
    }
    public ArrayList<Point3> getPoints(){
        return points;
    }
    public void addPoint(Point3 point){
        points.add(point);
    }
    public int getCount(){
        return points.size();
    }
}
