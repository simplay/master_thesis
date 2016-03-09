import sun.applet.*;

public class Point2f {

    private double x;
    private double y;

    public Point2f(double x, double y) {
        this.x = x;
        this.y = y;
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
