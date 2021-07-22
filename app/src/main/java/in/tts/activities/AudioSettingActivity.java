package in.tts.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import in.tts.R;
import in.tts.model.AudioSetting;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;

import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;
import com.google.gson.Gson;
import com.vsa.seekbarindicated.SeekBarIndicated;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class AudioSettingActivity extends AppCompatActivity {

    private SeekBarIndicated seekBarIndicated;
    private RadioGroup rgVoiceSel, rgLangSel, rgAccentSel;
    private RadioButton rbMale, rbFemale, rbEnglish, rbHindi, rbMarathi, rbTamil, rbAccent1, rbAccent2;
    private AudioSetting audioSetting;
    private RelativeLayout rlEng;
    private PrefManager prefManager;

    @Override
    @AddTrace(name = "onCreateAudioSettingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_audio_setting);
            CommonMethod.setAnalyticsData(AudioSettingActivity.this, "MainTab", "AudioSetting", null);

            if (getSupportActionBar() != null) {
                CommonMethod.toSetTitle(getSupportActionBar(), AudioSettingActivity.this, getString(R.string.str_title_audio_settings));
            }

            prefManager = new PrefManager(AudioSettingActivity.this);
            prefManager.getAudioSetting();
            audioSetting = AudioSetting.getAudioSetting(AudioSettingActivity.this);

            rgVoiceSel = findViewById(R.id.rgVoice);
            rbMale = findViewById(R.id.rbMale);
            rbFemale = findViewById(R.id.rbFemale);

            rgLangSel = (RadioGroup) findViewById(R.id.rgLanguageSel);
            rbEnglish = findViewById(R.id.rbEnglishLs);
            rbHindi = findViewById(R.id.rbHindiLs);
            rbMarathi = findViewById(R.id.rbMarathiLs);
            rbTamil = findViewById(R.id.rbTamilLs);

            rgAccentSel = findViewById(R.id.rgAccentSel);
            rbAccent1 = findViewById(R.id.rbAccentUS);
            rbAccent2 = findViewById(R.id.rbAccentUK);

            seekBarIndicated = findViewById(R.id.seek1);

            rlEng = findViewById(R.id.rlEnglish);

            toSetDefaultValue();

            toClickListener();

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toClickListener() {
        try {
            seekBarIndicated.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    audioSetting.setVoiceSpeed(progress);
                    Log.d("TAG SEEK", " onProgressChanged " + progress + ":" + fromUser + audioSetting.getVoiceSpeed());
                    setPrefData();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            rgLangSel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    try {
                        if (i == R.id.rbEnglishLs) {
                            rlEng.setVisibility(View.VISIBLE);
                        } else {
                            rlEng.setVisibility(View.GONE);
                        }
                        switch (i) {
                            case R.id.rbEnglishLs:
                                audioSetting.setLangSelection(String.valueOf(Locale.ENGLISH));
                                setPrefData();
                                break;

                            case R.id.rbHindiLs:
                                audioSetting.setLangSelection(String.valueOf(new Locale("hin", "IND")));
                                setPrefData();
                                break;

                            case R.id.rbMarathiLs:
                                audioSetting.setLangSelection(String.valueOf(new Locale("mar", "IND")));
                                setPrefData();
                                break;

                            case R.id.rbTamilLs:
                                audioSetting.setLangSelection(String.valueOf(new Locale("ta", "IND")));
//                                audioSetting.setLangSelection(String.valueOf(new Locale("ta")));
                                setPrefData();
                                break;

                            default:
                                audioSetting.setLangSelection(String.valueOf(Locale.ENGLISH));
                                setPrefData();
                                break;
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });

            rgAccentSel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    try {
                        switch (i) {
                            case R.id.rbAccentUS:
                                audioSetting.setAccentSelection(String.valueOf(Locale.UK));
                                Log.d("TAG", " Lang Uk " + String.valueOf(Locale.UK));
                                setPrefData();
                                break;

                            case R.id.rbAccentUK:
                                audioSetting.setAccentSelection(String.valueOf(Locale.US));
                                Log.d("TAG", " Lang Us " + String.valueOf(Locale.US));
                                setPrefData();
                                break;

                            default:
                                audioSetting.setAccentSelection(String.valueOf(Locale.US));
                                Log.d("TAG", " Lang def " + String.valueOf(Locale.US));
                                setPrefData();
                                break;
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });
            rgVoiceSel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    try {
                        if (rbMale.isChecked()) {
                            audioSetting.setVoiceSelection("Male");
                            setPrefData();
                        } else {
                            audioSetting.setVoiceSelection("Female");
                            setPrefData();
                        }
                    } catch (Exception | Error e) {
                        e.printStackTrace();
                        FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                        Crashlytics.logException(e);
                        FirebaseCrash.report(e);
                    }
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetDefaultValue() {
        try {
            if (audioSetting != null) {
                seekBarIndicated.setValue((int) audioSetting.getVoiceSpeed());
                if (audioSetting.getLangSelection() != null) {
                    switch (audioSetting.getLangSelection()) {
                        case "hin_IND":
                            toSetLanguageSelection(rbHindi, rbEnglish, rbMarathi, rbTamil, false);
                            break;

                        case "mar_IND":
                            toSetLanguageSelection(rbMarathi, rbHindi, rbTamil, rbEnglish, false);
                            break;

//                        case "ta_IND":
                        case "ta":
                            toSetLanguageSelection(rbTamil, rbHindi, rbEnglish, rbMarathi, false);
                            break;

                        case "en":
                            toSetEnglish();
                            break;

                        default:
                            toSetEnglish();
                            break;
                    }
                } else {
                    toSetEnglish();
                }

                switch (audioSetting.getVoiceSelection()) {
                    case "Male":
                        rbMale.setChecked(true);
                        rbFemale.setChecked(false);
                        break;

                    case "Female":
                        rbFemale.setChecked(true);
                        rbMale.setChecked(false);
                    default:
                        rbAccent1.setChecked(true);
                        rbAccent2.setChecked(false);
                        break;
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetEnglish() {
        try {
            toSetLanguageSelection(rbEnglish, rbHindi, rbTamil, rbMarathi, true);
            if (audioSetting.getAccentSelection() != null) {
                switch (audioSetting.getAccentSelection()) {
                    case "eng_US":
                        rbAccent1.setChecked(true);
                        rbAccent2.setChecked(false);
                        break;
                    case "eng_GB":
                        rbAccent1.setChecked(false);
                        rbAccent2.setChecked(true);
                        break;
                    default:
                        rbAccent1.setChecked(true);
                        rbAccent2.setChecked(false);
                        break;
                }
            } else {
                rbAccent1.setChecked(true);
                rbAccent2.setChecked(false);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void toSetLanguageSelection(RadioButton rb, RadioButton rb1, RadioButton rb2, RadioButton rb3, boolean b) {
        try {
            rb.setChecked(true);
            rb1.setChecked(false);
            rb2.setChecked(false);
            rb3.setChecked(false);
            if (b) {
                rlEng.setVisibility(View.VISIBLE);
            } else {
                rlEng.setVisibility(View.GONE);
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    public void setPrefData() {
        try {
            Log.d("TAG ", "to setAudioSetting : " + new JSONObject(new Gson().toJson(AudioSetting.getAudioSetting(AudioSettingActivity.this))).toString());
            prefManager.setAudioSetting();
            prefManager.getAudioSetting();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    break;
                default:
                    return true;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}