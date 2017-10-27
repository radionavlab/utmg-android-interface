package utmg.android_interface.model.util;

import java.util.ArrayList;

/**
 * Created by pwhitt24 on 10/25/17.
 */

public class Point {
    private point;
    public final String name;
    
    public Point(
        final point,
        final String name){
        this.point = point;
        this.name = name;
    }


    public Point(
            final String name){
        this(new ArrayList<>(), name);
    }

    public getPoints(){return point;}

    public void addPoint(Point3 point){
        point.add(point);
    }

    public void setAltitude(
            final float altitude) {
        this.altitude = altitude;
    }

    public float getAltitude() {
        return this.altitude;
    }


    public void clear() {
        this.point = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "Point{" +
                "point=" + point +
                ", name='" + name + '\'' +
                '}';
    }
    
}
