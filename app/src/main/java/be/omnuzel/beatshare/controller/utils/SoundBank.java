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

    public interface ISoundBank {
        boolean isPlaying();
        boolean isRecording();
        boolean hasRecordingStarted();
        void    startRecording();

        void writeSound(int soundId);
    }

    public static SoundBank instance;
    public static SoundBank getInstance(Context context) {
        if (instance == null)
            instance = new SoundBank(context);
        return instance;
    }

    private SoundPool  soundPool;
    private Context    context;
    private ISoundBank callback;
    private boolean    isLoaded;
    private int        maxSoundId;

    private HashMap<Integer, Integer> pads = new HashMap<>();

    public SoundBank(Context context) {
        this.context  = context;
        this.callback = (ISoundBank) context;

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

    /**
     * Load a sound in the SoundPool and put key: button id, value: sound id in pads
     * @param resourceId Identifier of the sound resource
     * @param buttonId Identifier of the button to link with this sound
     */
    public void load(int resourceId, int buttonId) {
        int id = soundPool.load(context, resourceId, 0);
        Log.i("SOUNDBANK_LOAD", "Sample " + id + " loaded !");

        pads.put(buttonId, id);
        maxSoundId = id;
    }

    /**
     * Play the sound linked to the button
     * @param buttonId Identifier of pushed button
     */
    public void play(int buttonId) {
        if (pads.get(buttonId) != null) {
            int soundId = pads.get(buttonId);
            soundPool.play(soundId, 1, 1, 1, 0, 1);

            if (callback.isRecording()) {
                if (!callback.hasRecordingStarted())
                    callback.startRecording();
                callback.writeSound(soundId);
            }
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

    public LinkedList<Integer> getLoadedButtons() {
        return new LinkedList<>(pads.keySet());
    }

    public int getMaxSoundId() {
        return maxSoundId;
    }
}
