package readers;

import managers.MetaDataManager;

import java.util.ArrayList;

public class MetaInfoReader extends FileReader {

    private ArrayList<String> data;

    public MetaInfoReader(String dataset) {
        String baseFileName = "../output/tracker_data/" + dataset + "/metainfo.txt";
        data = new ArrayList<>();
        readFile(baseFileName);
        MetaDataManager.getInstance(data);
    }

    @Override
    protected void processLine(String line) {
        String[] lines = line.split(",");
        for (String l : lines) {
            data.add(l);
        }
    }
}
