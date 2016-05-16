import datastructures.Trajectory;
import managers.TrajectoryManager;
import org.junit.Before;
import org.junit.Test;
import pipeline_components.ArgParser;
import writers.LargeFileWriter;
import writers.SimilarityWriter;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class LargeFileWriterTest {

    private String dataset = "foobar";

    @Before
    public void prepareObject() {
        ArgParser.release();
    }

    @Test
    public void testCanWriteFile() {
        LinkedList<String> list = new LinkedList<>();
        list.add("1.0, 0.0\n");
        list.add("0.0, 1.0");

        try {
            new LargeFileWriter().writeFile(list, "./testdata/dummy.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream("./testdata/dummy.dat");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        try {
            int counter = 0;
            while ((strLine = br.readLine()) != null) {
                assertEquals(list.get(counter).trim(), strLine);
                counter++;
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

    @Test
    public void testFilenamePrefixDefault() {
        String[] args = {"-d", dataset, "-task", "1"};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                int nn_count = TrajectoryManager.getTrajectories().size();
                assertEquals(dataset + "_sd_all_" + nn_count, filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFilenamePrefixTopNNM() {
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "top"};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                assertEquals(dataset + "_sd_top_100", filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFilenamePrefixTopNNMWithNNCount() {
        int nn_count = (int)(Math.random()*500)+50;
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "top", "-nn", String.valueOf(nn_count)};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                assertEquals(dataset + "_sd_top_" + nn_count, filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFilenamePrefixBothNNMWithNNCount() {
        int nn_count = (int)(Math.random()*500)+50;
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "both", "-nn", String.valueOf(nn_count)};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                assertEquals(dataset + "_sd_both_" + nn_count, filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFilenamePrefixAllNNMWithNNCountHasNoEffectInFilenamePrefix() {
        int nn_count = (int)(Math.random()*500)+50;
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "all", "-nn", String.valueOf(nn_count)};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                nn_count = TrajectoryManager.getTrajectories().size();
                assertEquals(dataset + "_sd_all_" + nn_count, filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFilenamePrefixBothNNM() {
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "both"};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                assertEquals(dataset + "_sd_both_100", filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFilenamePrefixAllNMM() {
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "all"};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                int nn_count = TrajectoryManager.getTrajectories().size();
                assertEquals(dataset + "_sd_all_" + nn_count, filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFilenamePrefixBothNNMWithCustomPrefix() {
        String customPrefix = "foobar" + Math.random();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "both", "-prefix", customPrefix};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                assertEquals(dataset + "_sd_both_100_" + customPrefix, filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFilenamePrefixTopNNMWithCustomPrefix() {
        String customPrefix = "foobar" + Math.random();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "top", "-prefix", customPrefix};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                assertEquals(dataset + "_sd_top_100_" + customPrefix, filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFilenamePrefixAllNNMWithCustomPrefix() {
        String customPrefix = "foobar" + Math.random();
        String[] args = {"-d", dataset, "-task", "1", "-nnm", "all", "-prefix", customPrefix};
        ArgParser.getInstance(args);
        try {
            Method method = LargeFileWriter.class.getDeclaredMethod("getOutputFilenamePrefix");
            method.setAccessible(true);
            try {
                String suffix = (String)method.invoke(new LargeFileWriter());
                String filename = dataset + "_" + suffix;
                int nn_count = TrajectoryManager.getInstance().trajectoryCount();
                assertEquals(dataset + "_sd_all_" + nn_count + "_" + customPrefix, filename);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
