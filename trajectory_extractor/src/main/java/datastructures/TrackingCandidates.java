package datastructures;

import java.util.ArrayList;
import java.util.LinkedList;

public class TrackingCandidates {

    private ArrayList<Integer[]> rows;
    private ArrayList<Integer[]> columns;
    private static TrackingCandidates instance = null;

    public static TrackingCandidates getInstance() {
        if (instance == null) {
            instance = new TrackingCandidates();
        }
        return instance;
    }

    private TrackingCandidates() {
        rows = new ArrayList<Integer[]>();
        columns = new ArrayList<Integer[]>();
    }

    public void addCandidate(String[] row, String[] column) {
        rows.add(parseToIntArray(row));
        columns.add(parseToIntArray(column));
    }

    public LinkedList<Integer[]> getCandidateOfFrame(int frame_idx) {
        LinkedList<Integer[]> candidate = new LinkedList<Integer[]>();
        candidate.add(rows.get(frame_idx));
        candidate.add(columns.get(frame_idx));
        return candidate;
    }

    private Integer[] parseToIntArray(String[] items) {
        Integer[] intItems = new Integer[items.length];
        int idx = 0;
        for (String item : items) {
            intItems[idx] = Integer.parseInt(item);
            idx++;
        }
        return intItems;
    }
}
