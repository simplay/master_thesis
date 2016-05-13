package managers;

import datastructures.FlowVarField;

import java.util.ArrayList;

public class VarianceManager {

    private static VarianceManager instance = null;
    private ArrayList<FlowVarField> localVariances;
    private ArrayList<Double> globalVariances;

    public static VarianceManager getInstance() {
        if (instance == null) {
            instance = new VarianceManager();
        }
        return instance;
    }

    private VarianceManager() {
        localVariances = new ArrayList<>();
        globalVariances = new ArrayList<>();
    }

    public FlowVarField getVariance(int frame_idx) {
        return localVariances.get(frame_idx);
    }

    public void add(FlowVarField flowVarField) {
        localVariances.add(flowVarField);
    }

    public void addGlobalVariance(double value) {
        globalVariances.add(value);
    }

    public double getGlobalVarianceValue(int frame_idx) {
        return globalVariances.get(frame_idx);
    }
}
