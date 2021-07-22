package in.tts.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.tts.*;
import in.tts.classes.TTS;
import in.tts.classes.ToSetMore;
import in.tts.model.PrefManager;
import in.tts.utils.CommonMethod;
import in.tts.utils.MyBounceInterpolator;

public class ImageOcrActivity extends AppCompatActivity {

    private String photoPath, name;
    private Bitmap bitmap;
    private TextRecognizer textRecognizer;
    private Frame imageFrame;
    private SparseArray<TextBlock> items;
    private TextBlock item;
    private StringBuilder stringBuilder;
    private View view;

    private RelativeLayout mRl;
    private TTS tts;

    private TextView tvImgOcr;
    private ImageView ivSpeak, ivReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_image_ocr);

            photoPath = getIntent().getStringExtra("PATH");

            if (getSupportActionBar() != null) {
                name = photoPath.substring(photoPath.lastIndexOf("/") + 1);
                CommonMethod.toSetTitle(getSupportActionBar(), ImageOcrActivity.this, name);
            }

            mRl = findViewById(R.id.rlImageOcrActivity);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(photoPath.trim().replaceAll("%20", " "), options);

            ImageView mIvOcr = findViewById(R.id.imgOcr);
            mIvOcr.setImageBitmap(bitmap);
            new toGetImage().execute();
            fn_permission();

            PrefManager prefManager = new PrefManager(ImageOcrActivity.this);
            ArrayList<String> list = prefManager.toGetImageListRecent();
            if (list != null) {
                if (!list.contains(photoPath)) {
                    list.add(photoPath.replaceAll("\\s", "%20"));
                    PrefManager.AddedRecentImage = true;
                    prefManager.toSetImageFileListRecent(list);
                }
            } else {
                list = new ArrayList<>();
                list.add(photoPath);
                PrefManager.AddedRecentImage = true;
                prefManager.toSetImageFileListRecent(list);
            }

            tts = new TTS(ImageOcrActivity.this);

        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {

            new toGetImage().execute();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }

    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
//            new toGetImage().execute();
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private void fn_permission() {
        try {
            if ((ContextCompat.checkSelfPermission(ImageOcrActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(ImageOcrActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            if ((ContextCompat.checkSelfPermission(ImageOcrActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(ImageOcrActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
//            if ((ContextCompat.checkSelfPermission(ImageOcrActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
//                ActivityCompat.requestPermissions(ImageOcrActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
//            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class toGetImage extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                imageFrame = new Frame.Builder().setBitmap(bitmap).build();

//            textBlocks = textRecognizer.detect(imageFrame);
//            for (int i = 0; i < textBlocks.size(); i++) {
//                textBlock = textBlocks.get(textBlocks.keyAt(i));
//                imageText = textBlock.getValue();                   // return string
//                Log.d("TAG", " Result : " + i + ":" + imageText);
//            }
//            Log.d("TAG", " Result Final: " + imageText);

                stringBuilder = new StringBuilder();
                items = textRecognizer.detect(imageFrame);
                for (int i = 0; i < items.size(); i++) {
                    item = items.valueAt(i);
                    stringBuilder.append(item.getValue());
                    stringBuilder.append("\n");
                }
                Log.d("TAG", " Final DATA " + stringBuilder);
            } catch (Exception | Error e) {
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
            }
            return null;
        }


        @SuppressLint("InflateParams")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (inflater != null) {
                    view = inflater.inflate(R.layout.layout_ocr_image, null, false);

                    tvImgOcr = view.findViewById(R.id.tvImgOcr);
                    ivSpeak = view.findViewById(R.id.ivSpeak);
                    ivReload = view.findViewById(R.id.ivReload);

                    tvImgOcr.setText(stringBuilder);

                    ivReload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {

                                tvImgOcr.setText("");
                                tts.toStop();

                                new toGetImage().execute();

                            } catch (Exception | Error e) {
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
                            }
                        }
                    });

                    ivSpeak.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
//                                Animation myAnim = AnimationUtils.loadAnimation(ImageOcrActivity.this, R.anim.bounce);
//
//                                // Use bounce interpolator with amplitude 0.2 and frequency 20
//                                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
//                                myAnim.setInterpolator(interpolator);
//
//                                ivSpeak.startAnimation(myAnim);

                                tts.SpeakLoud(stringBuilder.toString(), "AUD_Image" + name.substring(0, name.lastIndexOf(".")) + System.currentTimeMillis());
//                                tts.SpeakLoud(stringBuilder.toString(), "AUD_Image" + name.substring(0, (name.length() - 4)) + System.currentTimeMillis());
//                                tts.toSaveAudioFile(stringBuilder.toString(), "AUD_Image"+name.substring(0, (name.length()-4) )+System.currentTimeMillis());
                            } catch (Exception | Error e) {
                                e.printStackTrace();
                                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
                                Crashlytics.logException(e);
                                FirebaseCrash.report(e);
                            }
                        }
                    });

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // or wrap_content
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    layoutParams.setMargins(
                            CommonMethod.dpToPx(10, ImageOcrActivity.this),
                            CommonMethod.dpToPx(10, ImageOcrActivity.this),
                            CommonMethod.dpToPx(10, ImageOcrActivity.this),
                            CommonMethod.dpToPx(10, ImageOcrActivity.this)
                    );

                    mRl.addView(view, layoutParams);
                }

            } catch (Exception | Error e) {
                Crashlytics.logException(e);
                FirebaseCrash.report(e);
                e.printStackTrace();
                FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu != null) {
            getMenuInflater().inflate(R.menu.image_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            ToSetMore.MenuOptions(ImageOcrActivity.this, item);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        try {
            if (tts != null) {
                tts.toStop();
            }
            if (mRl != null) {
                if (mRl.getChildCount() > 1) {
                    if (view != null) {
                        mRl.removeView(view);
                    }
                }
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            if (tts != null) {
                tts.toStop();
                tts.toShutDown();
            }
            if (mRl != null) {
                if (mRl.getChildCount() > 1) {
                    if (view != null) {
                        mRl.removeView(view);
                    }
                }
            }
        } catch (Exception | Error e) {
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
        }
        super.onDestroy();
    }
}