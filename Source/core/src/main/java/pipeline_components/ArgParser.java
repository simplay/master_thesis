package pipeline_components;

import datastructures.NearestNeighborMode;
import datastructures.NearestNeighborsHeap;
import similarity.SimilarityTaskType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The ArgParser handles the inputs given by the user when running the application.
 *
 * Don't forget to provide it with the assigned user arguments.
 */
public class ArgParser {

    /**
     * A hash containing all user args, where
     * The key corresponds to the argument identifier,
     * and its value corresponds to the actual argument value,
     * encoded as a String.
     */
    private HashMap<String, String> arguments;

    // Singleton instance
    private static ArgParser instance = null;

    /**
     * Get the singleton
     *
     * @return the ArgParser singleton.
     */
    public static ArgParser getInstance() {
        return getInstance(null);
    }

    /**
     * Get the arg parser singleton and pass its arguments.
     *
     * @param args all provided user inputs
     * @return the ArgParser singleton.
     */
    public static ArgParser getInstance(String[] args) {
        if (instance == null) {
            instance = new ArgParser(args);
        }
        return instance;
    }

    /**
     * Releases this singleton.
     *
     * Calling a method of this singleton results in re-initializing
     * the internal state of the singleton.
     */
    public static void release() {
        instance = null;
    }

    /**
     * Process the user args
     *
     * @param args an array containing pairs of user args of the form
     *  -<ARG_ID> <ARG_VALUE>
     *  where ARG_ID corresponds to a known core arguments described in the readme
     *  and ARG_VALUE is a valid value for the corresponding argument.
     */
    private ArgParser(String[] args) {
        if (args != null) {
            arguments = new HashMap<>();

            if (args.length % 2 != 0) {
                throw new IllegalArgumentException();
            }

            for (int k = 0; k < args.length / 2; k++) {
                String key = args[2*k].split("-")[1];
                arguments.put(key, args[2*k+1]);
            }
        }
    }

    /**
     * Fetches a hash value by its key.
     *
     * @param key and argument identifier passed by the user input
     *  without the `-`
     * @return returns the associated argument value that belongs to the queried arg key.
     */
    public String getHashValue(String key) {
        return arguments.get(key);
    }

    /**
     * Get the name of the used dataset
     *
     * This name corresponds to a directory located at `./Data/`
     *
     * @return dataset name.
     */
    public static String getDatasetName() {
        return getInstance().getHashValue("d");
    }

    /**
     * Get the similarity task that maps to the given user input.
     *
     * @return the similarity task that should be run in order to compute the affinity matrix.
     */
    public static SimilarityTaskType getSimTask() {
        String taskName = getInstance().getHashValue("task");
        return SimilarityTaskType.TypeById(Integer.parseInt(taskName));
    }

    /**
     * Get the number of nearest neighbors per trajectory that should be dumped into a file.
     *
     * @return the neighbors of trajectories that should be dumped into the `_spnn.txt` file.
     */
    public static int getNearestNeighborhoodCount() {
        String nnCount = getInstance().getHashValue("nn");
        if (nnCount == null) {
            return 100;
        }
        return Integer.parseInt(nnCount);
    }

    /**
     * Should the assigned nearest neighborcount be skipped.
     *
     * @return true if we do not want to use the given nn count otherwise false.
     */
    public static boolean shouldSkipNNCount() {
        return getNNMode().getShouldIgnoreNNCount();
    }

    /**
     * This user specified flags allows to enable trajectory continuation.
     * Trajectories are appended and prepended by addition points, if possible.
     *
     * is only true if a user explicitly sets this flag equals 1.
     *
     * @return true if we want to continue the trajectories, otherwise false.
     */
    public static boolean shouldContinueTrajectories() {
        String ct = getInstance().getHashValue("ct");
        if (ct == null) {
            return false;
        }
        return (Integer.parseInt(ct) == 1);
    }
    /**
     * Affinity scaling factor used for MD sim tasks,
     * acts as a sensitivity parameter and is different when using depth cues.
     *
     * @return affinity md task scaling factor
     */
    public static double getLambda() {
        String lambda = getInstance().getHashValue("lambda");
        if (lambda == null) {
            return (ArgParser.useDepthCues())? 1000d : 0.1d;
        }
        return Double.parseDouble(lambda);
    }

    /**
     * Gives the appropriate depth field scale.
     *
     * Since we are using datasets from different sources (authors, methods), which all have
     * different depth ranges (and formats), an additional scale of the depth field values may be
     * required. This particular scale is given by this method.
     *
     * @return scaling factor of depth values to obtain measured depths in meters.
     */
    public static double getDepthFieldScale() {
        String depthScale = getInstance().getHashValue("dscale");
        if (depthScale == null) {
            // The depth fields measured by our own camera software are scaled by a factor of 1/5000
            return 1d;
        }
        return Double.parseDouble(depthScale);
    }

