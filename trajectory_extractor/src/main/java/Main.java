import datastructures.FlowField;
import managers.CalibrationManager;
import managers.MetaDataManager;
import managers.TrajectoryManager;
import readers.*;
import pipeline_components.AffinityCalculator;
import pipeline_components.ArgParser;
import pipeline_components.Tracker;
import writers.*;

import java.io.*;
// TODO write more descriptive information:
// value range of input data, invalid values, what they do, where they are used
// what data is required / optional
// where it is computed and stored
/**
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
 * Supported user args
 *
 *  required args:
 *      -d => dataset that should be used
 *      -task => similarity method that should be used
 *          1 => runs ProdDistTask
 *      -var => should the local variance be used
 *          -var 1 => use the local flow variance values for normalization
 *          -var 0 => use the global flow variance values.
 *      -depth => should depth cues be used
 *          -depth 1 => use depth cues
 *      -nn => number of nearest neighbors that should be saved per trajectory
 *          -nn 1000 => save the top 1000 nearest neighbors, default value is 100
 *      -debug => should the program run in the debug mode. If so, it will dumb intermediate calculated data
 *          -debug 1 => run in debug mode, otherwise in normal mode, by default a program runs in normal mode.
 *  @example:
 *      -d c14 -task 1
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
        }

        System.out.println("Starts computing affinity values between remaining trajectories...");
        long beforeAffCompTime = System.currentTimeMillis();
        new AffinityCalculator();
        long afterAffCompTime = System.currentTimeMillis();
        System.out.println("Computing similarity values took " + ((afterAffCompTime-beforeAffCompTime)/1000d)+ "s");

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
