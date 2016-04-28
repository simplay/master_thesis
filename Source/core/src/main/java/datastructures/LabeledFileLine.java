package datastructures;

public class LabeledFileLine {

    private String label;
    private String content;

    public LabeledFileLine(String label, String content) {
        this.label = label;
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public String getContent() {
        return content;
    }
}
