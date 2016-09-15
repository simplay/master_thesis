package readers;

import datastructures.FlowField;
import managers.FlowFieldManager;
import pipeline_components.Logger;
import java.util.ArrayList;

/**
 * FlowFileReader reads the flow fields that were computed for a target dataset.
 * More precisely, it reads the forward-and backward u-and v directional fields.
 * Therefore, there are 4 files read per frame: per direction (forward/backward) there
 * are an u-and v flow component field read.
 *
 * The flow fields are used to:
 *  a) compute the tracking points which form a trajectory
 *      (current position + flow = tracked to position) and
 *  b) for computing the motion distance between trajectories used within the similarity tasks.
 *
 * The values of optical flow are in pixel units and can be either negative or positive.
 *
 * Flow files are located at `../output/tracker_data/DATASET/"`
 * and are named like:
 *  `forward_u_NUM.mat.txt`.
 *  `forward_v_NUM.mat.txt`.
 *  `backward_u_NUM.mat.txt`.
 *  `backward_v_NUM.mat.txt`.
 */
public class FlowFileReader extends FileReader {

    // current flow direction scalar field that should be filled
    private ArrayList<double[]> activeRowDir;

    /**
     * Reads an given flow type file for a given dataset using an arbitrary base file path.
     *
     * @param dataset dataset we are running.
     * @param type flow type we want to read.
     * @param basePath base file path where the target file is located.
     * @param fileNr frame index the target file belongs to.
     */
    public FlowFileReader(String dataset, String type, String fileNr, String basePath) {
        String baseFileNameU = basePath + dataset + "/" + type + "_u_"+ fileNr + ".mat";
        String baseFileNameV = basePath + dataset + "/" + type + "_v_"+ fileNr + ".mat";

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
            Logger.printError("Wrong flow type assigned");
        }
    }

    /**
     * Reads an given flow type file for a given dataset using the pipeline path conventions.
     *
     * @param dataset dataset we are running.
     * @param type flow type we want to read.
     * @param fileNr frame index the target file belongs to.
     */
    public FlowFileReader(String dataset, String type, String fileNr) {
        this(dataset, type, fileNr, "../output/tracker_data/");
    }

    /**
     * Extract the line items of a depth variance file
     *
     * Line items are separated by white spaces.
     *
     * @param line a line of a flow file.
     */
    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");
        activeRowDir.add(parseToDoubleArray(row));
    }
}
