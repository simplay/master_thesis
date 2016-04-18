package readers;

import datastructures.DepthVarField;
import managers.DepthVarManager;
import java.util.LinkedList;

public class DepthVarReader extends FileReader {
    private LinkedList<double[]> rows;

    public DepthVarReader(String dataset, String fileNr) {
        String baseFileName = "../output/tracker_data/" + dataset + "/local_depth_variances_"+ fileNr + ".txt";
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

    @Override
    protected void processLine(String line) {
        String[] elements = extractElementsFromMatlabMatrixRow(line);
        rows.add(parseToDoubleArray(elements));
    }
}
