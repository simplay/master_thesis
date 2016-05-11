package managers;

import datastructures.DepthVarField;
import java.util.ArrayList;

public class DepthVarManager {

    private static DepthVarManager instance = null;
    private ArrayList<DepthVarField> depthVarFields;

    public static DepthVarManager getInstance() {
        if (instance == null) {
            instance = new DepthVarManager();
        }
        return instance;
    }

    public static void release() {
        instance = null;
    }

    private DepthVarManager() {
        this.depthVarFields = new ArrayList<>();
    }

    public void add(DepthVarField depthVarField) {
        depthVarFields.add(depthVarField);
    }

    public DepthVarField get(int frame_idx) {
        return depthVarFields.get(frame_idx);
    }
}
