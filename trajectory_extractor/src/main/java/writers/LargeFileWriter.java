package writers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class LargeFileWriter {

    /**
     * Writes fast a huge collection of Strings into a files
     * @param strLines collection of file lines
     * @param fileName file path name with extension
     * @throws IOException
     */
    protected void writeFile(List<String> strLines, String fileName) throws IOException {
        FileChannel rwChannel = new RandomAccessFile(fileName, "rw").getChannel();
        int n = strLines.size();
        for (String item : strLines) {
            byte[] buffer = item.getBytes();
            ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, buffer.length * n);
            wrBuf.put(buffer);
        }
        rwChannel.close();
    }
}
