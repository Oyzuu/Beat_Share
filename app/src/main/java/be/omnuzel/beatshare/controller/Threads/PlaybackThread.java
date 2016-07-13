package be.omnuzel.beatshare.controller.threads;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import be.omnuzel.beatshare.controller.activities.SequencerActivity;
import be.omnuzel.beatshare.controller.utils.SoundBank;
import be.omnuzel.beatshare.model.Sequence;

public class PlaybackThread extends Thread {
    public interface PlaybackListener {
        int      getState();
        int      getBPM();
        int      getCurrentStep();
        Sequence getSequence();
        void     setCurrentStep(int currentStep);
    }

    public static final int
            MINUTE        = 60000,
            STEPS_PER_BAR = 16,
            TIMES_PER_BAR = 4;

    private PlaybackListener callback;
    private int              currentStep;
    private SoundBank        soundBank;
    private int              totalSteps;
    private Sequence         sequence;

    private TreeMap<Integer, ArrayList<Integer>> sequenceMap;

    public PlaybackThread(Context context) {
        this.callback    = (PlaybackListener) context;
        this.currentStep = callback.getCurrentStep();
        this.sequence    = callback.getSequence();
        sequence.build();

        this.sequenceMap = sequence.getSoundsMap();
        this.totalSteps  = sequence.getTotalBars() * STEPS_PER_BAR;

        final Set<Integer> distinctSounds = sequence.getDistinctSoundsId();

        this.soundBank   = new SoundBank(context, distinctSounds.size());

//        Log.i("PLAYTHREAD", "Total steps : " + totalSteps);

        for (int soundId : distinctSounds) {
            soundBank.load(soundId);
        }

        soundBank.getSoundPool().setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (sampleId == distinctSounds.size()) {
                    start();
                }
            }
        });
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            this.sequence = callback.getSequence();
            sequence.build();

            int state     = callback.getState();
            int sleepTime = MINUTE / callback.getBPM() / TIMES_PER_BAR;

            if (state == SequencerActivity.STOPPED) {
                break;
            }

            if (state == SequencerActivity.PAUSED) {
                callback.setCurrentStep(currentStep);
                break;
            }

            for (int soundId : sequenceMap.get(currentStep)) {
                soundBank.play(soundId);
            }

            try {
                sleep(sleepTime);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            currentStep++;
//            Log.i("THREAD", currentStep + "");

            if (currentStep == totalSteps) {
                currentStep = 0;
            }
        }

//        Log.i("THREAD", "has stopped");
    }
}
