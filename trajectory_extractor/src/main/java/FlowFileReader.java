import java.io.*;
import java.util.ArrayList;

/**
 * Created by simplay on 03/03/16.
 */
public class FlowFileReader {
    private ArrayList<float[]> activeRowDir;

    public FlowFileReader(String dataset, String type, String fileNr) {
        String baseFileNameU = "../output/tracker_data/" + dataset + "/" + type + "_u_"+ fileNr + ".mat";
        String baseFileNameV = "../output/tracker_data/" + dataset + "/" + type + "_v_"+ fileNr + ".mat";

        ArrayList<float[]> u_rows = new ArrayList<float[]>();
        ArrayList<float[]> v_rows = new ArrayList<float[]>();

        activeRowDir = u_rows;
        read_flow_dir_matrix(baseFileNameU);

        activeRowDir = v_rows;
        read_flow_dir_matrix(baseFileNameV);

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

    private void read_flow_dir_matrix(String baseFileName) {
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(baseFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        try {
            while ((strLine = br.readLine()) != null) {
                processLine(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processLine(String line) {
        String[] row = line.split(" ");
        activeRowDir.add(parseToFloatArray(row));
    }

    private float[] parseToFloatArray(String[] items) {
        float[] intItems = new float[items.length];
        int idx = 0;
        for (String item : items) {
            intItems[idx] = Float.parseFloat(item);
            idx++;
        }
        return intItems;
    }
}
