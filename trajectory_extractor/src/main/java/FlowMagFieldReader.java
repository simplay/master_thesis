import java.util.ArrayList;

public class FlowMagFieldReader extends FileReader{

    private ArrayList<double[]> rows;

    public FlowMagFieldReader(String dataset, String fileNr) {
        String baseFileName = "../output/tracker_data/" + dataset + "/d_fw_flow_"+ fileNr + ".mat";
        rows = new ArrayList<double[]>();

        readFile(baseFileName);

        int m = rows.size();
        int n = rows.get(0).length;

        FlowMagnitudeField ff = new FlowMagnitudeField(m, n);

        for (int k = 0; k < m; k++) {
            ff.setRow(k, rows.get(k));
        }

        FlowMagManager.getInstance().addMagFlow(ff);
    }

    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");
        rows.add(parseToDoubleArray(row));
    }

}
