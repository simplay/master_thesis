import java.io.*;

public class Main {
    public static void main(String[] argv) {
        String dataset = "chair3";
        int samplingRate = 8;


        String output_base_path = "../output/trajectories/";

        File folder = new File("../output/tracker_data/" + dataset);
        File[] fileList = folder.listFiles();
        int counter = 0;
        for (File file : fileList) {
            if (file.getName().matches("candidates_[\\d]+.txt")) {
                counter++;
            }
        }
        System.out.println("Tracking points over " + counter + " frames...");
        System.out.println();

        int till_index = counter;
        for (int idx = 1; idx <= till_index; idx++) {
            String fileNr = Integer.toString(idx);
            new CandidateFileReader(dataset, fileNr);
            new FlowFileReader(dataset, FlowField.FORWARD_FLOW, fileNr);
            new FlowFileReader(dataset, FlowField.BACKWARD_FLOW, fileNr);
            new InvalidRegionReader(dataset, fileNr);
            // TODO load gradient flow files
        }

        System.out.println("Files loaded...");
        System.out.println();
        System.out.println("Sampling every " + samplingRate + "th pixel");
        new Tracker(till_index, samplingRate);
        System.out.println();
        System.out.println("Number of extracted trajectories: "+ TrajectoryManager.getInstance().trajectoryCount());
        for (int k = 0; k <= till_index+1; k++) {
            int trajectoryCount = TrajectoryManager.getInstance().allTrajectoryWithLength(k).size();
            System.out.println("#Trajectories with len=" + k + ": " + trajectoryCount);
        }
        System.out.println();
        // one pointed trajectories have a length of 0.
        System.out.println("Filtering 1-pointed trajectories...");

        TrajectoryManager.getInstance().filterOnePointedTrajectories();
        System.out.println("Filtered too short trajectories...");
        System.out.println("Number of remaining trajectories: "+ TrajectoryManager.getInstance().trajectoryCount());

        String output_filePathName = output_base_path + "traj_out_" + dataset+"_fc_" + till_index + ".txt";
        System.out.println("Writting trajectories to output file: " + output_filePathName);
        TrajectoryManager.getInstance().saveTrajectoriesToFile(output_filePathName);

        String outTLF = "../output/trajectory_label_frame/" + dataset + "/";
        System.out.println("Writting active trajectory frame files: " + outTLF);
        (new File(outTLF)).mkdirs();
        TrajectoryManager.getInstance().saveFramewiseTrajectoryDataToFile(outTLF, till_index);
    }
}
