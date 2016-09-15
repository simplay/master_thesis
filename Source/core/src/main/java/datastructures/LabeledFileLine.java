package datastructures;

/**
 * LabeledFileLine models an actual read-in line of LabeledFile, such as the 'calibration.txt' file.
 * It is a container class carrying the label-and content of a file line.
 */
public class LabeledFileLine {

    // The file label
    private String label;

    // The file content that belongs to the label.
    private String content;

    /**
     * Create a LabeledFileLine
     *
     * @param label line identifier
     * @param content content that belongs to the identifier.
     */
    public LabeledFileLine(String label, String content) {
        this.label = label;
        this.content = content;
    }

    /**
     * Get the label.
     *
     * @return label value.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the content that belongs to the label.
     *
     * @return label content value.
     */
    public String getContent() {
        return content;
    }
}
