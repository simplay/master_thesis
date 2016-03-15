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
       int m = data.length;
       int n = data[0].length;
       if (x > m-1 || y > n-1) {
          throw new ArrayIndexOutOfBoundsException("Dimensions (m,n)=("+m+","+n+") but accessing (x,y)=("+x+","+y+")" );
       }

       int px_i = (int) Math.floor(x);
       int py_i = (int) Math.floor(y);

       int px_i2 = px_i + 1;
       int py_i2 = py_i + 1;

       double dx = x - px_i;
       double dy = y - py_i;

       double f_00 = data[px_i][py_i];
       double f_01 = saveGetAt(data, px_i, py_i2); // data[px_i][py_i2];
       double f_10 = saveGetAt(data, px_i2, py_i); // data[px_i2][py_i];
       double f_11 = saveGetAt(data, px_i2, py_i2); // data[px_i2][py_i2];

       double sum = 0d;
       sum += f_00*(1.0d-dx)*(1.0d-dy);
       sum += f_01*(1.0d-dx)*dy;
       sum += f_10*dx*(1.0d-dy);
       sum += f_11*dx*dy;

       return sum;
    }

   protected Point3d interpolatedValueAt(Point3d[][] data, double x, double y) {
      int m = data.length;
      int n = data[0].length;
      if (x > m-1 || y > n-1) {
         throw new ArrayIndexOutOfBoundsException("Dimensions (m,n)=("+m+","+n+") but accessing (x,y)=("+x+","+y+")" );
      }

      int px_i = (int) Math.floor(x);
      int py_i = (int) Math.floor(y);

      int px_i2 = px_i + 1;
      int py_i2 = py_i + 1;

      double dx = x - px_i;
      double dy = y - py_i;

      Point3d f_00 = data[px_i][py_i];
      Point3d f_01 = saveGetAt(data, px_i, py_i2); // data[px_i][py_i2];
      Point3d f_10 = saveGetAt(data, px_i2, py_i); // data[px_i2][py_i];
      Point3d f_11 = saveGetAt(data, px_i2, py_i2); // data[px_i2][py_i2];

      double i_x = bilinearInterpolatedValue(f_00.x(), f_01.x(), f_10.x(), f_11.x(), dx, dy);
      double i_y = bilinearInterpolatedValue(f_00.y(), f_01.y(), f_10.y(), f_11.y(), dx, dy);
      double i_z = bilinearInterpolatedValue(f_00.z(), f_01.z(), f_10.z(), f_11.z(), dx, dy);
      return new Point3d(i_x, i_y, i_z);
   }

   private double bilinearInterpolatedValue(double f_00, double f_01, double f_10, double f_11, double dx, double dy) {
      double sum = 0d;
      sum += f_00*(1.0d-dx)*(1.0d-dy);
      sum += f_01*(1.0d-dx)*dy;
      sum += f_10*dx*(1.0d-dy);
      sum += f_11*dx*dy;
      return sum;
   }

   protected double saveGetAt(double[][] data, int idx, int idy) {
      int m = data.length;
      int n = data[0].length;
      if (idx == m || idy == n) {
         return 0d;
      }
      return data[idx][idy];
   }

   protected Point3d saveGetAt(Point3d[][] data, int idx, int idy) {
      int m = data.length;
      int n = data[0].length;
      if (idx == m || idy == n) {
         return new Point3d(0,0,0);
      }
      return data[idx][idy];
   }
}
