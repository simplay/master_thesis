package writers;

import managers.TrajectoryManager;
import pipeline_components.ArgParser;
import pipeline_components.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.List;

/**
 * Allows to write huge files (several 100 mb large) within few seconds by making use of
 * byte streams. Offers basic file write and deletion operations and is responsible for
 * deriving the filename.
 */
public class LargeFileWriter {

    // Indicates whether files be deleted
    private boolean deletionIsAllowed = true;

    /**
     * Prevents this file handler from deleting files.
     */
    public void disableDeletionMode() {
        this.deletionIsAllowed = false;
    }

    /**
     * Writes fast a huge collection of Strings into a files
     *
     * @param strLines collection of file lines
     * @param fileName file path name with extension
     * @throws IOException
     */
    public void writeFile(List<String> strLines, String fileName) throws IOException {
        // delete old version of this file, if it exists, to prevent the od bug of writing
        // weird bytes into the file. This issue only occurs in case the file was previously
        // already generated.
        deleteFile(fileName);

        // number of bytes apart from the starting position.
        int offset = 0;

        FileChannel rwChannel = new RandomAccessFile(fileName, "rw").getChannel();
        for (String item : strLines) {
            byte[] buffer = item.getBytes();

            // Number of bytes the current line requires
            int buff_len = buffer.length;
            ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, offset, buff_len);
            offset += buff_len;
            wrBuf.put(buffer);
        }
        rwChannel.close();
    }

    /**
     * Writes fast a huge collection of Strings into a files
     *
     * @param item collection of file lines
     * @param fileName file path name with extension
     * @throws IOException
     */
    public void writeFile(String item, String fileName) throws IOException {
        // delete old version of this file, if it exists, to prevent the od bug of writing
        // weird bytes into the file. This issue only occurs in case the file was previously
        // already generated.
        deleteFile(fileName);

        FileChannel rwChannel = new RandomAccessFile(fileName, "rw").getChannel();
        byte[] buffer = item.getBytes();

        // Number of bytes the current line requires
        int buff_len = buffer.length;
        ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, buff_len);
        wrBuf.put(buffer);
        rwChannel.close();
    }

    /**
     * Report filepath bound with a custom msg.
     *
     * @param filePath relative file path to generated file.
     * @param msg custom msg prepended to logging msg.
     */
    public void reportFilePath(String filePath, String msg) {
        Logger.println(msg + " `" + filePath + "`");
    }

    /**
     * Deletes a file with a given filename pointing to the target file.
     *
     * @info: This method is used to delete an old version of an already existing
     *  file when re-generating it. This is required, since sometimes, writing
     *  a regenerated version of an already existing file may cause problem, resulting
     *  in writing weird bytes into the file, corrupting it completely.
     *
     * @example deleteFile("../output/similarities/c14_sim.dat") tries to
     *  delete the file c14_sim.dat located at `../output/similarities/`
     * @param filepath file path name to target file that should be deleted.
     */
    public void deleteFile(String filepath) {

        // Skip deletion attempt, if deletion is not allowed
        if (!deletionIsAllowed) return;

        Path path = FileSystems.getDefault().getPath(filepath);
        try {
            Files.delete(path);
        } catch (NoSuchFileException x) {
            Logger.printFormattedError("%s: no such" + " file or directory%n", path);
        } catch (DirectoryNotEmptyException x) {
            Logger.printFormattedError("%s not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            Logger.printError(x.getMessage());
        }
    }

    /**
     * Obtain the filename prefix for a given dataset.
     *
     * In general, generated output files have the following prefix:
     * `<DS>_<TN>_<NNT>_<N>_<P>_<SUFFIX>` where
     *
     * + <DS> the used dataset name
     * + <TN> the selected similarity task
     * + <NNT> the nearest neighbor type that is used
     * + <N> the number of nearest neighbors  and
     * + <P> the user given prefix, optional.
     * + <SUFFIX> corresponds to the generated output file name.
     *
     * Example: When running
     *  -d c14 -task 2 -nn 1000 -nnm top -prefix foobar
     * the following output files are generated:
     *  c14_md_top_1000_foobar_sim.data
     *  c14_md_top_1000_foobar_labels.txt
     *  c14_md_top_1000_foobar_spnn.txt
     *
     * @param dataset dataset name used during computation.
     *
     * @return filename prefix used for generated files.
     */
    protected String getOutputFilenamePrefix(String dataset) {
        return dataset + "_" + getOutputFilenamePrefix();
    }

    /**
     * Obtain the filename prefix.
     *
     * In general, generated output files have the following prefix:
     * `<TN>_<NNT>_<N>_<P>_<SUFFIX>` where
     *
     * + <TN> the selected similarity task
     * + <NNT> the nearest neighbor type that is used
     * + <N> the number of nearest neighbors  and
     * + <P> the user given prefix, optional.
     * + <SUFFIX> corresponds to the generated output file name.
     *
     * Example: When running
     *  -d c14 -task 2 -nn 1000 -nnm top -prefix foobar
     * the following output files are generated:
     *  md_top_1000_foobar_sim.data
     *  md_top_1000_foobar_labels.txt
     *  md_top_1000_foobar_spnn.txt
     *
     * @return filename prefix used for generated files.
     */
    protected String getOutputFilenamePrefix() {
        String filenamePrefix = ArgParser.getSimTask().getIdName();
        filenamePrefix += "_" + ArgParser.getNNMode().getId();
        filenamePrefix += "_" + getNNPrefixCount();

        // In case a user has provided a custom name infix,
        // append it to the filename suffix.
        if (ArgParser.hasCustomFileNamePrefix()) {
            filenamePrefix += "_" + ArgParser.getCustomFileNamePrefix();
        }

        return filenamePrefix;
    }

    /**
     * Get the nearest neighborhood count that should be used for the filename prefix.
     *
     * Is equal to the total amount of trajectories if we want to return the complete neighborhood.
     * Otherwise it is equal to the pre-specified neighborhood size (given by the user args).
     *
     * @return nearest neighborhood count used by in the filename prefix.
     */
    protected int getNNPrefixCount() {
        return (ArgParser.shouldSkipNNCount()) ?
                TrajectoryManager.getInstance().trajectoryCount() :
                ArgParser.getNearestNeighborhoodCount();
    }
}
