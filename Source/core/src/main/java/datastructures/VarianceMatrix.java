package datastructures;

public class VarianceMatrix extends Interpolator {

    private double[][] matrix;

    public VarianceMatrix(int m, int n) {
        this.matrix = new double[m][n];
    }

    public double valueAt(Point2d p) {
        return interpolatedValueAt(matrix, p.x(), p.y());
    }

    public void setRow(int row_idx, double[] row) {
        matrix[row_idx] = row;
    }
}
