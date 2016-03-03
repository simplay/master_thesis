/**
 * Created by simplay on 03/03/16.
 */
public class FlowField {

    private String type;
    private int m;
    private int n;
    public final static String FORWARD_FLOW = "fw";
    public final static String BACKWARD_FLOW = "bw";

    private float[][] u_dir;
    private float[][] v_dir;

    public FlowField(int m, int n, String type) {
        this.m = m;
        this.n = n;
        this.type = type;
        this.u_dir = new float[m][n];
        this.v_dir = new float[m][n];
    }

    public float[][] getU() {
        return u_dir;
    }

    public float[][] getV() {
        return v_dir;
    }

    public void setRow(int row_idx, float[] u_row, float[] v_row) {
        u_dir[row_idx] = u_row;
        v_dir[row_idx] = v_row;
    }

    public boolean isForwardFlow() {
        return type.equals(FORWARD_FLOW);
    }

    public boolean isBackwardFlow() {
        return type.equals(BACKWARD_FLOW);
    }
}
