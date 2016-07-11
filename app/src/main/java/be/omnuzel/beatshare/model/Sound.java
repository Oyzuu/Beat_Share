package be.omnuzel.beatshare.model;

import android.util.Log;

import java.util.Arrays;

public class Sound {
    private String name;
    private int[]  matrix;
    private int    id;

    public Sound(String name, int id) {
        int[] pads = new int[16];

        for (int i = 0; i < pads.length; i++){
            pads[i] = 0;
        }

        this.name   = name;
        this.matrix = pads;
        this.id     = id;
        Log.i("CONSTRUCT_SOUND_MATR", Arrays.toString(this.matrix));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[] matrix) {
        this.matrix = matrix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
