package pipeline_components;

import datastructures.NearestNeighborsHeap;
import similarity.SimilarityTaskType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ArgParser {

    private boolean hasArgsAssigned = false;
    private HashMap<String, String> arguments;

    private static ArgParser instance = null;


    public static ArgParser getInstance() {
        return getInstance(null);
    }

    public static ArgParser getInstance(String[] args) {
        if (instance == null) {
            instance = new ArgParser(args);
        }
        return instance;
    }

    public static boolean hasArgs() {
        return getInstance().hasArgsAssigned;
    }

    private ArgParser(String[] args) {
        if (args != null) {
            hasArgsAssigned = true;
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
     * Fetches hesh values by their key
     * @param key
     * @return
     */
    public String getHashValue(String key) {
        return arguments.get(key);
    }

    public static String getDatasetName() {
        return getInstance().getHashValue("d");
    }

    public static SimilarityTaskType getSimTask() {
        String taskName = getInstance().getHashValue("task");
        return SimilarityTaskType.TypeById(Integer.parseInt(taskName));
    }

    public static int getNearestNeighborhoodCount() {
        String nnCount = getInstance().getHashValue("nn");
        if (nnCount == null) {
            return 100;
        }
        return Integer.parseInt(nnCount);
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

    public static boolean useDepthCues() {
        return getSimTask().getUsesDepthCues();
    }

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


    public static NearestNeighborsHeap.NNMode getNNMode() {
        String nnMode = getInstance().getHashValue("nnm");
        if (nnMode == null) {
            return NearestNeighborsHeap.NNMode.TOP_N;
        }
        return NearestNeighborsHeap.NNMode.getModeById(nnMode);
    }

    public String toString() {
        String msg = "";
        Iterator it = arguments.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            msg += "-" + pair.getKey() + " " + pair.getValue() + " ";
        }
        return msg.trim();
    }

    public static void reportUsedParameters() {
        Logger.print("Provided runtime args: ");
        Logger.println(getInstance().toString());
        Logger.println("Using the following runtime parameter setting:");
        Logger.println("+ Using dataset: " + getDatasetName());

        if (hasCustomFileNamePrefix()) {
            Logger.println("+ Using custom prefix: " + getCustomFileNamePrefix());
        }

        Logger.println("+ Running task: " + getSimTask().getName() + " [" + getSimTask().getIdName() + "]");
        Logger.println("+ Using local flow variances: " + useLocalVariance());
        Logger.println("+ Using depth cues: " + useDepthCues());
        Logger.println("+ Using depth variances: " + getSimTask().usesDepthVariance());
        Logger.println("+ Using color cues: " + useColorCues());
        Logger.println("+ Writing Nearest Neighbor Count: " + getNearestNeighborhoodCount());
        Logger.println("+ Using NN Mode: " + getNNMode().name());
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
