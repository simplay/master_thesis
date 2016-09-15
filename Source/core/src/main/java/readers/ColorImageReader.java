package readers;

import datastructures.ColorImage;
import datastructures.Point3d;
import managers.ColorImgManager;
import managers.FlowFieldManager;

/**
 * ColorImageReader reads the CIE L*a*b* color data that belongs to a dataset frame.
 *
 * We assume, that the values of the read color data is in range [0, 255].
 *
 * The read color data is used as a cue for computing SD similarity values.
 *
 * Color files are located at `../output/tracker_data/DATASET/"`
 * and are named like the following `color_lab_NUM.txt`.
 *
 * A file line of such a color file is formatted like the following:
 *  /IDX_ROW,IDX_COL = (NUM,NUM,NUM)/
 *  where NUM is a value in range [0,255]
 *  IDX_ROW is the row index in the dataset frame the color value belongs to
 *  IDX_COL is the column index in the dataset frame the color value belongs to
 *
 * Example file content:
 *  ...
 *  4,1 = (244,128,128)
 *  5,1 = (244,128,128)
 *  6,1 = (244,128,128)
 *  7,1 = (244,128,128)
 *  8,1 = (244,128,128)
 *  ...
 *
 */
public class ColorImageReader extends FileReader {

    // Read color image file
    private ColorImage img;

    /**
     * Reads an color file for a given dataset using an arbitrary base file path.
     *
     * @param dataset dataset we are running.
     * @param basePath base file path where the target file is located.
     * @param fileNr frame index the target file belongs to.
     */
    public ColorImageReader(String dataset, String fileNr, String basePath) {
        String baseFileName = basePath + dataset + "/color_lab_"+ fileNr + ".txt";
        img = new ColorImage(FlowFieldManager.getInstance().m(), FlowFieldManager.getInstance().n());
        readFile(baseFileName);
        ColorImgManager.getInstance().add(img);
    }

    /**
     * Reads an color file for a given dataset using the pipeline path convention.
     *
     * @param dataset dataset we are running.
     * @param fileNr frame index the target file belongs to.
     */
    public ColorImageReader(String dataset, String fileNr) {
        this(dataset, fileNr, "../output/tracker_data/");
    }

    /**
     * Extracts the line items of the target color file.
     *
     * Line format: /IDX_ROW,IDX_COL = (NUM,NUM,NUM)/
     *
     * @param line line of a color file
     */
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

        // Assumption: value of color data is in range [0, 255]
        // TODO: export this logic to the matlab `init_data.m` script
        p.div_by(255d);

        img.setElement(p, row_idx, col_idx);
    }
}
