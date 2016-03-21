package datastructures;

public class Point2d {

    private double x;
    private double y;

    public Point2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2d copy() {
        return new Point2d(x,y);
    }

    public Point2d sub(Point2d other) {
        x -= other.x();
        y -= other.y();
        return this;
    }

    public Point2d div_by(double f) {
        x /= f;
        y /= f;
        return this;
    }

    public double x() {
        return u();
    }

    public double y() {
        return v();
    }

    public int iU() {
        return (int) x;
    }

    public int iV() {
        return (int) y;
    }

    public Point2d rounded() {
        int px = (int) Math.round(x);
        int py = (int) Math.round(y);

        return new Point2d(px, py);
    }

    public double length() {
        return Math.sqrt(length_squared());
    }

    public double length_squared() {
        return x*x + y*y;
    }

    public double u() {
        return x;
    }

    public double v() {
        return y;
    }

    public String toString() {
        return "(x,y)=("+ x +","+y+")";
    }

    public String toOutputString() {
        return x + " " + y;
    }
}
