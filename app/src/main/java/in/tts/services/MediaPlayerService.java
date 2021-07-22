package in.tts.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import in.tts.R;
import in.tts.activities.RecentVoiceActivity;
import in.tts.model.AudioModel;
import in.tts.utils.PlaybackStatus;
import in.tts.utils.StorageUtils;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,

        AudioManager.OnAudioFocusChangeListener {

    public static final String ACTION_PLAY = "audioplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "audioplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "audioplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "audioplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "audioplayer.ACTION_STOP";

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;

    private MediaPlayer mediaPlayer;
    //path to the audio file
    private String mediaFile;

    //Used to pause/resume MediaPlayer
    private int resumePosition;

    private AudioManager audioManager;

    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    //List of available Audio files
//    private ArrayList<Audio> audioList;
    private ArrayList<AudioModel> list;
    private int audioIndex = -1;
    private String audio;
//    private Audio activeAudio; //an object of the currently playing audio

    File file;

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            // Perform one-time setup procedures

            // Manage incoming phone calls during playback.
            // Pause MediaPlayer on incoming call,
            // Resume on hangup.
            callStateListener();
            //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
            registerBecomingNoisyReceiver();
            //Listen for new Audio to play -- BroadcastReceiver
            register_playNewAudio();
        } catch (Error | Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info.
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    //Becoming noisy
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //pause audio on ACTION_AUDIO_BECOMING_NOISY
                pauseMedia();
                buildNotification(PlaybackStatus.PAUSED);
            } catch (Error | Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            //Set up MediaPlayer event listeners
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnSeekCompleteListener(this);
            mediaPlayer.setOnInfoListener(this);
            //Reset so that the MediaPlayer is not pointing to another data source
            mediaPlayer.reset();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // Set the data source to the mediaFile location
//            mediaPlayer.setDataSource(mediaFile);
//            mediaPlayer.setDataSource(activeAudio.getData());

            Log.d("TAG", " audio path :  " + audio);
            mediaPlayer.setDataSource("file://" + audio);
            mediaPlayer.prepareAsync();
        } catch (Error | Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    private void playMedia() {
        try {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMedia() {
        try {
            if (mediaPlayer == null) return;
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void pauseMedia() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                resumePosition = mediaPlayer.getCurrentPosition();
            }
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            Log.d("TAG", " onCompletion " + audioIndex + ":"+ list.size() + (audioIndex != list.size()));
            if (audioIndex != (list.size()-1)) {
                skipToPrevious();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            } else {
                //Invoked when playback of a media source has completed.
                stopMedia();
                //stop the service
                stopSelf();
            }
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    //Handle errors
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        try {
            //Invoked when there has been an error during an asynchronous operation
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                    Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                    break;
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                    break;
            }
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        try {
            //Invoked when the media source is ready for playback.
            playMedia();
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        try {
            //Invoked when the audio focus of the system is updated.
            switch (focusState) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // resume playback
                    if (mediaPlayer == null) initMediaPlayer();
                    else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    // Lost focus for an unbounded amount of time: stop playback and release media player
                    if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Lost focus for a short time, but we have to stop
                    // playback. We don't release the media player because playback
                    // is likely to resume
                    if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lost focus for a short time, but it's ok to keep playing
                    // at an attenuated level
                    if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                    break;
            }
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private boolean requestAudioFocus() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                //Focus gained
                return true;
            }
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    //Handle incoming phone calls
    private void callStateListener() {
        try {
            // Get the telephony manager
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //Starting listening for PhoneState changes
            phoneStateListener = new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state) {
                        //if at least one call exists or the phone is ringing
                        //pause the MediaPlayer
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        case TelephonyManager.CALL_STATE_RINGING:
                            if (mediaPlayer != null) {
                                pauseMedia();
                                ongoingCall = true;
                            }
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                            // Phone idle. Start playing.
                            if (mediaPlayer != null) {
                                if (ongoingCall) {
                                    ongoingCall = false;
                                    resumeMedia();
                                }
                            }
                            break;
                    }
                }
            };
            // Register the listener with the telephony manager
            // Listen for changes to the device call state.
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //Get the new media index form SharedPreferences
                audioIndex = new StorageUtils(getApplicationContext()).loadAudioIndex();
                Log.d("TAG", " Song 1 " + audioIndex +":"+list.size());
                if (audioIndex != -1 && audioIndex < list.size()) {
                    //index is in a valid range
                    audio = list.get(audioIndex).getText();
                } else {
                    stopSelf();
                }

                //A PLAY_NEW_AUDIO action received
                //reset mediaPlayer to play the new Audio
                stopMedia();
                mediaPlayer.reset();
                initMediaPlayer();
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            } catch (Error | Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void register_playNewAudio() {
        try {
            //Register playNewMedia receiver
            IntentFilter filter = new IntentFilter(RecentVoiceActivity.Broadcast_PLAY_NEW_AUDIO);
            registerReceiver(playNewAudio, filter);
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mediaPlayer != null) {
                stopMedia();
                mediaPlayer.release();
            }
            removeAudioFocus();
            //Disable the PhoneStateListener
            if (phoneStateListener != null) {
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            }

            removeNotification();

            //unregister BroadcastReceivers
            unregisterReceiver(becomingNoisyReceiver);
            unregisterReceiver(playNewAudio);

            //clear cached playlist
            new StorageUtils(getApplicationContext()).clearCachedAudioPlaylist();
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void initMediaSession() throws RemoteException {
        try {
            if (mediaSessionManager != null) return; //mediaSessionManager exists

            mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
            // Create a new MediaSession
            mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
            //Get MediaSessions transport controls
            transportControls = mediaSession.getController().getTransportControls();
            //set MediaSession -> ready to receive media commands
            mediaSession.setActive(true);
            //indicate that the MediaSession handles transport control commands
            // through its MediaSessionCompat.Callback.
            mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

            //Set mediaSession's MetaData
            updateMetaData();

            // Attach Callback to receive MediaSession updates
            mediaSession.setCallback(new MediaSessionCompat.Callback() {
                // Implement callbacks
                @Override
                public void onPlay() {
                    super.onPlay();
                    resumeMedia();
                    buildNotification(PlaybackStatus.PLAYING);
                }

                @Override
                public void onPause() {
                    super.onPause();
                    pauseMedia();
                    buildNotification(PlaybackStatus.PAUSED);
                }

                @Override
                public void onSkipToNext() {
                    super.onSkipToNext();
                    skipToNext();
                    updateMetaData();
                    buildNotification(PlaybackStatus.PLAYING);
                }

                @Override
                public void onSkipToPrevious() {
                    super.onSkipToPrevious();
                    skipToPrevious();
                    updateMetaData();
                    buildNotification(PlaybackStatus.PLAYING);
                }

                @Override
                public void onStop() {
                    super.onStop();
                    removeNotification();
                    //Stop the service
                    stopSelf();
                }

                @Override
                public void onSeekTo(long position) {
                    super.onSeekTo(position);
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMetaData() {
        try {
            Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                    R.drawable.bg_main); //replace with medias albumArt
            // Update the current metadata
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
//                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
//                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
//                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                    .build());
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void skipToNext() {
        try {
            if (audioIndex == list.size() - 1) {
                //if last in playlist
                audioIndex = 0;
                audio = list.get(audioIndex).getText();
                Log.d("TAG", " ms 1skipToNext : " + audioIndex + ":" + audio);
            } else {
                //get next in playlist
                audio = list.get(++audioIndex).getText();
                Log.d("TAG", " ms skipToNext 2: " + audioIndex + ":" + audio);
            }

            //Update stored index
            new StorageUtils(getApplicationContext()).storeAudioIndex(audioIndex);

            stopMedia();
            //reset mediaPlayer
            mediaPlayer.reset();
            initMediaPlayer();
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void skipToPrevious() {
        try {
            if (audioIndex == 0) {
                //if first in playlist
                //set index to the last of audioList
                audioIndex = list.size() - 1;
                audio = list.get(audioIndex).getText();
                Log.d("TAG", " ms 1skipTopre : " + audioIndex + ":" + audio);
            } else {
                //get previous in playlist
                audio = list.get(--audioIndex).getText();
                Log.d("TAG", " ms 2skipToNexpre: " + audioIndex + ":" + audio);
            }

            //Update stored index
            new StorageUtils(getApplicationContext()).storeAudioIndex(audioIndex);

            stopMedia();
            //reset mediaPlayer
            mediaPlayer.reset();
            initMediaPlayer();
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void buildNotification(PlaybackStatus playbackStatus) {
        try {
            int notificationAction = R.drawable.ic_ntf_pause;//needs to be initialized
            PendingIntent play_pauseAction = null;

            //Build a new notification according to the current state of the MediaPlayer
            if (playbackStatus == PlaybackStatus.PLAYING) {
                notificationAction = R.drawable.ic_ntf_pause;
                //create the pause action
                play_pauseAction = playbackAction(1);
            } else if (playbackStatus == PlaybackStatus.PAUSED) {
                notificationAction = R.drawable.ic_ntf_play;
                //create the play action
                play_pauseAction = playbackAction(0);
            }

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher); //replace with your own image

            file = new File(audio);
            // Create a new Notification
            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setShowWhen(false)
                    // Set the Notification style
                    .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                            // Attach our MediaSession token
                            .setMediaSession(mediaSession.getSessionToken())
                            // Show our playback controls in the compact notification view.
                            .setShowActionsInCompactView(0, 1, 2))
                    // Set the Notification color
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    // Set the large and small icons
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.ic_ntf_headset)
                    // Set Notification content information
//                    .setContentText(activeAudio.getArtist())
//                    .setContentTitle(activeAudio.getAlbum())
//                    .setContentInfo(activeAudio.getTitle())
                    .setContentTitle(file.getName())
//                    .setContentText("Playing audio")
                    .setContentInfo("Recent Audios")
                    .setContentText("Playing Recent Audios")
                    // Add playback actions
                    .addAction(R.drawable.ic_ntf_previous, "previous", playbackAction(3))
                    .addAction(notificationAction, "pause", play_pauseAction)
                    .addAction(R.drawable.ic_ntf_next, "next", playbackAction(2));

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void removeNotification() {
        try {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private PendingIntent playbackAction(int actionNumber) {
        try {
            Intent playbackAction = new Intent(this, MediaPlayerService.class);
            switch (actionNumber) {
                case 0:
                    // Play
                    playbackAction.setAction(ACTION_PLAY);
                    return PendingIntent.getService(this, actionNumber, playbackAction, 0);
                case 1:
                    // Pause
                    playbackAction.setAction(ACTION_PAUSE);
                    return PendingIntent.getService(this, actionNumber, playbackAction, 0);
                case 2:
                    // Next track
                    playbackAction.setAction(ACTION_NEXT);
                    return PendingIntent.getService(this, actionNumber, playbackAction, 0);
                case 3:
                    // Previous track
                    playbackAction.setAction(ACTION_PREVIOUS);
                    return PendingIntent.getService(this, actionNumber, playbackAction, 0);
                default:
                    break;
            }
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        try {
            if (playbackAction == null || playbackAction.getAction() == null) return;

            String actionString = playbackAction.getAction();
            if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
                transportControls.play();
            } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
                transportControls.pause();
            } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
                transportControls.skipToNext();
            } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
                transportControls.skipToPrevious();
            } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
                transportControls.stop();
            }
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            try {
                //Load data from SharedPreferences
                StorageUtils storage = new StorageUtils(getApplicationContext());
                list = storage.loadAudio();
//                list = RecentVoiceActivity.user_list;

                audioIndex = storage.loadAudioIndex();
                Log.d("TAG", "Audio data :" + list.size() + audioIndex + ":" + (audioIndex != -1 && audioIndex < list.size()) );
                if (audioIndex != -1 && audioIndex < list.size()) {
                    //index is in a valid range
                    audio = list.get(audioIndex).getText();
                } else {
                    stopSelf();
                }
            } catch (NullPointerException e) {
                stopSelf();
            }

            //Request audio focus
            if (requestAudioFocus() == false) {
                //Could not gain focus
                stopSelf();
            }

            if (mediaSessionManager == null) {
                try {
                    initMediaSession();
                    initMediaPlayer();
                } catch (RemoteException e) {
                    e.printStackTrace();
                    stopSelf();
                }
                buildNotification(PlaybackStatus.PLAYING);
            }

            //Handle Intent action from MediaSession.TransportControls
            handleIncomingActions(intent);
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}