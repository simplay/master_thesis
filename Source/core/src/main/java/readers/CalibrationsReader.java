package readers;

import datastructures.LabeledFile;
import datastructures.LabeledFileLine;
import managers.CalibrationManager;
import pipeline_components.Logger;
import similarity.DataChecker;

import java.util.ArrayList;

public class CalibrationsReader extends FileReader {

    public ArrayList<LabeledFileLine> fLines;

    public CalibrationsReader(String dataset) {
        String baseFileName = "../../Data/" + dataset + "/meta/calib.txt";
        if (DataChecker.hasCalibrationData(baseFileName)) {
            fLines = new ArrayList<>();
            readFile(baseFileName);
            CalibrationManager.getInstance(new LabeledFile(fLines));
        } else {
            Logger.printError("No calibration matrix given at `" + baseFileName + "`");
        }
    }

    @Override
    protected void processLine(String line) {
        if (!line.isEmpty()) {
            String[] rawLabelContent = line.split(": ");
            fLines.add(new LabeledFileLine(rawLabelContent[0], rawLabelContent[1]));
        }
    }

}
