import java.io.*;
import java.util.ArrayList;

/**
 * Created by simplay on 03/03/16.
 */
public class FlowFileReader extends FileReader{

    // current flow direction scalar field that should be filled
    private ArrayList<float[]> activeRowDir;

    public FlowFileReader(String dataset, String type, String fileNr) {
        String baseFileNameU = "../output/tracker_data/" + dataset + "/" + type + "_u_"+ fileNr + ".mat";
        String baseFileNameV = "../output/tracker_data/" + dataset + "/" + type + "_v_"+ fileNr + ".mat";

        ArrayList<float[]> u_rows = new ArrayList<float[]>();
        ArrayList<float[]> v_rows = new ArrayList<float[]>();

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
        activeRowDir.add(parseToFloatArray(row));
    }
}
