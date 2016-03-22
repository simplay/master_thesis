package similarity;

import java.io.File;

/**
 * Utility singleton used to check the existence of certain dataset to simplify the
 * implementation of conditional runtime-behaviour.
 */
public class DataChecker {

    /**
     * Checks whether a given file exists.
     * @param fpathName file path name
     * @return true if file at given location exists, otherwise false.
     */
    public static boolean hasCalibrationData(String fpathName) {
        return new File(fpathName).exists();
    }
}
