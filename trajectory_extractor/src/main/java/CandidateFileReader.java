import java.io.*;

/**
 * Created by simplay on 03/03/16.
 */
public class CandidateFileReader {
    public CandidateFileReader(String dataset, String fileNr) {

        String baseFileName = "../output/tracker_data/" + dataset + "/candidates_"+ fileNr + ".txt";

        File f = new File(baseFileName);
        System.out.println("Expecting data in Folder : " + f.getAbsolutePath());

        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(baseFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        try {
            while ((strLine = br.readLine()) != null) {
                processLine(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void processLine(String line) {
        System.out.println(line);
    }
}
