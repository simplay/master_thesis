import datastructures.FlowField;
import managers.MetaDataManager;
import managers.TrajectoryManager;
import readers.*;
import pipeline_components.AffinityCalculator;
import pipeline_components.ArgParser;
import pipeline_components.Tracker;
import writers.*;
import java.io.*;

// TODO allow to define which nearest neighbors should be chosen: top N, top N + worst N/2, top N/2 + distinct(rand(N/2))
/**
 * Runs the core pipeline:
 *
 *  1. Parse runtime args and load input data
 *  2. Perform point tracking:
 *      2.1. start new trackings at candidate locations
 *      2.2. continue existing trackings
 *  3. Extract trajectories from tracked coherent points
 *  4. Compute the affinity matrix formed by the trajectory similarities.
 *
 * The following input data (frame-wise) is always read into memory:
 *  tracking candidate locations
 *  invalid tracking regions
 *  forward-and backward flow fields
 *  local and global flow variance values
 *
 *  optional input data:
 *      cie lab color values
 *      depth fields: not that the value zero indicates invalid pixel regions.
 *
 * For a complete description of all supported runtime arguments, please have a look at the README.md file.
 *
 */
public class Main {
    public static void main(String[] argv) {
        long startTime = System.currentTimeMillis();
        String output_base_path = "../output/trajectories/";
        ArgParser.getInstance(argv);

        // The number of spatially nearest neighbors per trajectory that should be written into an output file.
        int numberOfNNToSave = ArgParser.getNearestNeighborhoodCount();

        String dataset = ArgParser.getDatasetName();
        ArgParser.reportUsedParameters();
        System.out.println();
        new MetaInfoReader(dataset);
        int samplingRate = MetaDataManager.samplingRate();
        MetaDataManager.reportStatus();
        System.out.println();

        /**
         * Read required input data
         */

        File folder = new File("../output/tracker_data/" + dataset);
        File[] fileList = folder.listFiles();
        int counter = 0;
        for (File file : fileList) {
            if (file.getName().matches("candidates_[\\d]+.txt")) {
                counter++;
            }
        }
        System.out.println("Loading input data...");
        System.out.println();

        /**
         * For a dataset consisting of a image sequence of N frames load
         *  + N-1 flow files
         *  + N-1 candidates
         *  + N   color files
         *  + N-1 consistency images
         *  + N   depth images
         */
        int till_index = counter;
        for (int idx = 1; idx <= till_index; idx++) {
            String fileNr = Integer.toString(idx);
            new CandidateFileReader(dataset, fileNr);
            new FlowFileReader(dataset, FlowField.FORWARD_FLOW, fileNr);
            new FlowFileReader(dataset, FlowField.BACKWARD_FLOW, fileNr);
            new InvalidRegionReader(dataset, fileNr);
            new FlowVarFileReader(dataset, fileNr);
            new GlobalVarFileReader(dataset);

            // optionally loaded cue-data
            if (ArgParser.useColorCues()) new ColorImageReader(dataset, fileNr);
            if (ArgParser.useDepthCues()) new DepthFieldReader(dataset, fileNr);
        }
        String fileNr = Integer.toString(till_index+1);
        if (ArgParser.useColorCues()) new ColorImageReader(dataset, fileNr);
        if (ArgParser.useDepthCues()) new DepthFieldReader(dataset, fileNr);

        // load relevant transformation data in order to transform pixel coordinates to euclid. coordinates,
        // using depth cues and applying the appropriate extrinsic and intrinsic transformations.
        if (ArgParser.useDepthCues()) new CalibrationsReader(dataset);

        long tillFileLoadedTime = System.currentTimeMillis();
        System.out.println("Files loaded in "+((tillFileLoadedTime-startTime)/1000d) +"s...");
        System.out.println();

        /**
         * Extract trajectories
         */

        System.out.println("Sampling every " + samplingRate + "th pixel");
        System.out.println("Tracking points over " + counter + " frames...");
        System.out.println();
        new Tracker(till_index, samplingRate);

        long tillTrajectoriesTrackedTime = System.currentTimeMillis();
        System.out.println("Tracking took " + ((tillTrajectoriesTrackedTime-tillFileLoadedTime)/1000d)+ "s");

        System.out.println();
        System.out.println("Number of extracted trajectories: "+ TrajectoryManager.getInstance().trajectoryCount());
        for (int k = 0; k <= till_index+1; k++) {
            int trajectoryCount = TrajectoryManager.getInstance().allTrajectoryWithLength(k).size();
            System.out.println("#Trajectories with len=" + k + ": " + trajectoryCount);
        }
        System.out.println();

        /**
         * Filter extracted trajectories
         */

        // one pointed trajectories have a length of 0.
        System.out.println("Filtering 1-pointed trajectories...");

        TrajectoryManager.getInstance().filterOnePointedTrajectories();
        System.out.println("Filtered too short trajectories...");
        System.out.println("Number of remaining trajectories: "+ TrajectoryManager.getInstance().trajectoryCount());

        // Transform trajectory points to euclidian space
        if (ArgParser.useDepthCues()) {
            TrajectoryManager.getInstance().transformTrajectoryPointsToEuclidianSpace();

            // all remaining trajectories exhibit at least one tracking point that has a valid depth value associated.
            TrajectoryManager.getInstance().filterDeletableTrajectories();
            System.out.println("Remaining trajectories after depth transformations: " + TrajectoryManager.getInstance().trajectoryCount());
        }

        System.out.println("Starts computing affinity values between remaining trajectories...");
        long beforeAffCompTime = System.currentTimeMillis();
        new AffinityCalculator();
        long afterAffCompTime = System.currentTimeMillis();
        System.out.println("Computing similarity values took " + ((afterAffCompTime-beforeAffCompTime)/1000d)+ "s");
        System.out.println();

        TrajectoryManager.getInstance().filterNoSimilarityTrajectories();
        System.out.println("Remaining trajectories after post filtering: " + TrajectoryManager.getInstance().trajectoryCount());
        System.out.println();

        /**
         * Write output data
         *  + extracted trajectories
         *  + the frame-wise trajectory points
         *  + the similarity matrix
         *  + the label mappings: transformation which column/row a label belongs to in the similarity matrix
         */

        if (ArgParser.runInDebugMode()) new TraWriter(output_base_path, dataset, till_index);
        if (ArgParser.runInDebugMode()) new FramewiseActiveTraWriter(dataset, till_index);

        // Write clustering related files
        new SimilarityWriter(dataset);
        new LabelMappingWriter(dataset);
        new NearestSpatialNeighborsWriter(dataset, numberOfNNToSave);

        long tillFinishedTime = System.currentTimeMillis();
        System.out.println("Total elapsed time: " + ((tillFinishedTime-startTime)/1000d)+ "s");
    }
}
