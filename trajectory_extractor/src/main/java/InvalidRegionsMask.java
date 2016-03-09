public class InvalidRegionsMask extends Interpolator{

    private double[][] mags;

    /**
     *
     * @param m row count
     * @param n column count
     */
    public InvalidRegionsMask(int m, int n) {
        mags = new double[m][n];
    }

    public void setRow(int row_idx, double[] row) {
        mags[row_idx] = row;
    }

    public double valueAt(double row_idx, double col_idx) {
        return interpolatedValueAt(mags, row_idx, col_idx);
    }

    public int valueAt(int row_idx, int column_idx) {
        return (int) mags[row_idx][column_idx];
    }

    public boolean isInvalidAt(Point2f p) {
        if (valueAt(p.rounded().u(), p.rounded().v()) == 1f) {
            return true;
        }
        return false;
    }
}
