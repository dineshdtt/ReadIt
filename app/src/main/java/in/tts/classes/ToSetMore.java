package in.tts.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;

import in.tts.R;
import in.tts.activities.AudioSettingActivity;
import in.tts.activities.CameraActivity;
import in.tts.activities.Contact_us;
import in.tts.activities.HelpActivity;
import in.tts.activities.OurOtherAppActivity;
import in.tts.activities.RecentVoiceActivity;
import in.tts.activities.SettingActivity;

public class ToSetMore {
    public static void MenuOptions(Context context, MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.actionCamera:
//                    context.startActivity(new Intent(context, CameraOcrActivity.class));
//                    context.startActivity(new Intent(context, MyCameraActivity.class));
                    context.startActivity(new Intent(context, CameraActivity.class));
                    break;

                case R.id.settings:
                    context.startActivity(new Intent(context, SettingActivity.class));
                    break;

                case R.id.audio_settings:
                    context.startActivity(new Intent(context, AudioSettingActivity.class));
//                    context.startActivity(new Intent(context, AudioPlayerActivity.class));
                    break;

                case R.id.recent_audios:
                    context.startActivity(new Intent(context, RecentVoiceActivity.class));
                    break;

                case R.id.our_other_apps:
                    context.startActivity(new Intent(context, OurOtherAppActivity.class));
                    break;

                case R.id.help:
                    context.startActivity(new Intent(context, HelpActivity.class));
                    break;

                case R.id.give_feedback:
                    context.startActivity(new Intent(context, Contact_us.class));
                    break;

                case R.id.rate_us:
                    showRateDialog(context);
                    break;

                case R.id.share_apps:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "I suggest this app for you : https://play.google.com/store/apps/details?id=com.android.chrome");
                    intent.setType("text/plain");
                    context.startActivity(intent);
                    break;

                case android.R.id.home:
                    ((Activity) context).onBackPressed();
                    break;

                default:
                    break;
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    private static void showRateDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Rate application")
                .setMessage("Please, rate the app at PlayMarket")
                .setPositiveButton("RATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (context != null) {
                            String link = "market://details?id=";
                            try {
                                // play market available
                                context.getPackageManager()
                                        .getPackageInfo("com.android.vending", 0);
                                // not available
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                                // should use browser
                                link = "https://play.google.com/store/apps/details?id=";
                            }
                            // starts external action
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link + context.getPackageName())));
                        }
                    }
                })
                .setNegativeButton("CANCEL", null);
        builder.show();
    }
}
