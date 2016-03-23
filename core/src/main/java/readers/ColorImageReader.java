package readers;

import datastructures.ColorImage;
import datastructures.Point3d;
import managers.ColorImgManager;
import managers.FlowFieldManager;

public class ColorImageReader extends FileReader {

    private ColorImage img;
    public ColorImageReader(String dataset, String fileNr) {
        String baseFileName = "../output/tracker_data/" + dataset + "/color_lab_"+ fileNr + ".txt";
        img = new ColorImage(FlowFieldManager.getInstance().m(), FlowFieldManager.getInstance().n());
        readFile(baseFileName);
        ColorImgManager.getInstance().add(img);
    }

    @Override
    protected void processLine(String line) {
        String[] parts = line.split("\\(|\\)");
        String[] indices = parts[0].substring(0, parts[0].length()-3).split(",");
        String[] rgbValues = parts[1].split(",");

        // Subtract 1 from the index values since Matlab starts counting at 1
        // where as the first index in java arrays is 0
        int row_idx = Integer.parseInt(indices[0])-1;
        int col_idx = Integer.parseInt(indices[1])-1;

        Point3d p = new Point3d(
                Double.parseDouble(rgbValues[0]),
                Double.parseDouble(rgbValues[1]),
                Double.parseDouble(rgbValues[2])
        );

        img.setElement(p, row_idx, col_idx);
    }
}
