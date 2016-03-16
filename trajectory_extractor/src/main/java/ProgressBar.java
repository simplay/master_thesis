public class ProgressBar {
    private static ProgressBar instance = null;

    // The total number of extracted trajectories
    private double n;

    private int counter = 0;

    public static ProgressBar getInstance() {
        if (instance == null) {
            instance = new ProgressBar();
        }
        return instance;
    }

    private ProgressBar() {
        this.n = TrajectoryManager.getTrajectories().size();
    }

    public synchronized void incState() {
        counter++;
        if (counter % 200 == 0) {
            double rate = (counter / n)*100d;
            System.out.println("+ Progress: " + rate + "%");
        }
    }

    public static void reportStatus() {
        getInstance().incState();
    }

}
