package be.omnuzel.beatshare.controller.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;

/**
 * Facilitate sound management with the sequencer
 */
public class SoundBank {

    private final SoundPool soundPool;
    private final Context context;
    private final int totalSounds;

    private final HashMap<Integer, Integer> sounds = new HashMap<>();

    public SoundBank(Context context, int totalSounds) {
        this.context = context;
        this.totalSounds = totalSounds;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(totalSounds, AudioManager.STREAM_MUSIC, 0);
        } else {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(totalSounds)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
    }

    public void load(int resourceId) {
        int id = soundPool.load(context, resourceId, 0);

        sounds.put(resourceId, id);
    }

    public void play(int resourceId) {
        if (sounds.get(resourceId) != null) {
            int soundId = sounds.get(resourceId);
            soundPool.play(soundId, 1, 1, 1, 0, 1);
        }
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }
}
