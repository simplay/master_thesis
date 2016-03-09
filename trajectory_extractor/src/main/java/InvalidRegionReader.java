import java.util.ArrayList;

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
        rows.add(parseToDoubleArray(row));
    }

}
