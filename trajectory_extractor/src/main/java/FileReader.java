import java.io.*;

/**
 * Created by simplay on 04/03/16.
 */
public abstract class FileReader {

    protected void readFile(String fileNamePath) {
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(fileNamePath);
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

    protected abstract void processLine(String line);

    protected float[] parseToFloatArray(String[] items) {
        float[] intItems = new float[items.length];
        int idx = 0;
        for (String item : items) {
            intItems[idx] = Float.parseFloat(item);
            idx++;
        }
        return intItems;
    }
}

