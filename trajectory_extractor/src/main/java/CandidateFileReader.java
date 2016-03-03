import java.io.*;
import java.util.LinkedList;

/**
 * Created by simplay on 03/03/16.
 */
public class CandidateFileReader {
    private LinkedList<String[]> candidates;

    public CandidateFileReader(String dataset, String fileNr) {

        String baseFileName = "../output/tracker_data/" + dataset + "/candidates_"+ fileNr + ".txt";
        candidates = new LinkedList<String[]>();

        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(baseFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        try {
            while ((strLine = br.readLine()) != null) {
                processLine(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TrackingCandidates.getInstance().addCandidate(candidates.getFirst(), candidates.getLast());
    }

    private void processLine(String line) {
        String[] elements = line.split("\\[|\\]")[1].split(" ");
        candidates.add(elements);
    }
}
