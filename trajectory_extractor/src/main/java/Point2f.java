import sun.applet.*;

public class Point2f {

    private double x;
    private double y;

    public Point2f(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2f copy() {
        return new Point2f(x,y);
    }

    public Point2f sub(Point2f other) {
        x -= other.x();
        y -= other.y();
        return this;
    }

    public Point2f div_by(double f) {
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

    public Point2f rounded() {
        int px = (int) Math.round(x);
        int py = (int) Math.round(y);

        return new Point2f(px, py);
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
