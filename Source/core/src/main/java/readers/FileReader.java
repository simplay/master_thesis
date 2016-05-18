package readers;

import java.io.*;

/**
 * FileReader implements the basic functionality in order to read a file.
 * Files are processes line by line. The line processing behaviour has to
 * be implemented by the extending file reader class.
 */
public abstract class FileReader {

    /**
     * Reads the file that maps to a provided file path name.
     *
     * The file is read line by line.
     *
     * @param fileNamePath the file path name we want to read
     */
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

    /**
     * Defines the logic of how a file line should be processes.
     *
     * @param line a read file line.
     */
    protected abstract void processLine(String line);

    /**
     * Maps a given array of strings to an array of doubles.
     *
     * @param items string items that should be mapped to doubles.
     * @return double array containing the double version of the initially given string items.
     */
    protected double[] parseToDoubleArray(String[] items) {
        double[] intItems = new double[items.length];
        int idx = 0;
        for (String item : items) {
            intItems[idx] = Double.parseDouble(item);
            idx++;
        }
        return intItems;
    }

    /**
     * Maps a given array of strings to an array of doubles.
     * The values are scaled by a given factor.
     *
     * @param items string items that should be mapped to doubles.
     * @return double array containing the double version of the initially given string items.
     */
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
     * Extracts all elements in a row of a serialized Matlab matrix.
     *
     * @param line a row of a serialized Matlab matrix.
     * @return matrix elements as string array.
     */
    protected String[] extractElementsFromMatlabMatrixRow(String line) {
        return line.split("\\[|\\]")[1].split(" ");
    }
}

