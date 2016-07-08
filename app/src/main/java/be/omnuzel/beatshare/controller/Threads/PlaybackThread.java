package be.omnuzel.beatshare.controller.Threads;

import android.media.SoundPool;
import android.util.Log;

import java.util.Arrays;

public class PlaybackThread extends Thread {

    private SoundPool soundPool;
    private int[] timestamps, sounds, sleeps;

    public PlaybackThread(String sequence, SoundPool soundPool) {
        this.soundPool = soundPool;

        if (sequence.equals(""))
            return;

        String[] soundArray = sequence.split(",");
        timestamps = new int[soundArray.length];
        sounds     = new int[soundArray.length];
        sleeps     = new int[soundArray.length];

        for (int i = 0; i < soundArray.length; i++) {
            String[] details = soundArray[i].split("-");

            timestamps[i] = Integer.parseInt(details[0]);
            sounds[i]     = Integer.parseInt(details[1]);
        }

        for (int i = 0; i < timestamps.length; i++) {
            if (i == timestamps.length - 1)
                sleeps[i] = 0;
            else
                sleeps[i] = timestamps[i+1] - timestamps[i];
        }

        Log.i("TIMESTAMPS", Arrays.toString(timestamps));
        Log.i("SLEEPS",     Arrays.toString(sleeps));
        Log.i("SOUNDS_ID",  Arrays.toString(sounds));
    }

    @Override
    public void run() {
        if (sounds == null)
            return;

        for (int i = 0; i < sounds.length; i++) {
            soundPool.play(sounds[i], 1, 1, 1, 0, 1);

            try {
                Thread.sleep(sleeps[i]);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
