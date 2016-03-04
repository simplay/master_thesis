import java.util.ArrayList;

/**
 * Created by simplay on 03/03/16.
 */
public class FlowFieldManager {

    private ArrayList<FlowField> forwardFlows;
    private ArrayList<FlowField> backwardFlows;
    private static FlowFieldManager instance = null;

    public static FlowFieldManager getInstance() {
        if (instance == null) {
            instance = new FlowFieldManager();
        }
        return instance;
    }

    public int m() {
        return forwardFlows.get(0).m();
    }

    public int n() {
        return forwardFlows.get(0).n();
    }

    private FlowFieldManager() {
        forwardFlows = new ArrayList<FlowField>();
        backwardFlows = new ArrayList<FlowField>();
    }

    public void addForwardFlow(FlowField flow) {
        forwardFlows.add(flow);
    }

    public FlowField getForwardFlow(int frame_idx) {
        return forwardFlows.get(frame_idx);
    }

    public void addBackwardFlow(FlowField flow) {
        forwardFlows.add(flow);
    }

    public FlowField getBackwardFlow(int frame_idx) {
        return forwardFlows.get(frame_idx);
    }
}
