package in.tts.model;

import android.graphics.Bitmap;

public class Browser {

    private String title;
    private String url;
    private String Orignal_url;
    private Bitmap url_icon;

    public Bitmap getUrl_icon() {
        return url_icon;
    }

    public void setUrl_icon(Bitmap url_icon) {
        this.url_icon = url_icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOrignal_url() {
        return Orignal_url;
    }

    public void setOrignal_url(String orignal_url) {
        Orignal_url = orignal_url;
    }

}
