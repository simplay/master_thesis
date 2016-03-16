package datastructures;

public class VarianceMatrix extends Interpolator {

    private double[][] matrix;

    public VarianceMatrix(int m, int n) {
        this.matrix = new double[m][n];
    }

    public double valueAt(Point2f p) {
        return interpolatedValueAt(matrix, p.x(), p.y());
    }

    public double valueAt(double row_idx, double col_idx) {
        return interpolatedValueAt(matrix, row_idx, col_idx);
    }

    public void setRow(int row_idx, double[] row) {
        matrix[row_idx] = row;
    }

}
