import java.io.*;

/**
 * Created by simplay on 02/03/16.
 */
public class Main {
    public static void main(String[] argv) {
        String dataset = "c14";
        String fileNr = "1";

        new CandidateFileReader(dataset, fileNr);

        for (Integer i : TrackingCandidates.getInstance().getCandidateOfFrame(0).getFirst()) {
            System.out.print(i + " ");
        }
    }
}
