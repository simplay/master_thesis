public class Interpolator {

    /**
     * Compute bilinear interpolation of a given flow position
     *
     * @param data target flow direction values
     * @param x corresponds to row index
     * @param y corresponds to column index
     * @return interpolated flow value
     */
     public double interpolatedValueAt(double[][] data, double x, double y) {
        int px_i = (int) Math.floor(x);
        int py_i = (int) Math.floor(y);

        int px_i2 = px_i + 1;
        int py_i2 = py_i + 1;

        double dx = x - px_i;
        double dy = y - py_i;

        double f_00 = data[px_i][py_i];
        double f_01 = data[px_i][py_i2];
        double f_10 = data[px_i2][py_i];
        double f_11 = data[px_i2][py_i2];

        double sum = 0f;
        sum += f_00*(1.0f-dx)*(1.0-dy);
        sum += f_01*(1.0f-dx)*dy;
        sum += f_10*dx*(1.0f-dy);
        sum += f_11*dx*dy;

        return sum;
    }
}
