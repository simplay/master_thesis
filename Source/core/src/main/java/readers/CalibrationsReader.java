package readers;

import managers.CalibrationManager;
import similarity.DataChecker;

import java.util.ArrayList;

public class CalibrationsReader extends FileReader {

    public ArrayList<String> fLines;

    public CalibrationsReader(String dataset) {
        String baseFileName = "../../Data/" + dataset + "/meta/calib.txt";
        if (DataChecker.hasCalibrationData(baseFileName)) {
            fLines = new ArrayList<>();
            readFile(baseFileName);
            CalibrationManager.getInstance(fLines);
        } else {
            System.err.println("No calibration matrix given at `" + baseFileName + "`");
        }
    }

    @Override
    protected void processLine(String line) {
        if (!line.isEmpty()) fLines.add(line);
    }
}
