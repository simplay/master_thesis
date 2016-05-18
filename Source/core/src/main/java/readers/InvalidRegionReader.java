package readers;

import datastructures.InvalidRegionsMask;
import managers.InvalidRegionManager;

import java.util.ArrayList;

/**
 * Read the consistency matrix extracted via the `init_data.m` script.
 *
 * Such consistency files are located at `../output/tracker_data/DATASET/`
 * and aee named like the following `/flow_consistency_NUM.mat`.
 *
 * Such a .mat file contains white space separated float values,
 * where each file line models a matrix row.
 *
 * This matrix encodes the invalid tracking location for given frame.
 *
 * Before continuing a tracking, we first have to check whether its starting
 * point yields a valid location.
 *
 * A location is supposed to be invalid if:
 *  => it is occluded: using the computed tracked to position via the forward flow and going back via the backward flow
 *  does not yield the initial position. Thus, the queried position is occluded.
 *
 *  => the brightness varies too much: too bright locations may cause wrong pixel tracings and thus have to be skipped.
 */
public class InvalidRegionReader extends FileReader{

    // Read file lines
    private ArrayList<double[]> rows;

    /**
     * Reads an invalid region file for a given dataset using an arbitrary base file path.
     *
     * @param dataset dataset we are running.
     * @param basePath base file path where the target file is located.
     * @param fileNr frame index the target file belongs to.
     */
    public InvalidRegionReader(String dataset, String fileNr, String basePath) {
        String baseFileName = basePath + dataset + "/flow_consistency_"+ fileNr + ".mat";
        rows = new ArrayList<double[]>();

        readFile(baseFileName);

        int m = rows.size();
        int n = rows.get(0).length;

        InvalidRegionsMask ff = new InvalidRegionsMask(m, n);

        for (int k = 0; k < m; k++) {
            ff.setRow(k, rows.get(k));
        }

        InvalidRegionManager.getInstance().addMagFlow(ff);
    }

    /**
     * Reads an invalid region file for a given dataset using the pipeline path convention.
     *
     * @param dataset dataset we are running.
     * @param fileNr frame index the target file belongs to.
     */
    public InvalidRegionReader(String dataset, String fileNr) {
        this(dataset, fileNr, "../output/tracker_data/");
    }

    /**
     * Line ttems are whitespace separated.
     *
     * @param line a matrix line.
     */
    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");
        rows.add(parseToDoubleArray(row));
    }

}
