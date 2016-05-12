package readers;

import datastructures.InvalidRegionsMask;
import managers.InvalidRegionManager;

import java.util.ArrayList;

/**
 * Read the consistency matrix extracted via the `init_data.m` script.
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

    private ArrayList<double[]> rows;

    public InvalidRegionReader(String dataset, String fileNr) {
        String baseFileName = "../output/tracker_data/" + dataset + "/flow_consistency_"+ fileNr + ".mat";
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

    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");
        //rows.add(parseToBooleanArray(row));
        rows.add(parseToDoubleArray(row));
    }

}
