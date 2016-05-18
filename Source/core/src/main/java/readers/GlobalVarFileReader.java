package readers;

import managers.VarianceManager;

/**
 * GlobalVarFileReader reads the global flow variances computed for each dataset frame.
 *
 * Global flow variance can be used as a normalization factor for the motion distance,
 * computed in similarity tasks. However, we usually make use of the local flow variance values.
 *
 * The global variance is just a default fall-back normalization factor, used for evaluation purposes.
 */
public class GlobalVarFileReader extends FileReader {

    /**
     * Reads a global variance file for a given dataset using an arbitrary base file path.
     *
     * @param dataset dataset we are running.
     * @param basePath base file path where the target file is located.
     */
    public GlobalVarFileReader(String dataset, String basePath) {
        String baseFileName = basePath + dataset + "/global_variances" + ".txt";
        readFile(baseFileName);
    }

    /**
     * Reads a global variance file for a given dataset using the pipeline path convention.
     *
     * @param dataset dataset we are running.
     */
    public GlobalVarFileReader(String dataset) {
        this(dataset, "../output/tracker_data/");
    }

    /**
     * Extracts the items of a global variance file.
     *
     * Ech file line contains a float, represented as a string.
     * The file line index corresponds to the dataset frame index.
     *
     * @param line a line in a global variance file.
     */
    @Override
    protected void processLine(String line) {
        VarianceManager.getInstance().addGlobalVariance(Double.parseDouble(line));
    }
}
