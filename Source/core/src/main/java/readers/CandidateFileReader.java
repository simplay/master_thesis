package readers;

import datastructures.TrackingCandidates;
import java.util.LinkedList;

/**
 * CandidateFileReader reads all the traceable candidate tracking points for a given frame.
 * These points were determined and extracted by the `init_data.m` script.
 *
 * The files are located at "../output/tracker_data/DATASET/"
 * and named like the regex /(candidates_)(1-9)+(.txt)/ describes.
 *
 * The files contains two lists (ordered) of indices, the row and column indices
 * of traceable feature locations living in a target frame.
 *
 * Fileformat:
 *  The indices are wrapped by `[]`. Both indices list have to be equally sized and
 *  are formatted liked the following:
 *  /\[((1-9)(\s)+)*((1-9)+)\]\n\[((1-9)(\s)+)*((1-9)+)\]/
 * Example: When determining the tracking candidates (1,3),(2,3),(55,99),
 * we get the following file content:
 *  ```
 *      [1 2 55]
 *      [3 3 99]
 *  ```
 */
public class CandidateFileReader extends FileReader {

    // The read File lines modeled as labeled file lines.
    private LinkedList<String[]> candidates;

    /**
     * Reads a candidate file for a given dataset using an arbitrary base file path.
     *
     * @param dataset dataset we are running.
     * @param fileNr index of frame we want to process.
     * @param basePath base file path where the target file is located.
     */
    public CandidateFileReader(String dataset, String fileNr, String basePath) {
        String baseFileName = basePath + dataset + "/candidates_"+ fileNr + ".txt";
        candidates = new LinkedList<String[]>();
        readFile(baseFileName);
        TrackingCandidates.getInstance().addCandidates(candidates.getFirst(), candidates.getLast());
    }

    /**
     * Reads a candidate file for a given dataset using the pipeline convention path.
     *
     * @param dataset dataset we are running.
     * @param fileNr index of frame we want to process.
     */
    public CandidateFileReader(String dataset, String fileNr) {
        this(dataset, fileNr, "../output/tracker_data/");
    }

    /**
     * Extracts the items of a file line.
     * The content of a file is formatted like the regex
     * /\[((1-9)(\s)+)*((1-9)+)\]\n\[((1-9)(\s)+)*((1-9)+)\]/
     * describes.
     *
     * @param line String version of file line.
     */
    @Override
    protected void processLine(String line) {
        String[] elements = extractElementsFromMatlabMatrixRow(line);
        candidates.add(elements);
    }
}
