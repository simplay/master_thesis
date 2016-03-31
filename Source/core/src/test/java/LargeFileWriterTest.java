import org.junit.Test;
import writers.LargeFileWriter;

import java.io.*;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class LargeFileWriterTest {
    @Test
    public void testCanWriteFile() {
        LinkedList<String> list = new LinkedList<>();
        list.add("1.0, 0.0\n");
        list.add("0.0, 1.0");

        try {
            new LargeFileWriter().writeFile(list, "../output/similarities/dummy.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }


        FileInputStream fstream = null;
        try {
            fstream = new FileInputStream("../output/similarities/dummy.dat");
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
}
