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
        System.out.print("Provided runtime args: ");
        System.out.println(getInstance().toString());
        System.out.println("Using the following runtime parameter setting:");
        System.out.println("+ Using dataset: " + getDatasetName());
        System.out.println("+ Number of clusters: " + getClusterCount());
        System.out.println("+ Max Iterations per Cluster: " + getMaxIterCountPerCluster());
        System.out.println("+ Number of calculation repetitions: " + getRepetitionCount());
        if (hasCustomFileNamePrefix()) {
            System.out.println("+ Using custom prefix: " + getCustomFileNamePrefix());
        }
    }

}