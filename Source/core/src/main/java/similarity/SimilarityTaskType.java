package similarity;

/**
 * SimilarityTaskType models the properties and internal state of similarity tasks
 * used for computing affinities between trajectories.
 *
 */
public enum SimilarityTaskType {

    SD(1, SumDistTask.class, true, false, false, "Sum of Distances"),
    PD(2, ProdDistTask.class, false, false, false, "Product of Distances"),
    PED(3, ProdDistEuclidTask.class, false, true, false, "Product of Euclidian Distances"),
    SED(4, SumDistEuclidTask.class, true, true, false, "Sum of Euclidian Distances"),
    PAED(5, ProdDistAllEuclidTask.class, false, true, true, "Product of Distances all 3d"),
    PAENDD(6, ProdDistAllEuclidNoDepthVarTask.class, false, true, false, "Product of Distances all 3d without depth variance");

    // Unique numeric identifier of a SimilarityTaskType
    private int id;

    // Similarity class that should be used for affinity computation
    private Class similarityTaskClass;

    // Indicates whether this similarity requires color cues.
    private boolean usesColorCues;

    // Indicates whether this similarity requires depth variance fields.
    private boolean usesDepthVar;

    // Indicates whether this similarity requires depth cues.
    private boolean usesDepthCues;

    // Pretty string name of this task that can be used for naming dumped output files.
    private String taskName;

    /**
     * Constructor of a similarity task type.
     *
     * @param id unique numeric identifier of every similarity task type.
     * @param similarityTaskClass the class of a SimiarityTask descendant class used for computing the affinities.
     * @param usesColorCues does this task require color cues.
     * @param usesDepthCues does this task require depth cues.
     * @param usesDepthVar does this taks make use of depth variance.
     * @param taskName the logger representation of the task name.
     */
    private SimilarityTaskType(int id, Class similarityTaskClass, boolean usesColorCues, boolean usesDepthCues, boolean usesDepthVar, String taskName) {
        this.id = id;
        this.similarityTaskClass = similarityTaskClass;
        this.usesColorCues = usesColorCues;
        this.usesDepthCues = usesDepthCues;
        this.usesDepthVar = usesDepthVar;
        this.taskName = taskName;
    }

    /**
     * Get the task name for logging purposes
     *
     * @return logger representation of task name.
     */
    public String getName() {
        return taskName;
    }

    /**
     * Get the abbreviation of a particular task.
     * Corresponds to the lower-cased Enum identifier.
     *
     * Is used for naming generated output files.
     *
     * @return pretty string representation of the name of the used similarity task.
     */
    public String getIdName() {
        return name().toLowerCase();
    }

    /**
     * Indicates whether this SimilarityTaskType makes use of depth variances
     *
     * @return true if the task uses depth variances, false otherwise.
     */
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

    /**
     * Get the class of the similarity task that should be used for
     * computing affinity values.
     *
     * Note that there is no default task assigned.
     *
     * @return the class of a SimiarityTask descendant class.
     */
    public Class getTaskClass() {
        return similarityTaskClass;
    }

    /**
     * Find a similarity task by its unique identifier.
     *
     * Null is returned, if no task for a given id was found.
     *
     * @param id unique similarity task type identifier.
     * @return the SimilarityTaskType that maps to the given id.
     */
    public static SimilarityTaskType TypeById(int id) {
        for (SimilarityTaskType t : values() ) {
            if (t.id == id) {
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
