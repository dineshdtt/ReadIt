package in.tts.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;

import in.tts.model.PrefManager;

public class ToGetImages {

    private static ArrayList<String> imageFile = new ArrayList<>();
    private static boolean status;

    public static void getAllShownImagesPath(final Activity activity, final Context context) {
        try {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    status = true;
                    // Find the last picture
                    String[] projection = new String[]{
                            MediaStore.Images.ImageColumns._ID,
                            MediaStore.Images.ImageColumns.DATA,
                            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                            MediaStore.Images.ImageColumns.DATE_TAKEN,
                            MediaStore.Images.ImageColumns.MIME_TYPE
                    };
                    if (activity != null) {
                        Cursor cursor =
                                activity.getContentResolver()
                                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                                null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
                        if (cursor != null) {
                            while (cursor.moveToNext() && imageFile.size() < 10) {
                                String imageLocation = cursor.getString(1);
                                imageFile.add(imageLocation.trim().replaceAll("\\s", "%20"));
                            }
                        }
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    new PrefManager(context).toSetImageFileList(imageFile);

                }
            });
            status = false;
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            CommonMethod.toCloseLoader();
            Crashlytics.logException(e);
        }
    }

    public static boolean isRunning() {
        return status;
    }
}