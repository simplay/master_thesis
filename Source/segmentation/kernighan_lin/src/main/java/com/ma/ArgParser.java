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

    public static String getDatasetName() {
        return getInstance().getHashValue("d");
    }


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
        if (hasCustomFileNamePrefix()) {
            System.out.println("+ Using custom prefix: " + getCustomFileNamePrefix());
        }
    }

}
