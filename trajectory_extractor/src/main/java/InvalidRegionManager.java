import java.util.ArrayList;

public class InvalidRegionManager {

    private ArrayList<InvalidRegionsMask> magnitudes;

    private static InvalidRegionManager instance = null;

    public static InvalidRegionManager getInstance() {
        if (instance == null) {
            instance = new InvalidRegionManager();
        }
        return instance;
    }

    private InvalidRegionManager() {
        magnitudes = new ArrayList<InvalidRegionsMask>();
    }

    public void addMagFlow(InvalidRegionsMask flow) {
        magnitudes.add(flow);
    }

    public InvalidRegionsMask getMagFlow(int frame_idx) {
        return magnitudes.get(frame_idx);
    }


}

