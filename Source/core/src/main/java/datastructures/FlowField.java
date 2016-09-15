package datastructures;

/**
 * FlowField models the optical flow, i.e. a 2D vector field.
 * It is possible to perform queries on arbitrary positions within the vectorfield grid.
 */
public class FlowField extends Interpolator {

    // Indicates whether it is either a forward-or a backward flow
    private String type;

    // Vectorfield dimensions: m x n x 2
    private int m;
    private int n;

    // FlowField types
    public final static String FORWARD_FLOW = "fw";
    public final static String BACKWARD_FLOW = "bw";

    // The directional components of the vectorfield, i.e. scalarfields.
    private double[][] u_dir;
    private double[][] v_dir;

    /**
     * Constructor of a FlowField.
     *
     * @param m number of rows
     * @param n number of columns
     * @param type the type of the FlowField, either "fw" or "bw"
     */
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

    /**
     * Obtain the u directions
     *
     * @return all u directional components
     */
    public double[][] getU() {
        return u_dir;
    }

    /**
     * Obtain the v directions
     *
     * @return all v directional components
     */
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

    /**
     * Assign an u- and v row values to a certain row in the vector field.
     *
     * @param row_idx target index
     * @param u_row items of u-row.
     * @param v_row items of v-row
     */
    public void setRow(int row_idx, double[] u_row, double[] v_row) {
        u_dir[row_idx] = u_row;
        v_dir[row_idx] = v_row;
    }

    /**
     * Checks whether this flow field carries a forward flow.
     *
     * @return true if it is a forward flow, otherwise false.
     */
    public boolean isForwardFlow() {
        return type.equals(FORWARD_FLOW);
    }

    /**
     * Checks whether this flow field carries a backward flow.
     *
     * @return true if it is a backward flow, otherwise false.
     */
    public boolean isBackwardFlow() {
        return type.equals(BACKWARD_FLOW);
    }
}
