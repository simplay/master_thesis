package readers;

import datastructures.LabeledFile;
import datastructures.LabeledFileLine;
import managers.CalibrationManager;
import pipeline_components.Logger;
import similarity.DataChecker;

import java.util.ArrayList;

/**
 * CalibrationsReader reads the content of a `calib.txt` file.
 * Such files are located at `./Data/DATASET/meta/`.
 *
 * The valid format is described in the README.md located at `./Data/`
 */
public class CalibrationsReader extends FileReader {

    // The read File lines modeled as labeled file lines.
    public ArrayList<LabeledFileLine> fLines;

    /**
     * General constructor
     *
     * @param dataset dataset we are running.
     * @param basePath base file path where files are located.
     */
    public CalibrationsReader(String dataset, String basePath) {
        String baseFileName = basePath + dataset + "/meta/calib.txt";
        if (DataChecker.hasCalibrationData(baseFileName)) {
            fLines = new ArrayList<>();
            readFile(baseFileName);
            CalibrationManager.getInstance(new LabeledFile(fLines));
        } else {
            Logger.printError("No calibration matrix given at `" + baseFileName + "`");
        }
    }

    /**
     * Constructor used by the pipeline.
     * Convention: Files are located at "../../Data/"
     *
     * @param dataset dataset we are running.
     */
    public CalibrationsReader(String dataset) {
        this(dataset, "../../Data/");
    }

    /**
     * Parse a file line according to the specified format of a calib.txt file.
     *
     * @param line a line of the calib.txt file
     */
    @Override
    protected void processLine(String line) {
        if (!line.isEmpty()) {
            String[] rawLabelContent = line.split(": ");
            fLines.add(new LabeledFileLine(rawLabelContent[0], rawLabelContent[1]));
        }
    }

}
