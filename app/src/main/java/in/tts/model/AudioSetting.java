package in.tts.model;

import android.annotation.SuppressLint;
import android.content.Context;

public class AudioSetting {

    @SuppressLint("StaticFieldLeak")
    private static transient AudioSetting audioSetting;
    private transient Context context;
    private String VoiceSelection ;
    private String LangSelection ;
    private String AccentSelection ;
    private int VoiceSpeed =0;


    private AudioSetting(Context context) {
        this.context = context;
    }

    public static AudioSetting getAudioSetting(Context context) {
        try {
            if (audioSetting == null) {
                audioSetting = new AudioSetting(context);
            }
        } catch (Exception| Error e){
            e.printStackTrace();

        }
        return audioSetting;
    }

    public void setAudioSetting(AudioSetting audioSettin) {
        audioSetting = audioSettin;
    }

    public String getVoiceSelection() {
        return VoiceSelection;
    }

    public void setVoiceSelection(String voiceSelection) {
       VoiceSelection = voiceSelection;
    }

    public String getLangSelection() {
        return LangSelection;
    }

    public void setLangSelection(String langSelection) {
        LangSelection = langSelection;
    }

    public String getAccentSelection() {
        return AccentSelection;
    }

    public void setAccentSelection(String accentSelection) {
        AccentSelection = accentSelection;
    }

    public float getVoiceSpeed() {
        return VoiceSpeed;
    }

    public void setVoiceSpeed(int voiceSpeed) {
        VoiceSpeed = voiceSpeed;
    }
}
