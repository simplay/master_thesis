import java.util.LinkedList;

public class FlowVarFileReader extends FileReader{
    private LinkedList<double[]> varianceRows;

    public FlowVarFileReader(String dataset, String fileNr) {
        String baseFileName = "../output/tracker_data/" + dataset + "/local_variances_"+ fileNr + ".txt";
        varianceRows = new LinkedList<double[]>();
        readFile(baseFileName);
        int m = varianceRows.size();
        int n = varianceRows.get(0).length;

        VarianceMatrix varMat = new VarianceMatrix(m, n);

        for (int k = 0; k < m; k++) {
            varMat.setRow(k, varianceRows.get(k));
        }
        VarianceManager.getInstance().add(varMat);
    }

    @Override
    protected void processLine(String line) {
        String[] elements = extractElementsFromMatlabMatrixRow(line);
        varianceRows.add(parseToDoubleArray(elements));
    }
}
