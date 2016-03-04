import sun.applet.*;

public class Point2f {

    private float x;
    private float y;

    public Point2f(float x, float y) {
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
        int px = Math.round(x);
        int py = Math.round(y);

        return new Point2f(px, py);
    }

    public float u() {
        return x;
    }

    public float v() {
        return y;
    }

    public String toString() {
        return "(x,y)=("+ x +","+y+")";
    }
}
