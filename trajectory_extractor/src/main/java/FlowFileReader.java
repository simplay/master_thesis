import java.util.ArrayList;

public class FlowFileReader extends FileReader{

    // current flow direction scalar field that should be filled
    private ArrayList<double[]> activeRowDir;

    public FlowFileReader(String dataset, String type, String fileNr) {
        String baseFileNameU = "../output/tracker_data/" + dataset + "/" + type + "_u_"+ fileNr + ".mat";
        String baseFileNameV = "../output/tracker_data/" + dataset + "/" + type + "_v_"+ fileNr + ".mat";

        ArrayList<double[]> u_rows = new ArrayList<double[]>();
        ArrayList<double[]> v_rows = new ArrayList<double[]>();

        activeRowDir = u_rows;
        readFile(baseFileNameU);

        activeRowDir = v_rows;
        readFile(baseFileNameV);

        int m = activeRowDir.size();
        int n = activeRowDir.get(0).length;

        FlowField ff = new FlowField(m, n, type);

        for (int k = 0; k < m; k++) {
            ff.setRow(k, u_rows.get(k), v_rows.get(k));
        }

        if (ff.isForwardFlow()) {
            FlowFieldManager.getInstance().addForwardFlow(ff);
        } else if (ff.isBackwardFlow()) {
            FlowFieldManager.getInstance().addBackwardFlow(ff);
        } else {
            System.err.println("Wrong flow type assigned");
        }

    }

    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");
        activeRowDir.add(parseToDoubleArray(row));
    }
}
