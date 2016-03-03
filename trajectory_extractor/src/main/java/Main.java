import java.io.*;

/**
 * Created by simplay on 02/03/16.
 */
public class Main {
    public static void main(String[] argv) {
        String dataset = "c14";
        String fileNr = "1";

        // TODO make a for loop to read all files
        //new CandidateFileReader(dataset, fileNr);


        // TODO read flow files here
        FlowField ff_fw = new FlowFileReader(dataset, "fw", fileNr).getFlowField();
        FlowField ff_bw = new FlowFileReader(dataset, "bw", fileNr).getFlowField();


        //TODO start tracking
    }
}
