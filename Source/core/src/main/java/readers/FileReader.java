package readers;

import java.io.*;

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

    protected double[] parseToDoubleArray(String[] items) {
        double[] intItems = new double[items.length];
        int idx = 0;
        for (String item : items) {
            intItems[idx] = Double.parseDouble(item);
            idx++;
        }
        return intItems;
    }

    protected double[] parseToDoubleArray(String[] items, double scaleF) {
        double[] intItems = new double[items.length];
        int idx = 0;
        for (String item : items) {
            intItems[idx] = scaleF*Double.parseDouble(item);
            idx++;
        }
        return intItems;
    }

    /**
     * Maps an array of (0,1) valued strings to a boolean array.
     *
     * @param items serialized booleans
     * @return boolean array representation of read line.
     */
    protected boolean[] parseToBooleanArray(String[] items) {
        boolean[] boolItems = new boolean[items.length];
        int idx = 0;
        for (String item : items) {
            boolItems[idx] = Boolean.parseBoolean(item);
            idx++;
        }
        return boolItems;
    }

    /**
     * Extracts all elements in a row of a serialized Matlab matrix.
     *
     * @param line a row of a serialized Matlab matrix.
     * @return matrix elements as string array.
     */
    protected String[] extractElementsFromMatlabMatrixRow(String line) {
        return line.split("\\[|\\]")[1].split(" ");
    }
}