    /**
     * Indicates whether color cues should be used within a similarity task.
     * Note that color cues are only used for SD tasks.
     *
     * @return true if color cues should be used, otherwise false.
     */
    public static boolean useColorCues() {
        return getSimTask().usesColorCues();
    }

    /**
     * Does the program run in the debug state?
     * Forces to dumb many intermediate data.
     * by default a program does not run in the debug mode.
     *
     * @return true if specified by user via flag -debug 1
     */
    public static boolean runInDebugMode() {
        String isInDebugState = getInstance().getHashValue("debug");
        if (isInDebugState == null || !isInDebugState.equals("1")) {
            return false;
        }
        return true;
    }

    /**
     * Indicates whether depth cues should be used.
     * Depth cues can only be used if there was depth information extracted for the target dataset.
     *
     * Only some similarity tasks make use of depth cues.
     *
     * @return true if depth cures should be used, otherwise false.
     */
    public static boolean useDepthCues() {
        return getSimTask().getUsesDepthCues();
    }

    /**
     * Indicates whether the local flow variance should
     * be used for normalizing the motion distance when computing the similarity matrix.
     *
     * By default, we only normalize by the global variance.
     *
     * @return true, if we want to normalize the motion distances by the local flow variance,
     *  false otherwise.
     */
    public static boolean useLocalVariance() {
        String useVarState = getInstance().getHashValue("var");
        if (useVarState == null || !useVarState.equals("1")) {
            return false;
        }
        return true;
    }

    /**
     * Obtain the user-specified segmentation cut probability used for the PD task
     * Default value is 0.5.
     *
     * @return user specified cut probability or the pre-defined default value.
     */
    public static double getCutProbability() {
        String cutProb = getInstance().getHashValue("prob");
        if (cutProb == null) {
            return 0.5d;
        }
        return Double.parseDouble(cutProb);
    }

    /**
     * Obtain custom, user-specified output file name prefix.
     *
     * @return if no prefix is specified, return the empty string.
     */
    public static String getCustomFileNamePrefix() {
        String fnamePrefix = getInstance().getHashValue("prefix");
        if (fnamePrefix == null) {
            return "";
        }
        return fnamePrefix;
    }

    /**
     * Has the user provided a custom output filename prefix.
     * @return
     */
    public static boolean hasCustomFileNamePrefix() {
        return !getCustomFileNamePrefix().isEmpty();
    }

    /**
     * Return which nearest neighbors should be returned and dumped into the `_spnn.txt` file.
     * Currently, we either can return the best N nearest neighbors or
     * n/2 of the best and worst nearest neighbors per trajectory.
     *
     * by default all nearest neighbors are returned.
     *
     * @return the type of nearest neighbors that should be dumped.
     */
    public static NearestNeighborMode getNNMode() {
        String nnMode = getInstance().getHashValue("nnm");
        if (nnMode == null) {
            return NearestNeighborMode.ALL;
        }
        return NearestNeighborMode.getModeById(nnMode);
    }

    /**
     * Parse the user arguments in a more readable form.
     *
     * @return Pretty String variant of provided arguments.
     */
    public String toString() {
        String msg = "";
        Iterator it = arguments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            msg += "-" + pair.getKey() + " " + pair.getValue() + " ";
        }
        return msg.trim();
    }

    /**
     * Print the resulting state given a certain user input into the logger.
     */
    public static void reportUsedParameters() {
        Logger.print("Provided runtime args: ");
        Logger.println(getInstance().toString());
        Logger.println("Using the following runtime parameter setting:");
        Logger.println("+ Using dataset: " + getDatasetName());

        if (hasCustomFileNamePrefix()) {
            Logger.println("+ Prepending custom prefix: " + getCustomFileNamePrefix());
        }

        Logger.println("+ Running task: " + getSimTask().getName() + " [" + getSimTask().getIdName() + "]");
        Logger.println("+ Using local flow variances: " + useLocalVariance());
        Logger.println("+ Using depth cues: " + useDepthCues());
        Logger.println("+ Using depth variances: " + getSimTask().usesDepthVariance());
        Logger.println("+ Using color cues: " + useColorCues());
        Logger.println("+ Writing Nearest Neighbor Count: " + getNearestNeighborhoodCount());
        Logger.println("+ Using NN Mode: " + getNNMode().name());
        Logger.println("+ Applying trajectory continuation: " + shouldContinueTrajectories());

        if (shouldSkipNNCount()) {
            Logger.println("  => Ignoring the Nearest Neighbor Count and returning the complete neighborhood instead.");
        }

        Logger.println("+ Using Lambda equals: " + getLambda());

        if (useDepthCues()) {
            Logger.println("+ Scaling depth values by: " + getDepthFieldScale());
        }

        if (useColorCues()) {
            Logger.println("+ Using cut probability: " + getCutProbability());
        }

        Logger.println("+ Program runs in debug mode: " + runInDebugMode());
    }
}
