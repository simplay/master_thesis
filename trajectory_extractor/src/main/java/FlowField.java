public class FlowField extends Interpolator{

    private String type;
    private int m;
    private int n;
    public final static String FORWARD_FLOW = "fw";
    public final static String BACKWARD_FLOW = "bw";

    private double[][] u_dir;
    private double[][] v_dir;

    public FlowField(int m, int n, String type) {
        this.m = m;
        this.n = n;
        this.type = type;
        this.u_dir = new double[m][n];
        this.v_dir = new double[m][n];
    }

    /**
     * Get total row count.
     *
     * @return
     */
    public int m() {
        return m;
    }

    /**
     * Get total column count
     *
     * @return
     */
    public int n() {
        return n;
    }

    public double[][] getU() {
        return u_dir;
    }

    public double[][] getV() {
        return v_dir;
    }

    /**
     * Obtain the bilinear interpolated value in the u direction at a given location.
     *
     * @param row_idx
     * @param col_idx
     * @return interpolated u direction value
     */
    public double u_valueAt(double row_idx, double col_idx) {
        return interpolatedValueAt(u_dir, row_idx, col_idx);
    }

    /**
     * Obtain the bilinear interpolated value in the v direction at a given location.
     *
     * @param row_idx
     * @param col_idx
     * @return interpolated v direction value
     */
    public double v_valueAt(double row_idx, double col_idx) {
        return interpolatedValueAt(v_dir, row_idx, col_idx);
    }

    public void setRow(int row_idx, double[] u_row, double[] v_row) {
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
