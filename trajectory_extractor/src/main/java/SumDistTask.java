import java.util.Collection;

public class SumDistTask extends SimilarityTask {


    /**
     * @param a
     * @param trajectories
     */
    public SumDistTask(Trajectory a, Collection<Trajectory> trajectories) {
        super(a, trajectories);
    }

    @Override
    protected double similarityBetween(Trajectory a, Trajectory b) {
        return 0;
    }
}
