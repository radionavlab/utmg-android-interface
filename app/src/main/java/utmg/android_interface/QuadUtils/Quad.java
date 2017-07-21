package utmg.android_interface.QuadUtils;

import java.util.ArrayList;

/**
 * Created by jtb20 on 7/20/2017.
 */

public class Quad extends GameEntity{

    Trajectory trajectory;

    @Deprecated
    public Quad(String name, Trajectory trajectory){
        super(name);
        this.trajectory=trajectory;
    }
    @Deprecated
    public Quad(String name, ArrayList<Point3> points){
        super(name);
        trajectory=new Trajectory(points);
    }
    public Quad(String name){
        super(name);
        trajectory = new Trajectory();
    }



    public void addPoint(Point3 point){
        trajectory.addPoint(point);
    }
    public int getCount(){
        return trajectory.getCount();
    }

    public Trajectory getTrajectory(){
        return trajectory;
    }
    @Override
    public boolean equals(Object other){
        return other instanceof Quad && name.equals(((Quad)other).getName());
    }


}
