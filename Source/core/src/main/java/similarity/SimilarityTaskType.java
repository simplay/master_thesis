package similarity;

import managers.CalibrationManager;
import pipeline_components.ArgParser;

public enum SimilarityTaskType {
    SD(1, SumDistTask.class, true, false, false, "Sum of Distances", SumDistEuclidTask.class),
    PD(2, ProdDistTask.class, false, false, false, "Product of Distances", ProdDistEuclidTask.class),
    PED(3, ProdDistEuclidTask.class, false, true, false, "Product of Euclidian Distances"),
    SED(4, SumDistEuclidTask.class, true, true, false, "Sum of Euclidian Distances"),
    PAED(5, ProdDistAllEuclidTask.class, false, true, true, "Product of Distances all 3d"),
    PAENDD(6, ProdDistAllEuclidNoDepthVarTask.class, false, true, false, "Product of Distances all 3d without depth variance");

    private int value;
    private Class targetClass;
    private boolean usesColorCues;
    private boolean usesDepthVar;
    private boolean usesDepthCues;
    private String taskName;
    private Class alternativeTaskClass;

    private SimilarityTaskType(int value, Class targetClass, boolean usesColorCues, boolean usesDepthCues, boolean usesDepthVar, String taskName, Class alternativTaskClass) {
        this.value = value;
        this.targetClass = targetClass;
        this.usesColorCues = usesColorCues;
        this.usesDepthCues = usesDepthCues;
        this.usesDepthVar = usesDepthVar;
        this.taskName = taskName;
        this.alternativeTaskClass = alternativTaskClass;
    }

    private SimilarityTaskType(int value, Class targetClass, boolean usesColorCues, boolean usesDepthCues, boolean usesDepthVar, String taskName) {
        this(value, targetClass, usesColorCues, usesDepthCues, usesDepthVar, taskName, null);
    }

    public String getName() {
        return taskName;
    }

    /**
     * Checks whether the alternative task should be used.
     * This is the case whenever depth cues should be used but the color and depth camera are overlapping
     * and we are not already an alternative task.
     *
     * @return true if the alternative task should be used otherwise false.
     */
    public boolean shouldUseAlternativeTask() {
        if (alternativeTaskClass == null) return false;
        return CalibrationManager.hasNoIntrinsicDepthProjection() && ArgParser.useDepthCues();
    }

    public Class getAlternativeTaskClass() {
        return alternativeTaskClass;
    }

    public boolean usesDepthVariance() {
        return usesDepthVar;
    }

    /**
     * Indicates whether this Similarity task makes use of depth cues or not.
     *
     * @return true if this task makes use of depth fields, otherwise false.
     */
    public boolean getUsesDepthCues() {
        return usesDepthCues;
    }

    public Class getTaskClass() {
        return targetClass;
    }

    public static SimilarityTaskType TypeById(int id) {
        for (SimilarityTaskType t : values() ) {
            if (t.value == id) {
                return t;
            }
        }
        return null;
    }

    /***
     * Is the corresponding task required to use color cues?
     *
     * @return true if the tasks required color cues otherwise false.
     */
    public boolean usesColorCues() {
        return usesColorCues;
    }
}
