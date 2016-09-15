package readers;

import datastructures.DepthField;
import managers.DepthManager;
import pipeline_components.ArgParser;
import java.util.LinkedList;

/**
 * DepthFieldReader reads the depth values that belong to a dataset frame.
 * Depth values are supposed to be in meter units, where
 * the value indicates an invalid depth measurement.
 *
 * Depth values are used for computing spacial 3d distances between trajectories,
 * used within similarity tasks.
 *
 * Depth images are located at `../output/tracker_data/"`
 * and are named like `depth_NUM.txt`
 *
 * Each file line is enclosed by `[]`, the depth values
 * are float values separated by white spaces within their enclosures.
 *
 * Example
 *  ...
 *  [... 2.34 3.4 ...]
 *  [... 0.23 0.3 ...]
 *  ...
 *
 */
public class DepthFieldReader extends FileReader {

    // Read depth file lines.
    private LinkedList<double[]> rows;

    // Depth scale value in case we want to re-weight depth values.
    private double scale;

    /**
     * Reads an depth file for a given dataset using an arbitrary base file path.
     *
     * @param dataset dataset we are running.
     * @param basePath base file path where the target file is located.
     * @param fileNr frame index the target file belongs to.
     */
    public DepthFieldReader(String dataset, String fileNr, String basePath) {
        String baseFileName = basePath + dataset + "/depth_"+ fileNr + ".txt";
        scale = ArgParser.getDepthFieldScale();

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

    /**
     * Reads an depth file for a given dataset using the pipeline path convention.
     *
     * @param dataset dataset we are running.
     * @param fileNr frame index the target file belongs to.
     */
    public DepthFieldReader(String dataset, String fileNr) {
        this(dataset, fileNr, "../output/tracker_data/");
    }

    /**
     * Extract the line items of a depth file
     *
     * Lines are enclosed by [] and their items are separated by white spaces.
     *
     * @param line a line of a depth file.
     */
    @Override
    protected void processLine(String line) {
        String[] depth_matrix_row = line.split("\\[|\\]")[1].split(" ");
        rows.add(parseToDoubleArray(depth_matrix_row, scale));
    }
}
