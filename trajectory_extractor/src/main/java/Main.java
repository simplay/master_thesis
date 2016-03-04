import java.io.*;

public class Main {
    public static void main(String[] argv) {
        String dataset = "c14";
        int samplingRate = 8;

        File folder = new File("../output/tracker_data/" + dataset);
        File[] fileList = folder.listFiles();
        int counter = 0;
        for (File file : fileList) {
            if (file.getName().matches("candidates_[\\d].txt")) {
                counter++;
            }
        }
        System.out.println("Tracking points over " + counter + " frames...");

        int till_index = counter;
        for (int idx = 1; idx <= till_index; idx++) {
            String fileNr = Integer.toString(idx);
            new CandidateFileReader(dataset, fileNr);
            new FlowFileReader(dataset, FlowField.FORWARD_FLOW, fileNr);
            new FlowFileReader(dataset, FlowField.BACKWARD_FLOW, fileNr);
            new FlowMagFieldReader(dataset, fileNr);
            // TODO load gradient flow files
        }

        System.out.println("Files loaded...");
        new Tracker(till_index, samplingRate);

        // TODO Filter too short trajectories
        // TODO save Trajectories in output files
    }
}
