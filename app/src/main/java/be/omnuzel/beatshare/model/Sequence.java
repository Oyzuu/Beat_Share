package be.omnuzel.beatshare.model;

import java.util.ArrayList;
import java.util.TreeMap;

public class Sequence {

    private TreeMap<Integer, ArrayList<Integer>> soundsMap;
    private ArrayList<Bar>                       bars;

    public Sequence() {
        bars      = new ArrayList<>();
        soundsMap = new TreeMap<>();
    }

    public void addBar(Bar bar) {
        bars.add(bar);
    }
}
