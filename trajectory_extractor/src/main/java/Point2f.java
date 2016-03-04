public class Point2f {

    private float x;
    private float y;

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
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
