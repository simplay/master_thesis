public class ColorImage extends Interpolator{

    private Point3d[][] rgbValues;

    public ColorImage(int m, int n) {
        this.rgbValues = new Point3d[m][n];
    }

    public Point3d valueAt(double row_idx, double col_idx) {
        return interpolatedValueAt(rgbValues, row_idx, col_idx);
    }

    /**
     * Set a rgb triple at a given index location in the color image
     *
     * @param rgb a rgb point3d value
     * @param row_idx target row index
     * @param col_idx target column index
     */
    public void setElement(Point3d rgb, int row_idx, int col_idx) {
        rgbValues[row_idx][col_idx] = rgb;
    }
}
