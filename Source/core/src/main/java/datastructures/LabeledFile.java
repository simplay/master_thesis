package datastructures;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * LabeledFile represents calibration files supporting our defined file format.
 *
 * A labeled file models a text file consisting of a key-value pair per line:
 * an identifier (the so called label) followed by its actual value (similar like in yaml files).
 *
 * Abstractly formulated, a line looks like the following: <LABEL_ID>: <LABEL_VALUE>
 * where the <LABEL_ID> is a known string identifier (by the system),
 * and <LABEL_VALUE> is its corresponding value.
 *
 * Note that the ':' and also the white-space are required tokens in order to
 * define a valid format.
 *
 * Example calibration file:
 *  f_d: 504.261 503.905
 *  p_d: 352.457 272.202
 *  f_rgb: 573.71 574.394
 *  p_rgb: 346.471 249.031
 *  e_1: 0.999749 0.00518867 0.0217975 0.0243073
 *  e_2: -0.0051649 0.999986 -0.0011465 -0.000166518
 *  e_3: -0.0218031 0.00103363 0.999762 0.0151706
 *
 *  Supported labels and their meaning:
 *
 *      // required in case we are using depth cues
 *      f_d: focal depth point, two float numbers
 *      p_d: principal point of depth camera, two float numbers
 *
 *      // required the depth and color cameras are not aligned.
 *      f_rg: focal rgb point, two float numbers
 *      p_rgb: principal point of rgb camera, two float numbers
 *
 *      e_1: 1st row of extrinsic matrix, 4 float numbers
 *      e_2: 2nd row of extrinsic matrix, 4 float numbers
 *      e_3: 3rd row of extrinsic matrix, 4 float numbers
 *
 * Please not that the calibration file is located at `./Data/<dataset>/meta/calibration.txt`
 * For further information please read the README.md located at `./Data`
 *
 */
public class LabeledFile {

    // The read-in file lines: key is the label, value is the assigned label value.
    private final HashMap<String, LabeledFileLine> lines = new HashMap<>();

    // Was any rgb related information read
    private boolean hasRGB;

    // Was any depth related information read
    private boolean hasDepth;

    // Was an extrinsic calibration matrix red
    private boolean hasExtrinsicMat;

    /**
     * Create a new calibration file
     *
     * @param calibrations the read calibration lines.
     */
    public LabeledFile(ArrayList<LabeledFileLine> calibrations) {
        for (LabeledFileLine calibration : calibrations) {
            if (calibration.getLabel().contains("_rgb")) hasRGB = true;
            if (calibration.getLabel().contains("_d")) hasDepth = true;
            if (calibration.getLabel().contains("e_")) hasExtrinsicMat = true;
            lines.put(calibration.getLabel(), calibration);
        }
    }

    /**
     * Retrieves a line by its label identifier.
     *
     * @param label identifier of target line
     * @return label value for the fiven identifier.
     */
    public LabeledFileLine getLineByLabel(String label) {
        return lines.get(label);
    }

    /**
     * Checks whether there was any rgb information read-in.
     * 
     * @return true, if rgb information was read, false otherwise.
     */
    public boolean hasRGB() {
        return hasRGB;
    }

    /**
     * Checks whether there was any depth information read-in.
     *
     * @return true, if depth information was read, false otherwise.
     */
    public boolean hasDepth() {
        return hasDepth;
    }

    /**
     * Checks whether there was any extrinsic calibration information read-in.
     *
     * @return true, if extrinsic calibrations were read, false otherwise.
     */
    public boolean hasHasExtrinsicMat() {
        return hasExtrinsicMat;
    }
}
