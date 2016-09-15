package managers;

import pipeline_components.Logger;
import java.util.ArrayList;

/**
 * MetaDataManager contains relevant meta data extracted by the `init_data.m` script.
 *
 * Such data are the dataset image resolutions and the used sampling rate.
 *
 * A meta data file in located at `./Data/DATASET/meta/dataset.txt`
 */
public class MetaDataManager {

    // The meta data singleton
    private static MetaDataManager instance = null;

    // Dataset image height, i.e. the number of rows
    private int m;

    // Dataset image width, i.e the number of columns.
    private int n;

    // Sample every samplingRate-th pixel
    private int samplingRate;

    // The number of dataset frames
    private int frameCount;

    /**
     * Obtain and set the state of the metadata singleton.
     *
     * @param data File line items.
     * @return singleton
     */
    public static MetaDataManager getInstance(ArrayList<String> data) {
        if (instance == null && data != null) {
            instance = new MetaDataManager(data);
        }
        return instance;
    }

    /**
     * Obtain the singleton instance.
     *
     * @return singleton
     */
    public static MetaDataManager getInstance() {
        return getInstance(null);
    }

    /**
     * Retrieves the sampling rate that should be used within the pipeline.
     *
     * Used within the trajectory tracing stage and determines the density of the tracking.
     *
     * @return sampling rate of pixels
     */
    public static int samplingRate() {
        return getInstance().getSamplingRate();
    }

    /**
     * The number of rows / height of an image.
     *
     * @return row count
     */
    public static int m() {
        return getInstance().getHeight();
    }

    /**
     * The number of columns / width of an image.
     *
     * @return column count
     */
    public static int n() {
        return getInstance().getWidth();
    }


    /**
     * Get the number of available dataset frames for the used dataset.
     *
     * @return frame count of target dataset.
     */
    public static int frameCount() {
        return getInstance().getFrameCount();
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

    /**
     * Retrieves the sampling rate that should be used within the pipeline.
     *
     * Used within the trajectory tracing stage and determines the density of the tracking.
     *
     * @return sampling rate of pixels
     */
    public int getSamplingRate() {
        return samplingRate;
    }

    /**
     * The number of rows / height of an image.
     *
     * @return row count
     */
    public int getHeight() {
        return m;
    }

    /**
     * The number of columns / width of an image.
     *
     * @return column count
     */
    public int getWidth() {
        return n;
    }

    /**
     * Get the number of available dataset frames for the used dataset.
     *
     * @return frame count of target dataset.
     */
    public int getFrameCount() {
        return frameCount;
    }

    /**
     * Assign the number of available dataset frames.
     *
     * @param frameCount dataset frame count.
     */
    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    /**
     * Report the extracted meta data.
     */
    public static void reportStatus() {
        Logger.println("Read the following meta info:");
        Logger.println("+ Frame dimensions: (m,n)=(" + m() + "," + n() + ")");
        Logger.println("+ Sampling every " + samplingRate() + "th pixel");
    }
}
