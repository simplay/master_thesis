import java.io.*;

/**
 * Created by simplay on 02/03/16.
 */
public class Main {
    public static void main(String[] argv) {
        String dataset = "c14";

        int till_index = 4;
        for (int idx = 1; idx <= till_index; idx++) {

            String fileNr = Integer.toString(idx);

            // TODO make a for loop to read all files
            new CandidateFileReader(dataset, fileNr);


            // TODO read flow files here
            new FlowFileReader(dataset, FlowField.FORWARD_FLOW, fileNr);
            new FlowFileReader(dataset, FlowField.BACKWARD_FLOW, fileNr);

        }

        System.out.println("files loaded...");

    }
}
