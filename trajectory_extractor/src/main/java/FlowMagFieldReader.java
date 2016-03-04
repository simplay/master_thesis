
/**
 * Created by simplay on 04/03/16.
 */
public class FlowMagFieldReader extends FileReader{

    public FlowMagFieldReader(String dataset, String fileNr) {
        String baseFileName = "../output/tracker_data/" + dataset + "/d_fw_flow_"+ fileNr + ".mat";
        readFile(baseFileName);
    }

    @Override
    protected void processLine(String line) {
        String[] row = line.split(" ");
        float[] fs = parseToFloatArray(row);
    }

}
