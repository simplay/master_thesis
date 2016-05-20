package managers;

import datastructures.ColorImage;
import java.util.ArrayList;

/**
 * ColorImgManager allows to access the color image data that belongs to
 * a certain dataset frame. The color images are in CIE L*a*b color space.
 *
 * This Manager is primarily used within SD similarity tasks to
 * access color value in order to compute the color distances between overlapping trajectories.
 *
 * Assumption: The index in the internal list of images corresponds to the frame index
 * the image belongs to.
 */
public class ColorImgManager {

    // Color manager singleton
    private static ColorImgManager instance = null;

    // Set of images
    private ArrayList<ColorImage> images;

    /**
     * Get the color image singleton
     *
     * @return singleton
     */
    public static ColorImgManager getInstance() {
        if (instance == null) {
            instance = new ColorImgManager();
        }
        return instance;
    }

    /**
     * Releases the singleton and its held references.
     */
    public static void release() {
        instance = null;
    }

    /**
     * Construct a new color img singleton.
     */
    private ColorImgManager() {
        this.images = new ArrayList<>();
    }

    /**
     * Append an image to the internal image list.
     *
     * Assumption: The index in the internal list of images corresponds to the frame index
     * the image belongs to.
     *
     * @param img image to be added.
     */
    public void add(ColorImage img) {
        images.add(img);
    }

    /**
     * Get the color image that belongs to a given frame index.
     *
     * Note that the first frame maps to the index 0.
     *
     * @param frame_idx frame index we want to query
     * @return image that belongs to he provided frame index.
     */
    public ColorImage get(int frame_idx) {
        return images.get(frame_idx);
    }

}
