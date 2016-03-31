package readers;

import managers.VarianceManager;

public class GlobalVarFileReader extends FileReader {

    public GlobalVarFileReader(String dataset) {
        String baseFileName = "../output/tracker_data/" + dataset + "/global_variances" + ".txt";
        readFile(baseFileName);
    }

    @Override
    protected void processLine(String line) {
        VarianceManager.getInstance().addGlobalVariance(Double.parseDouble(line));
    }
}
