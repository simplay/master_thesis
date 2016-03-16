package managers;

import datastructures.ColorImage;

import java.util.ArrayList;

public class ColorImgManager {

    private static ColorImgManager instance = null;
    private ArrayList<ColorImage> images;

    public static ColorImgManager getInstance() {
        if (instance == null) {
            instance = new ColorImgManager();
        }
        return instance;
    }

    private ColorImgManager() {
        this.images = new ArrayList<>();
    }

    public void add(ColorImage img) {
        images.add(img);
    }

    public ColorImage get(int frame_idx) {
        return images.get(frame_idx);
    }

}
