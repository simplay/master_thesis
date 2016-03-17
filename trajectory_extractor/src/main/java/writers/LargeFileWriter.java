package writers;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
}
