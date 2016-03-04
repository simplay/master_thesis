import java.util.ArrayList;

public class FlowMagManager {

    private ArrayList<FlowMagnitudeField> magnitudes;

    private static FlowMagManager instance = null;

    public static FlowMagManager getInstance() {
        if (instance == null) {
            instance = new FlowMagManager();
        }
        return instance;
    }

    private FlowMagManager() {
        magnitudes = new ArrayList<FlowMagnitudeField>();
    }

    public void addMagFlow(FlowMagnitudeField flow) {
        magnitudes.add(flow);
    }

    public FlowMagnitudeField getMagFlow(int frame_idx) {
        return magnitudes.get(frame_idx);
    }


}

