package be.omnuzel.beatshare.controller.threads;

import android.content.Context;
import android.media.SoundPool;

import java.util.ArrayList;
import java.util.TreeMap;

import be.omnuzel.beatshare.controller.utils.SoundBank;
import be.omnuzel.beatshare.model.Sequence;

public class PlaybackThread extends Thread {
    public interface PlaybackListener {
        int getState();
        int getBPM();
    }

    public static final int
            STOPPED = 0,
            PLAYING = 1,
            PAUSED  = 2,

            MINUTE  = 60000;

    private PlaybackListener callback;
    private int              currentStep;
    private Sequence         sequence;
    private SoundBank        soundBank;

    private TreeMap<Integer, ArrayList<Integer>> sequenceMap;

    public PlaybackThread(Context context, Sequence sequence) {
        this.callback    = (PlaybackListener) context;
        this.currentStep = 0;
        this.sequence    = sequence;
        this.sequenceMap = sequence.getSoundsMap();

        soundBank = new SoundBank(context);
        for (int soundId : sequence.getDistinctSoundsId()) {
            soundBank.load(soundId);
        }

        soundBank.getSoundPool().setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

            }
        });

        // TODO check readiness of soundpool !!!
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            int state = callback.getState();
            if (state == STOPPED) {
                currentStep = 0;
                break;
            }

            if (state == PAUSED) {
                break;
            }

            int sleepTime = MINUTE / callback.getBPM() / 4;

            // TODO MAKE THIS LOOP HAPPEN !
            for (int soundId : sequenceMap.get(currentStep)) {
                soundBank.play(soundId);
            }

            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (currentStep == 63)
                break;

            currentStep++;
        }
    }
}
