package readers;

import managers.MetaDataManager;

import java.util.ArrayList;

/**
 * MetaInfoReader reads the dataset image dimensions and the sampling rate, stored in
 * ./output/tracker_data/metainfo.txt.
 *
 * The `metainfo.txt` file contains a sequence of strings, separated by the colonel string ','.
 * The first string represents the height, the 2nd the width and the 3rd the sampling rate.
 */
public class MetaInfoReader extends FileReader {

    // The read File lines modeled as labeled file lines.
    private ArrayList<String> data;

    /**
     * Reads a meta-info file for a given dataset using an arbitrary base file path.
     *
     * @param dataset dataset we are running.
     * @param basePath base file path where the target file is located.
     */
    public MetaInfoReader(String dataset, String basePath) {
        String baseFileName = basePath + dataset + "/metainfo.txt";
        data = new ArrayList<>();
        readFile(baseFileName);
        MetaDataManager.getInstance(data);
    }

    /**
     * Reads a meta-info file for a given dataset using the pipeline path-convention.
     *
     * @param dataset base file path where the target file is located.
     */
    public MetaInfoReader(String dataset) {
        this(dataset, "../output/tracker_data/");
    }

    /**
     * Extracts the content in a target metainfo file.
     *
     * Line items are ',' separated.
     *
     * @param line a line of the metainfo.txt file
     */
    @Override
    protected void processLine(String line) {
        String[] lines = line.split(",");
        for (String l : lines) {
            data.add(l);
        }
    }

}
