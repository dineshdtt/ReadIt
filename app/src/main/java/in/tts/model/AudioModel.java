package in.tts.model;

public class AudioModel {

    private String text;
    private boolean isSelected = false;

    public AudioModel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }
}