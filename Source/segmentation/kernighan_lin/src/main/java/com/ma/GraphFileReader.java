package com.ma;


import java.io.*;

public abstract class GraphFileReader {

    protected Graph graph;
    protected int fileLineCount;

    // File path where all computed trajectory similarity data is located at.
    private final String basePath = "../../output/similarities/";

    public GraphFileReader(String fname, Graph graph) {
        this.graph = graph;
        String baseFileName = basePath + fname;

        File f = new File(baseFileName);
        Logger.println("Expecting data in Folder : " + f.getAbsolutePath());
        checkFileExists(baseFileName);
        assignNumberofLines(baseFileName);

        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(baseFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        stepsBeforeFileProcessing();

        String strLine;
        try {
            while ((strLine = br.readLine()) != null) {
                processFileLine(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stepsAfterFileProcessing();
    }

    private void assignNumberofLines(String fname) {
        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream(fname);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        this.fileLineCount = (int) br.lines().count();
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFileExists(String fname) {
        File f = new File(fname);
        if (f.exists() && !f.isDirectory()) {
            Logger.println("File " + fname + " does exist");
        } else {
            Logger.println(" File does NOT exist");
        }
    }

    /**
     * Applies all relevant pre-processing steps before running the
     * Filereader.
     */
    protected abstract void stepsBeforeFileProcessing();

    /**
     * Applies all relevant post-processing steps after running the
     * Filereader.
     */
    protected abstract void stepsAfterFileProcessing();

    /**
     * Processes an extracted line of a read file.
     *
     * @param fline current read file line.
     */
    protected abstract void processFileLine(String fline);

}
