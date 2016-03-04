import java.io.*;
import java.util.LinkedList;

/**
 * Created by simplay on 03/03/16.
 */
public class CandidateFileReader extends FileReader{
    private LinkedList<String[]> candidates;

    public CandidateFileReader(String dataset, String fileNr) {
        String baseFileName = "../output/tracker_data/" + dataset + "/candidates_"+ fileNr + ".txt";
        candidates = new LinkedList<String[]>();
        readFile(baseFileName);
        TrackingCandidates.getInstance().addCandidate(candidates.getFirst(), candidates.getLast());
    }

    @Override
    protected void processLine(String line) {
        String[] elements = line.split("\\[|\\]")[1].split(" ");
        candidates.add(elements);
    }
}
