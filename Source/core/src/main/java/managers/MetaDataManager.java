package managers;

import pipeline_components.Logger;

import java.util.ArrayList;

public class MetaDataManager {

    private static MetaDataManager instance = null;
    private int m;
    private int n;
    private int samplingRate;

    public static MetaDataManager getInstance(ArrayList<String> data) {
        if (instance == null && data != null) {
            instance = new MetaDataManager(data);
        }
        return instance;
    }

    public static MetaDataManager getInstance() {
        return getInstance(null);
    }

    public static int samplingRate() {
        return getInstance().getSamplingRate();
    }

    public static int m() {
        return getInstance().getHeight();
    }

    public static int n() {
        return getInstance().getWidth();
    }

    /**
     * Releases internally held references and their states.
     */
    public static void release() {
        instance = null;
    }

    /**
     * Parse info from meta data file.
     *
     * format: m,n,stepsize
     * @param data a list containing the elements in the same order as in the format specified.
     */
    public MetaDataManager(ArrayList<String> data) {
        this.m = Integer.parseInt(data.get(0));
        this.n = Integer.parseInt(data.get(1));
        this.samplingRate = Integer.parseInt(data.get(2));
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public int getHeight() {
        return m;
    }

    public int getWidth() {
        return n;
    }

    public static void reportStatus() {
        Logger.println("Read the following meta info:");
        Logger.println("+ Frame dimensions: (m,n)=(" + m() + "," + n() + ")");
        Logger.println("+ Sampling every " + samplingRate() + "th pixel");
    }
}
