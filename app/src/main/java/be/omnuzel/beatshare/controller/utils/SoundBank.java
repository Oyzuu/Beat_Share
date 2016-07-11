package be.omnuzel.beatshare.controller.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Facilitate sound management with the sequencer
 */
public class SoundBank {

    public static SoundBank instance;
    public static SoundBank getInstance(Context context) {
        if (instance == null)
            instance = new SoundBank(context);
        return instance;
    }

    private SoundPool  soundPool;
    private Context    context;
    private boolean    isLoaded;
    private int        maxSoundId;

    private HashMap<Integer, Integer> sounds = new HashMap<>();

    public SoundBank(Context context) {
        this.context  = context;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }
        else {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage      (AudioAttributes.USAGE_GAME)
                    .build         ();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams     (6)
                    .setAudioAttributes(audioAttributes)
                    .build             ();
        }
    }

    public void load(int resourceId) {
        int id = soundPool.load(context, resourceId, 0);
        Log.i("SOUNDBANK_LOAD", "Sample " + id + " loaded !");

        sounds.put(resourceId, id);
        maxSoundId = id;
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

    public void setLoadingState(boolean state) {
        isLoaded = state;
    }

    public boolean getLoadingState() {
        return  isLoaded;
    }

    public int getMaxSoundId() {
        return maxSoundId;
    }
}
