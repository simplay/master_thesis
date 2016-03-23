package readers;

import datastructures.DepthField;
import managers.DepthManager;

import java.util.LinkedList;

/**
 * The depth images are scaled by a factor of 5000,
 * i.e., a pixel value of 5000 in the depth image corresponds
 * to a distance of 1 meter from the camera, 10000 to 2 meter distance,
 * etc. A pixel value of 0 means missing value/no data.
 *
 * This is why we have to divide all read values by a factor 5000 to obtain meter values.
 */
public class DepthFieldReader extends FileReader {

    private LinkedList<double[]> rows;
    private double scale;

    public DepthFieldReader(String dataset, String fileNr) {
        String baseFileName = "../output/tracker_data/" + dataset + "/depth_"+ fileNr + ".txt";
        scale = 1d/5000d;

        rows = new LinkedList<>();
        readFile(baseFileName);

        int m = rows.size();
        int n = rows.get(0).length;

        DepthField df = new DepthField(m,n);

        int counter = 0;
        for (double[] row : rows) {
            df.setRow(counter, row);
            counter++;
        }
        DepthManager.getInstance().add(df);
    }

    @Override
    protected void processLine(String line) {
        String[] depth_matrix_row = line.split("\\[|\\]")[1].split(" ");
        rows.add(parseToDoubleArray(depth_matrix_row, scale));
    }
}
