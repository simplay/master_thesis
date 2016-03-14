public class SumDistTask extends SimilarityTask {

    /**
     * @param a
     */
    public SumDistTask(Trajectory a) {
        super(a);
    }

    @Override
    protected double similarityBetween(Trajectory a, Trajectory b) {
        return 0;
    }
}
