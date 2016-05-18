package readers;

import datastructures.DepthVarField;
import managers.DepthVarManager;
import java.util.LinkedList;

/**
 * DepthVarReader reads the depth variance values that belong to a depth field.
 * Depth variance values are computed by applying a 2-pass bilateral filter
 * and are supposed to be in meter^2 units.
 *
 * Depth variance values are used for normalizing 3d motion distances between trajectories,
 * used within a SAED/PAED similarity tasks.
 *
 * Depth Variance fields are located at `../output/tracker_data/"`
 * and are named like `local_depth_variances_NUM.txt`
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
public class DepthVarReader extends FileReader {

    // Read depth variance lines
    private LinkedList<double[]> rows;

    /**
     * Reads an depth variance file for a given dataset using an arbitrary base file path.
     *
     * @param dataset dataset we are running.
     * @param basePath base file path where the target file is located.
     * @param fileNr frame index the target file belongs to.
     */
    public DepthVarReader(String dataset, String fileNr, String basePath) {
        String baseFileName = basePath + dataset + "/local_depth_variances_"+ fileNr + ".txt";
        rows = new LinkedList<>();
        readFile(baseFileName);

        int m = rows.size();
        int n = rows.get(0).length;

        DepthVarField dvf = new DepthVarField(m,n);

        int counter = 0;
        for (double[] row : rows) {
            dvf.setRow(counter, row);
            counter++;
        }
        DepthVarManager.getInstance().add(dvf);
    }

    /**
     * Reads an depth variance file for a given dataset using the pipeline path convention.
     *
     * @param dataset dataset we are running.
     * @param fileNr frame index the target file belongs to.
     */
    public DepthVarReader(String dataset, String fileNr) {
        this(dataset, fileNr, "../output/tracker_data/");
    }

    /**
     * Extract the line items of a depth variance file
     *
     * Lines are enclosed by [] and their items are separated by white spaces.
     *
     * @param line a line of a depth file.
     */
    @Override
    protected void processLine(String line) {
        String[] elements = extractElementsFromMatlabMatrixRow(line);
        rows.add(parseToDoubleArray(elements));
    }
}
