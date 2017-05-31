package utmg.android_interface;

import android.graphics.Color;

public class Thing {

    private double x = 0;
    private double y = 0;
    private double z = 0;
    private int quadColour = Color.BLACK;

    public void setX(double value) {
        this.x = value;
    }
    public void setY(double value) {
        this.y = value;
    }
    public void setZ(double value) {
        this.z = value;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }

    public void setQuadColour(int cl) { this.quadColour = cl; }
    public int getQuadColour() { return quadColour; }

//    public void setQuadColour(int cl) { this.quadColour = cl; }
//    public int getQuadColour() { return quadColour; }
}
