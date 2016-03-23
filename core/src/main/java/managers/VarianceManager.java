package managers;

import datastructures.VarianceMatrix;

import java.util.ArrayList;

public class VarianceManager {

    private static VarianceManager instance = null;
    private ArrayList<VarianceMatrix> localVariances;
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

    public VarianceMatrix getVariance(int frame_idx) {
        return localVariances.get(frame_idx);
    }

    public void add(VarianceMatrix varianceMatrix) {
        localVariances.add(varianceMatrix);
    }

    public void addGlobalVariance(double value) {
        globalVariances.add(value);
    }

    public double getGlobalVarianceValue(int frame_idx) {
        return globalVariances.get(frame_idx);
    }
}
