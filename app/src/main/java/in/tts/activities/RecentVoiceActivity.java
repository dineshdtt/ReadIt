package in.tts.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.perf.metrics.AddTrace;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.adapters.RecentVoiceAdapter;
import in.tts.model.Audio;
import in.tts.model.AudioModel;
import in.tts.services.MediaPlayerService;
import in.tts.utils.AlertDialogHelper;
import in.tts.utils.CommonMethod;
import in.tts.utils.RecyclerItemClickListener;
import in.tts.utils.StorageUtils;

public class RecentVoiceActivity extends AppCompatActivity implements AlertDialogHelper.AlertDialogListener {

    public static final String Broadcast_PLAY_NEW_AUDIO = "audioplayer.PlayNewAudio";

    boolean isMultiSelect = false;

    private ActionMode mActionMode;
    private Menu context_menu;

    private RecentVoiceAdapter mAdapter;
    private ArrayList<AudioModel> user_list = new ArrayList<>();
    private ArrayList<AudioModel> multiselect_list = new ArrayList<>();

    private AlertDialogHelper alertDialogHelper;

    private RecyclerView recyclerView;

    private MediaPlayerService player;
    private boolean serviceBound = false;

    @Override
    @AddTrace(name = "onCreateAudioSettingActivity", enabled = true)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_voice);
        CommonMethod.setAnalyticsData(RecentVoiceActivity.this, "MainTab", "Recent Voice", null);

        if (getSupportActionBar() != null) {
            CommonMethod.toSetTitle(getSupportActionBar(), RecentVoiceActivity.this, getString(R.string.str_title_recent_voice));
        }

        alertDialogHelper = new AlertDialogHelper(this);

        getFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()));

        recyclerView = findViewById(R.id.recycleView);
        mAdapter = new RecentVoiceAdapter(RecentVoiceActivity.this, user_list, multiselect_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        // Listening the click events

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    multi_select(position);
                } else {
                    playAudio(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    isMultiSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mActionModeCallback);
                    }
                    Log.d("TAG", "Audio : " + position + ":" + user_list.get(position).isSelected());
                    view.findViewById(R.id.ivSelected).setVisibility(user_list.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE);
                }

                multi_select(position);

            }
        }));
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(user_list.get(position)))
                multiselect_list.remove(user_list.get(position));
            else
                multiselect_list.add(user_list.get(position));

            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else
                mActionMode.setTitle("");

            refreshAdapter();
        }
    }

    public void refreshAdapter() {
        mAdapter.selected_usersList = multiselect_list;
        mAdapter.audioList = user_list;
        mAdapter.notifyDataSetChanged();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.selected_audio_menu, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("", "Delete Audio", "DELETE", "CANCEL", 1, false);
                    return true;
                case R.id.action_seelct_all:
                    selectAll();
//                    alertDialogHelper.showAlertDialog("", "Delete Audio", "DELETE", "CANCEL", 1, false);
                    return true;
                case R.id.action_share:
                    if (multiselect_list.size() > 0) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
                        intent.setType("image/jpeg"); /* This example is sharing jpeg images. */

                        ArrayList<Uri> files = new ArrayList<Uri>();

//                        for(String path : filesToSend /* List of the files you want to send */) {
                        for (int i = 0; i < multiselect_list.size(); i++) {
//                            File file = new File(path);
                            File file = new File(multiselect_list.get(i).getText());
                            Uri uri = Uri.fromFile(file);
                            files.add(uri);
                        }

                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

                        startActivity(Intent.createChooser(intent, "Share Sound File"));
                    } else {
                        CommonMethod.toDisplayToast(RecentVoiceActivity.this, " No File selected to share");
                    }
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<>();
            refreshAdapter();
        }
    };

    private void selectAll() {
        for (int i = 0; i < user_list.size(); i++) {
            if (mActionMode != null) {
                if (!multiselect_list.contains(user_list.get(i))) {
                    multiselect_list.add(user_list.get(i));
                }

                if (multiselect_list.size() > 0) {
                    mActionMode.setTitle("" + multiselect_list.size());
                } else {
                    mActionMode.setTitle("");
                }

                refreshAdapter();
            }
        }
    }

    // AlertDialog Callback Functions

    @Override
    public void onPositiveClick(int from) {
        if (from == 1) {
            if (multiselect_list.size() > 0) {
                for (int i = 0; i < multiselect_list.size(); i++) {
                    File fdelete = new File(multiselect_list.get(i).getText());
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Log.d("TAG", "file Deleted :" + fdelete.getPath());
                            user_list.remove(multiselect_list.get(i));

                        } else {
                            Log.d("TAG", "file not Deleted :" + fdelete.getPath());
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();

                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }
        } else if (from == 2) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
            mAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }


    public void getFile(final File dir) {
        try {
            File listFile[] = dir.listFiles();
            if (listFile != null && listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {
                    if (listFile[i].isDirectory()) {
                        getFile(listFile[i]);
                    } else {
                        boolean booleanpdf = false;
                        if (listFile[i].getName().endsWith(".wav")) {//|| listFile[i].getName().endsWith(".mp3"))
                            for (int j = 0; j < user_list.size(); j++) {
                                if (user_list.get(j).equals(listFile[i].getPath())) {
                                    booleanpdf = true;
//                                } else {
                                }
                            }
                            if (booleanpdf) {
                                booleanpdf = false;
                            } else {
                                Log.d("TAG", " Audio File :" + listFile[i].getAbsolutePath() + ":" + CommonMethod.getFileSize(listFile[i]));
                                if (!CommonMethod.getFileSize(listFile[i]).equals("0 B")) {
//                                file.add(listFile[i].getPath());
                                    user_list.add(new AudioModel(listFile[i].getPath()));
                                }
                            }
                        }
                    }


                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            FlurryAgent.onError(e.getMessage(), e.getLocalizedMessage(), e);
            Crashlytics.logException(e);
            FirebaseCrash.report(e);
        }
    }

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;

//            Toast.makeText(RecentVoiceActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private void playAudio(String media) {
        Log.d("TAG", "Media : " + media + ":" + serviceBound);
        //Check is service is active
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
        }
    }

    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable audioList to SharedPreferences
            StorageUtils storage = new StorageUtils(getApplicationContext());
//            storage.storeAudio(audioList);
            storage.storeAudio(user_list);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtils storage = new StorageUtils(getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
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