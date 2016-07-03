package be.omnuzel.beatshare.classes;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

public class SoundBank {

    public static SoundBank instance;
    public static SoundBank getInstance(Context context) {
        if (instance == null)
            instance = new SoundBank(context);
        return instance;
    }

    private SoundPool soundPool;
    private Context   context;

    private HashMap<Integer, Integer> pads = new HashMap<>();

    public SoundBank(Context context) {
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

        this.context = context;
    }

    public void load(int resourceId, int buttonId) {
        int id = soundPool.load(context, resourceId, 0);
        Log.i("SOUNDBANK_LOAD", "Sample " + id + " loaded !");
        pads.put(buttonId, id);
    }

    public void play(int buttonId) {
        if (pads.get(buttonId) != null)
            soundPool.play(pads.get(buttonId), 1, 1, 1, 0, 1);
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }
}
