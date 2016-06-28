import datastructures.FlowField;
import managers.*;
import pipeline_components.Logger;
import readers.*;
import pipeline_components.AffinityCalculator;
import pipeline_components.ArgParser;
import pipeline_components.Tracker;
import similarity.SimilarityTask;
import writers.*;
import java.io.*;

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
        ArgParser.getInstance(argv);

        String dataset = ArgParser.getDatasetName();
        ArgParser.reportUsedParameters();
        Logger.println();
        new MetaInfoReader(dataset);
        int samplingRate = MetaDataManager.samplingRate();
        MetaDataManager.reportStatus();
        Logger.println();

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

        // Minus one, since the first frame index maps to the index value 0
        MetaDataManager.getInstance().setFrameCount(counter - 1);

        Logger.println("Loading input data...");
        Logger.println();

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
            if (ArgParser.useDepthCues()) {
                new DepthFieldReader(dataset, fileNr);
                if (ArgParser.getSimTask().usesDepthVariance()) new DepthVarReader(dataset, fileNr);
            }
        }
        String fileNr = Integer.toString(till_index+1);
        if (ArgParser.useColorCues()) new ColorImageReader(dataset, fileNr);
        if (ArgParser.useDepthCues()) {
            new DepthFieldReader(dataset, fileNr);
            if (ArgParser.getSimTask().usesDepthVariance()) new DepthVarReader(dataset, fileNr);
        }

        // load relevant transformation data in order to transform pixel coordinates to euclid. coordinates,
        // using depth cues and applying the appropriate extrinsic and intrinsic transformations.
        if (ArgParser.useDepthCues()) new CalibrationsReader(dataset);

        long tillFileLoadedTime = System.currentTimeMillis();
        Logger.println("Files loaded in "+((tillFileLoadedTime-startTime)/1000d) +"s...");
        Logger.println();

        /**
         * Extract trajectories
         */

        Logger.println("Sampling every " + samplingRate + "th pixel");
        Logger.println("Tracking points over " + counter + " frames...");
        Logger.println();
        new Tracker(till_index, samplingRate);

        long tillTrajectoriesTrackedTime = System.currentTimeMillis();
        Logger.println("Tracking took " + ((tillTrajectoriesTrackedTime-tillFileLoadedTime)/1000d)+ "s");
        FlowFieldManager.release();
        Logger.println();
        Logger.println("Number of extracted trajectories: "+ TrajectoryManager.getInstance().trajectoryCount());
        for (int k = 0; k <= till_index + 1; k++) {
            int trajectoryCount = TrajectoryManager.getInstance().allTrajectoryWithLength(k).size();
            Logger.println("#Trajectories with len=" + k + ": " + trajectoryCount);
        }
        Logger.println();

        /**
         * Filter extracted trajectories
         */

        // one pointed trajectories have a length of 0.
        Logger.println("Filtering 1-pointed trajectories...");
        TrajectoryManager.getInstance().filterOnePointedTrajectories();

        Logger.println("Filtered too short trajectories...");
        Logger.println("=> Number of remaining trajectories: "+ TrajectoryManager.getInstance().trajectoryCount());

        int trajectoryLen = ArgParser.getMinExpectedTrajectoryLength();
        Logger.println("Filtering all trajectory shorter than " + trajectoryLen + "...");
        TrajectoryManager.getInstance().filterTrajectoriesShorterThanMinLen(trajectoryLen);
        Logger.println("=> Number of remaining trajectories: "+ TrajectoryManager.getInstance().trajectoryCount());
        Logger.println();

        /**
         * Extend trajectories
         */

        if (ArgParser.shouldContinueTrajectories()) {
            // TODO: should this be a user tune-able factor?
            int trajContMinLen = 6;
            Logger.println("Applying continuation to all extracted trajectories having length equals " + trajContMinLen + "...");
            TrajectoryManager.getInstance().continueTrajectories(trajContMinLen);
            Logger.println(" => Trajectories have been extended.");
            Logger.println();
        }

        /**
         * Transform computed data
         */

        if (CalibrationManager.shouldWarpDepthFields() && ArgParser.useDepthCues()) {
            Logger.println("Color and Depth Cameras do not overlap.");
            Logger.println("Warping depth fields...");

            DepthManager.warpDepthFields();
            Logger.println("=> Warped depth fields.");
        }

        // Transform trajectory points to euclidian space
        if (ArgParser.useDepthCues()) {
            TrajectoryManager.getInstance().transformTrajectoryPointsToEuclidianSpace();

            // all remaining trajectories exhibit at least one tracking point that has a valid depth value associated.
            TrajectoryManager.getInstance().filterDeletableTrajectories();
            Logger.println("=> Remaining trajectories after depth transformations: " + TrajectoryManager.getInstance().trajectoryCount());
        }

        /**
         * Compute Affinity Matrix and filter zero contribution trajectories
         */

        Logger.println("Starts computing affinity values between remaining trajectories...");
        long beforeAffCompTime = System.currentTimeMillis();
        new AffinityCalculator();
        long afterAffCompTime = System.currentTimeMillis();
        Logger.println("Computing similarity values took " + ((afterAffCompTime-beforeAffCompTime) / 1000d)+ "s");
        Logger.println();

        TrajectoryManager.getInstance().filterNoSimilarityTrajectories();

        int similarityThreshold = 20;
        Logger.println("Filtering too weak trajectories (having fewer than " + similarityThreshold + " similarities assigned) ...");
        int tooWeakCount = TrajectoryManager.filterTooWeakTrajectories(similarityThreshold);
        Logger.println("=> Filtered " + tooWeakCount + " trajectories.");

        Logger.println("=> Remaining trajectories after post filtering: " + TrajectoryManager.getInstance().trajectoryCount());
        Logger.println();

        /**
         * Write output data
         *  + extracted trajectories
         *  + the frame-wise trajectory points
         *  + the similarity matrix
         *  + the label mappings: transformation which column/row a label belongs to in the similarity matrix
         */
        System.gc();
        if (ArgParser.runInDebugMode()) new TrajectoryWriter(dataset, till_index);
        new FramewiseActiveTraWriter(dataset, till_index);

        // Write clustering related files
        new SimilarityWriter(dataset);
        new LabelMappingWriter(dataset);
        new NearestSpatialNeighborsWriter(dataset);

        long tillFinishedTime = System.currentTimeMillis();
        Logger.println();
        Logger.println("Total elapsed time: " + ((tillFinishedTime-startTime) / 1000d)+ "s");

        // Write logger status file to "../output/logs/"
        Logger.writeLog();
    }
}
