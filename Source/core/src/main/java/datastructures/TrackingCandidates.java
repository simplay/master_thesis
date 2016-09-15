package datastructures;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * TrackingCandidates is a singleton containing all the traceable candidates
 * of every dataset frame.
 *
 * The term 'candidate' refers to a pair of indices, the row-and column index
 * in an image, mapping to a traceable feature within a dataset frame.
 *
 * Candidates are used to start new trajectories during the tracking phase.
 *
 * The indices are/were extracted by the 'init_data.m' script.
 * For further information, please have a look at the `CandidateFileReader` class.
 *
 * Important note: Matlab's indices start counting at '1', whereas Java's first
 * Array index starts at '0'. Therefore, we have to subtract minus one from the read-in
 * indices values.
 *
 */
public class TrackingCandidates {

    // A list of row image indices of traceable candidates
    private ArrayList<Integer[]> rows;

    // A list of column image indices of traceable candidates
    private ArrayList<Integer[]> columns;

    // The singleton instance
    private static TrackingCandidates instance = null;

    /**
     * Obtain the singleton
     *
     * @return singleton
     */
    public static TrackingCandidates getInstance() {
        if (instance == null) {
            instance = new TrackingCandidates();
        }
        return instance;
    }

    /**
     * Releases held row-and column references.
     */
    public static void release() {
        instance = null;
    }

    /**
     * Constructor of singleton.
     */
    private TrackingCandidates() {
        rows = new ArrayList<Integer[]>();
        columns = new ArrayList<Integer[]>();
    }

    /**
     * Add a series of traceable candidates for a new frame.
     *
     * Note that a candidate is defined by its row-and column index.
     * The underlying assumption: the index in the row and column array
     * correspond to each other to encode a candidate properly.
     *
     * @param row list of row indices.
     * @param column list of column indices.
     */
    public void addCandidates(String[] row, String[] column) {
        rows.add(parseToIntArray(row));
        columns.add(parseToIntArray(column));
    }

    /**
     * Get all traceable candidates of a target frame.
     *
     * @param frame_idx frame index
     * @return a collection of index pair lists, containing all tracking candidates.
     */
    public LinkedList<Integer[]> getCandidateOfFrame(int frame_idx) {
        LinkedList<Integer[]> candidate = new LinkedList<Integer[]>();
        candidate.add(rows.get(frame_idx));
        candidate.add(columns.get(frame_idx));
        return candidate;
    }

    /**
     * Maps the read-in indices, represented as strings, to an integer array.
     *
     * @param items items contained in a string array.
     * @return integer array representation.
     */
    private Integer[] parseToIntArray(String[] items) {
        Integer[] intItems = new Integer[items.length];
        int idx = 0;
        for (String item : items) {

            // Since Matlab's indices start counting at 1
            // but Java's at 0, we have to perform an index shift
            // by minus 1 in order to have a correct lookup position.
            intItems[idx] = Integer.parseInt(item)-1;
            idx++;
        }
        return intItems;
    }
}
