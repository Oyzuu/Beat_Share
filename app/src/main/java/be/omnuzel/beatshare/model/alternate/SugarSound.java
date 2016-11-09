package be.omnuzel.beatshare.model.alternate;

import com.orm.SugarRecord;

/**
 * Created by isdc on 9/11/16.
 */
public class SugarSound extends SugarRecord {

    private String name;
    private int[] matrix;
    private int soundID;

    public SugarSound() {

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

    public int getSoundID() {
        return soundID;
    }

    public void setSoundID(int soundID) {
        this.soundID = soundID;
    }
}
