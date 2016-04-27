package datastructures;

public class DepthVarField extends Interpolator {

    private double[][] data;

    public DepthVarField(int m, int n) {
        data = new double[m][n];
    }

    public void setRow(int row_idx, double[] row) {
        data[row_idx] = row;
    }

    public double valueAt(double row_idx, double col_idx) {
        return interpolatedValueAt(data, row_idx, col_idx);
    }

}
