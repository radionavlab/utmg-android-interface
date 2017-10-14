package utmg.android_interface.QuadUtils;

import java.util.ArrayList;

/**
 * Created by jtb20 on 7/20/2017.
 */

public class Quad extends AbstractEntity {

    Trajectory trajectory, optimizedTrajectory;
    int color;

    public Quad(String name, Trajectory trajectory, int color) {
        super(name);
        this.trajectory=trajectory;
        optimizedTrajectory=new Trajectory();
        this.color=color;
    }

    public Quad(String name, ArrayList<Point3> points, int color) {
        super(name);
        trajectory=new Trajectory(points);
        optimizedTrajectory=new Trajectory();
        this.color=color;
    }

    public Quad(String name, int color) {
        super(name);
        trajectory = new Trajectory();
        optimizedTrajectory=new Trajectory();
        this.color=color;
    }

    public void clearTrajectory(){
        trajectory.clear();
    }
    public void setColor(int c){
        color=c;
    }
    public void setOptimizedTrajectory(Trajectory traj) { optimizedTrajectory = traj; }
    public void addPoint(Point3 point){
        trajectory.addPoint(point);
    }
    public int size(){
        return trajectory.size();
    }
    public int getColor(){ return color;}
    public Trajectory getOptimizedTrajectory() { return optimizedTrajectory; }

    public Trajectory getTrajectory(){
        return trajectory;
    }
    @Override
    public boolean equals(Object other) {
        return other instanceof Quad && name.equals(((Quad)other).getName());
    }



}
