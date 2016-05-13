package datastructures;

/**
 * ColorImage models a RGB image which belongs to a dataset frame.
 * The color values are in the CIE L*a*b* color space and thus are
 * in the range [0,1].
 *
 * This allows us to directly compare color distances by using the l2 norm.
 *
 * Color images are used as an additional cues - compute the color distance -
 * for all SD-similarity tasks.
 *
 */
public class ColorImage extends Interpolator {

    // Color cie lab rgb values represented as 3d points
    private Point3d[][] rgbValues;

    /**
     * Construct a new color image.
     *
     * @param m number of rows
     * @param n number of columns.
     */
    public ColorImage(int m, int n) {
        this.rgbValues = new Point3d[m][n];
    }

    /**
     * Get the bilinear interpolated color value for a given image location.
     *
     * @param p lookup position.
     * @return bilinear interpolated color value at the given lookup position.
     */
    public Point3d valueAt(Point2d p) {
        return interpolatedValueAt(rgbValues, p.x(), p.y());
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
