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

    public float[][] getU() {
        return u_dir;
    }

    public float[][] getV() {
        return v_dir;
    }

    /**
     * Obtain the bilinear interpolated value in the u direction at a given location.
     *
     * @param row_idx
     * @param col_idx
     * @return interpolated u direction value
     */
    public float u_valueAt(float row_idx, float col_idx) {
        return interpolatedValueAt(u_dir, row_idx, col_idx);
    }

    /**
     * Obtain the bilinear interpolated value in the v direction at a given location.
     *
     * @param row_idx
     * @param col_idx
     * @return interpolated v direction value
     */
    public float v_valueAt(float row_idx, float col_idx) {
        return interpolatedValueAt(v_dir, row_idx, col_idx);
    }

    /**
     * Compute bilinear interpolation of a given flow position
     *
     * @param data target flow direction values
     * @param x corresponds to row index
     * @param y corresponds to column index
     * @return interpolated flow value
     */
    private float interpolatedValueAt(float[][] data, float x, float y) {
        int px_i = (int) Math.floor(x);
        int py_i = (int) Math.floor(y);

        int px_i2 = px_i + 1;
        int py_i2 = px_i + 1;

        float dx = x - px_i;
        float dy = y - py_i;

        float f_00 = data[px_i][py_i];
        float f_01 = data[px_i][py_i2];
        float f_10 = data[px_i2][py_i];
        float f_11 = data[px_i2][py_i2];

        float sum = 0f;
        sum += f_00*(1.0f-dx)*(1.0-dy);
        sum += f_01*(1.0f-dx)*dy;
        sum += f_10*dx*(1.0f-dy);
        sum += f_11*dx*dy;

        return sum;
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
