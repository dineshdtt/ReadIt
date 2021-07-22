package in.tts.services;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;

public class ClipboardMonitorService extends Service {

    private static final String FILENAME = "clipboard-history.txt";

    private File mHistoryFile;
    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
    private ClipboardManager mClipboardManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAGCM", "onCreate ");
        mHistoryFile = new File(getExternalFilesDir(null), FILENAME);
        mClipboardManager =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAGCM", " onDestroy ");
        if (mClipboardManager != null) {
            mClipboardManager.removePrimaryClipChangedListener(
                    mOnPrimaryClipChangedListener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TAGCM", " IBinder ");
        return null;
    }

    private boolean isExternalStorageWritable() {
        Log.d("TAGCM", " isExternalStorageWritable ");
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    Log.d("TAGCM", " onPrimaryClipChanged");
                    Log.d("TAGCM", "onPrimaryClipChanged");
                    ClipData clip = mClipboardManager.getPrimaryClip();
                    mThreadPool.execute(new WriteHistoryRunnable(
                            clip.getItemAt(0).getText()));
                }
            };

    
    private class WriteHistoryRunnable implements Runnable {
        private final Date mNow;
        private final CharSequence mTextToWrite;

        public WriteHistoryRunnable(CharSequence text) {
            Log.d("TAGCM", " WriteHistoryRunnable");
            mNow = new Date(System.currentTimeMillis());
            mTextToWrite = text;
        }

        @Override
        public void run() {
            Log.d("TAGCM", " run");
            if (TextUtils.isEmpty(mTextToWrite)) {
                // Don't write empty text to the file
                return;
            }

            if (isExternalStorageWritable()) {
                try {
                    Log.i("TAGCM", "Writing new clip to history:");
                    Log.i("TAGCM", mTextToWrite.toString());
//                    BufferedWriter writer =
//                            new BufferedWriter(new FileWriter(mHistoryFile, true));
//                    writer.write(String.format("[%s]: ", mNow.toString()));
//                    writer.write(mTextToWrite.toString());
//                    writer.newLine();
//                    writer.close();
                    Log.d("TAGCM", "writer : "+ mTextToWrite.toString());
                } catch (Exception|Error e) {
                    Log.w("TAGCM", String.format("Failed to open file %s for writing!",
                            mHistoryFile.getAbsoluteFile()));
                }
            } else {
                Log.w("TAGCM", "External storage is not writable!");
            }
        }
    }
}

//https://github.com/twaddington/Android-Clipboard-Monitor/blob/master/src/com/example/clipboardmonitor/service/ClipboardMonitorService.java