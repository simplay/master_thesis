public class FlowVarFileReader extends FileReader{

    public FlowVarFileReader(String dataset, int fileNr) {
        String baseFileName = "../output/tracker_data/" + dataset + "/candidates_"+ fileNr + ".txt";
    }

    @Override
    protected void processLine(String line) {

    }
}
