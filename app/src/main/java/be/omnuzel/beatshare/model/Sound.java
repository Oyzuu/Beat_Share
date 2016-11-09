package be.omnuzel.beatshare.model;

import java.util.Arrays;

public class Sound {
    private String name;
    private int[] matrix;
    private int id;

    public Sound(String name, int id) {
        this.name = name;
        this.matrix = new int[16];
        this.id = id;
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

    @Override
    public String toString() {
        return "Sound{" +
                "name='" + name + '\'' +
                ", matrix=" + Arrays.toString(matrix) +
                ", id=" + id +
                '}';
    }


}
