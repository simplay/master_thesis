package managers;

import datastructures.DepthField;
import java.util.ArrayList;

public class DepthManager {

    private static DepthManager instance = null;
    private ArrayList<DepthField> depthFields;

    public static DepthManager getInstance() {
        if (instance == null) {
            instance = new DepthManager();
        }
        return instance;
    }

    private DepthManager() {
        this.depthFields = new ArrayList<>();
    }

    public void add(DepthField depthField) {
        depthFields.add(depthField);
    }

    public DepthField get(int frame_idx) {
        return depthFields.get(frame_idx);
    }
}
