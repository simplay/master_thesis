package writers;

import pipeline_components.ArgParser;
import pipeline_components.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.List;

public class LargeFileWriter {

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
     * Obtain the correct filename prefix for a given dataset.
     * Changes, in case a user has provided a custom filename prefix.
     *
     * @param dataset dataset name used during computation.
     *
     * @return filename prefix of outputted similarity files.
     */
    protected String getOutputFilename(String dataset) {
        if (ArgParser.hasCustomFileNamePrefix()) {
            return ArgParser.getCustomFileNamePrefix() + "_" + dataset;
        }
        return dataset;
    }
}
