package com.propya.suraksha.Helpers;

import android.content.Context;
import android.media.AudioManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.media.VolumeProviderCompat;

public class VolumeKeyController {

    private MediaSessionCompat mMediaSession;
    private final Context mContext;

    public VolumeKeyController(Context context) {
        mContext = context;
    }

    private void createMediaSession() {
        mMediaSession = new MediaSessionCompat(mContext, VolumeKeyController.class.getSimpleName());
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0)
                .build());
        mMediaSession.setPlaybackToRemote(getVolumeProvider());
        mMediaSession.setActive(true);
    }

    private VolumeProviderCompat getVolumeProvider() {
        final AudioManager audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
        int currentVolume = audio.getStreamVolume(STREAM_TYPE);
        int maxVolume = audio.getStreamMaxVolume(STREAM_TYPE);
        final int VOLUME_UP = 1;
        final int VOLUME_DOWN = -1;

        return new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, maxVolume, currentVolume) {
            @Override
            public void onAdjustVolume(int direction) {
                // Up = 1, Down = -1, Release = 0
                Toast.makeText(mContext, "Pressed", Toast.LENGTH_SHORT).show();
                Log.i("keysPressed"," "+direction);
                // Replace with your action, if you don't want to adjust system volume
                if (direction == VOLUME_UP) {
                    audio.adjustStreamVolume(STREAM_TYPE,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }
                else if (direction == VOLUME_DOWN) {
                    audio.adjustStreamVolume(STREAM_TYPE,
                            AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                }
                setCurrentVolume(audio.getStreamVolume(STREAM_TYPE));
            }
        };
    }

    // Call when control needed, add a call to constructor if needed immediately
    public void setActive(boolean active) {
        if (mMediaSession != null) {
            mMediaSession.setActive(active);
            return;
        }
        createMediaSession();
    }

    // Call from Service's onDestroy method
    public void destroy() {
        if (mMediaSession != null) {
            mMediaSession.release();
        }
    }
}