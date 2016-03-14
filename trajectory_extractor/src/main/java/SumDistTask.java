import java.util.Collection;
import java.util.regex.Matcher;

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
        return 1.2* Math.random();
    }
}
