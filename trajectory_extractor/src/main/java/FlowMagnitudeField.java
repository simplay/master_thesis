public class FlowMagnitudeField {

    private float[][] mags;

    /**
     *
     * @param m row count
     * @param n column count
     */
    public FlowMagnitudeField(int m, int n) {
        mags = new float[m][n];
    }

    public void setRow(int row_idx, float[] row) {
        mags[row_idx] = row;
    }

    public float valueAt(float row_idx, float col_idx) {
        return interpolatedValueAt(mags, row_idx, col_idx);
    }

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
}
