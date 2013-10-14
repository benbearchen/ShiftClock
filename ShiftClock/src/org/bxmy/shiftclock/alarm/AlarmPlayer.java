package org.bxmy.shiftclock.alarm;

import java.io.IOException;

import org.bxmy.shiftclock.ShiftClockApp;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

public class AlarmPlayer {

    private MediaPlayer mMediaPlayer;

    public void playAlarmRing() {
        Log.d("shiftclock", "play alarm ring");
        stop();

        Context context = ShiftClockApp.getInstance().getApplicationContext();
        Uri uri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, uri);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.start();
    }

    public void stopAlarmRing() {
        Log.d("shiftclock", "stop alarm ring");
        stop();
    }

    private void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public boolean isPlaying() {
        return (mMediaPlayer != null) && mMediaPlayer.isPlaying();
    }
}
