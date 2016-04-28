package datastructures;

import java.util.ArrayList;
import java.util.HashMap;

public class LabeledFile {

    private final HashMap<String, LabeledFileLine> lines = new HashMap<>();
    private boolean hasRGB;
    private boolean hasDepth;
    private boolean hasExtrinsicMat;

    public LabeledFile(ArrayList<LabeledFileLine> calibrations) {
        for (LabeledFileLine calibration : calibrations) {
            if (calibration.getLabel().contains("_rgb")) hasRGB = true;
            if (calibration.getLabel().contains("_d")) hasDepth = true;
            if (calibration.getLabel().contains("e_")) hasExtrinsicMat = true;
            lines.put(calibration.getLabel(), calibration);
        }
    }

    public LabeledFileLine getLineByLabel(String label) {
        return lines.get(label);
    }

    public boolean hasRGB() {
        return hasRGB;
    }

    public boolean hasDepth() {
        return hasDepth;
    }

    public boolean hasHasExtrinsicMat() {
        return hasExtrinsicMat;
    }
}
