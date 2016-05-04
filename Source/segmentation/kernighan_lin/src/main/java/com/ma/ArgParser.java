package com.ma;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ArgParser {
    private static ArgParser instance = null;
    private HashMap<String, String> arguments;

    public static ArgParser getInstance() {
        return getInstance(null);
    }

    public static ArgParser getInstance(String[] args) {
        if (instance == null) {
            instance = new ArgParser(args);
        }
        return instance;
    }

    private ArgParser(String[] args) {
        if (args != null) {
            arguments = new HashMap<>();
        }

        for (int k = 0; k < args.length / 2; k++) {
            String key = args[2*k].split("-")[1];
            arguments.put(key, args[2*k+1]);
        }
    }

    public String getHashValue(String key) {
        return arguments.get(key);
    }

    /**
     * Get the target dataset identifier located at
     * "../../output/similarities/"
     *
     * @return a name identifying an existing set of files
     *  (similarity, neighbors).
     */
    public static String getDatasetName() {
        return getInstance().getHashValue("d");
    }

    /**
     * Number of clusters we want the graph to partition into.
     * By default we use 2 clusters.
     *
     * @return
     */
    public static int getClusterCount() {
        String cc = getInstance().getHashValue("cc");
        if (cc == null) {
            return 2;
        }
        return Integer.parseInt(cc);
    }

    /**
     * The number of dummy vertices put into a cluster set.
     * By default this is 0.
     * Used as a filler to have equi-filled sets.
     *
     * @return number of dummy vertices put into a set.
     */
    public static int getDummyCount() {
        String dc = getInstance().getHashValue("dc");
        if (dc == null) {
            return 0;
        }
        return Integer.parseInt(dc);
    }

    /**
     * Which initial set partition should be used
     *
     * @return initial balanced set partition.
     */
    public static InitialPartitionMode getInitialPartitionMode() {
        String ipm = getInstance().getHashValue("ipm");
        if (ipm == null) {
            return InitialPartitionMode.EMPTY_FULL;
        }
        return InitialPartitionMode.getModeByName(ipm);
    }

    /**
     * Get the maximal number of used nearest neighbors.
     * Since we have a naming convention that also
     * encoded the number of nearest neighbors, we can parse this number.
     *
     * @return number of max. used nearest neighbors.
     */
    public static int maxNearestNeighborCount() {
        String[] splits = getDatasetName().split("_");
        for (String split : splits) {
            if (split.matches("[-+]?\\d*\\.?\\d+")) {
                return Integer.parseInt(split);
            }
        }
        return 0;
    }

    /**
     * The number of maximal iteration until we skip convergence computation.
     * By default this is 1.
     *
     * @return
     */
    public static int getMaxIterCountPerCluster() {
        String mic = getInstance().getHashValue("mic");
        if (mic == null) {
            return 1;
        }
        return Integer.parseInt(mic);
    }

    /**
     * How many times should the energy be optimized.
     * We optimize with the current assignment of the cluster sets.
     * Note that repetitions are only required for graphs that
     * are segmented into more than two clusters.
     *
     * @return number of computation repetitions using the state of the previous computation.
     */
    public static int getRepetitionCount() {
        String rc = getInstance().getHashValue("rc");
        if (rc == null) {
            return 0;
        }
        return Integer.parseInt(rc);
    }

    /**
     * The prefix of the output files.
     *
     * @return
     */
    public static String getCustomFileNamePrefix() {
        String fnamePrefix = getInstance().getHashValue("prefix");
        if (fnamePrefix == null) {
            return "";
        }
        return fnamePrefix;
    }

    public static boolean hasCustomFileNamePrefix() {
        return !getCustomFileNamePrefix().isEmpty();
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
        Logger.println("+ Number of max. nearest neighbors: " + maxNearestNeighborCount());
        Logger.println("+ Using initial partition mode: " + getInitialPartitionMode().getName());
        Logger.println("+ Number of clusters: " + getClusterCount());
        Logger.println("+ Max Iterations per Cluster: " + getMaxIterCountPerCluster());
        Logger.println("+ Number of calculation repetitions: " + getRepetitionCount());
        if (hasCustomFileNamePrefix()) {
            Logger.println("+ Using custom prefix: " + getCustomFileNamePrefix());
        }
    }

}
