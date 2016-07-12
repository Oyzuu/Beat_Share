package be.omnuzel.beatshare.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class Sequence {

    private TreeMap<Integer, ArrayList<Integer>> soundsMap;
    private ArrayList<Bar>                       bars;
    private Set<Integer>                         distinctSoundsId;

    public Sequence() {
        bars      = new ArrayList<>();
        soundsMap = new TreeMap<>();
        distinctSoundsId = new HashSet<>();
    }

    public void addBar(Bar bar) {
        bars.add(bar);
    }

    /**
     * Build a map with sequence step as KEY and sounds to play at that step as VALUE
     */
    public void build() {
//        long start = System.currentTimeMillis();

        for (int i = 0; i < bars.size() * 16; i++) {
            soundsMap.put(i, new ArrayList<Integer>());
        }

        int i = 0;
        for (Bar bar : bars) {
            if (bar.getActiveSounds().size() == 0) {
                break;
            }

            for (Sound sound : bar.getActiveSounds()) {
                int[] matrix = sound.getMatrix();
                distinctSoundsId.add(sound.getId());

                for (int j = 0; j < matrix.length; j++) {
                    if (matrix[j] == 1) {
                        soundsMap.get(j + (16 * i)).add(sound.getId());
                    }
                }
            }

            i++;
        }

//        for (Map.Entry<Integer, ArrayList<Integer>> entry : soundsMap.entrySet()) {
//            Log.i("key", entry.getKey() + "");
//            Log.i("value", entry.getValue() + "");
//        }
//        long elapsedTime = System.currentTimeMillis() - start;
//        Log.i("SEQUENCE BUILD", elapsedTime + " ms");
    }

    public Set<Integer> getDistinctSoundsId() {
        return distinctSoundsId;
    }

    public TreeMap<Integer, ArrayList<Integer>> getSoundsMap() {
        return soundsMap;
    }

    public int getTotalBars() {
        return bars.size();
    }
}
