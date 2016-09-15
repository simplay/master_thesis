package readers;

import datastructures.FlowVarField;
import managers.VarianceManager;

import java.util.LinkedList;

/**
 * FlowVarFileReader reads the local flow variance values that belong to a flow field.
 * Flow variance values are computed by applying a bilateral filter on the optical flow fields
 * and are supposed to be in pixel^2 units.
 *
 * Flow variance values are used for normalizing motion distances between trajectories,
 * used within a all similarity tasks.
 *
 * Flow Variance fields are located at `../output/tracker_data/DATASET/"`
 * and are named like `local_variances_NUM.txt`
 *
 * Each file line is enclosed by `[]`, the depth values
 * are float values separated by white spaces within their enclosures.
 *
 * Example
 *  ...
 *  [... 2.34 3.4 ...]
 *  [... 0.23 0.3 ...]
 *  ...
 */
public class FlowVarFileReader extends FileReader {

    // Read flow variance file lines
    private LinkedList<double[]> varianceRows;

    /**
     * Reads an flow variance file for a given dataset using an arbitrary base file path.
     *
     * @param dataset dataset we are running.
     * @param basePath base file path where the target file is located.
     * @param fileNr frame index the target file belongs to.
     */
    public FlowVarFileReader(String dataset, String fileNr, String basePath) {
        String baseFileName = basePath + dataset + "/local_variances_"+ fileNr + ".txt";
        varianceRows = new LinkedList<double[]>();
        readFile(baseFileName);
        int m = varianceRows.size();
        int n = varianceRows.get(0).length;

        FlowVarField varMat = new FlowVarField(m, n);

        for (int k = 0; k < m; k++) {
            varMat.setRow(k, varianceRows.get(k));
        }
        VarianceManager.getInstance().add(varMat);
    }

    /**
     * Reads an flow variance file for a given dataset using the pipeline path convention.
     *
     * @param dataset dataset we are running.
     * @param fileNr frame index the target file belongs to.
     */
    public FlowVarFileReader(String dataset, String fileNr) {
        this(dataset, fileNr, "../output/tracker_data/");
    }

    /**
     * Extract the line items of a flow variance file
     *
     * Lines are enclosed by [] and their items are separated by white spaces.
     *
     * @param line a line of a depth file.
     */
    @Override
    protected void processLine(String line) {
        String[] elements = extractElementsFromMatlabMatrixRow(line);
        varianceRows.add(parseToDoubleArray(elements));
    }
}
