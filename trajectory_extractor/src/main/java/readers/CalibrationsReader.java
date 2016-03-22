package readers;

import similarity.DataChecker;

public class CalibrationsReader extends FileReader {

    public CalibrationsReader(String dataset) {
        String baseFileName = "../data/ldof/" + dataset + "/meta/calib.txt";
        if (DataChecker.hasCalibrationData(baseFileName)) {
        } else {
            System.err.println("No calibration matrix given at `" + baseFileName + "`");
        }
    }

    @Override
    protected void processLine(String line) {
        System.out.println(line);
    }
}
