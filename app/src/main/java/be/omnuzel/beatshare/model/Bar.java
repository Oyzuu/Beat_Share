package be.omnuzel.beatshare.model;


import java.util.TreeMap;

public class Bar {

    private TreeMap<Integer, Integer[]> sounds;

    public Bar() {}

    public void updateSound(int soundId, Integer[] matrix) {
        sounds.put(soundId, matrix);
    }
}
