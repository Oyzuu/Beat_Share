package be.omnuzel.beatshare.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class Bar {

    private ArrayList<Sound>  activeSounds;
    private ArrayList<String> activeSoundsNames;

    public Bar() {
        activeSounds      = new ArrayList<>();
        activeSoundsNames = new ArrayList<>();

        activeSoundsNames.add("empty");
    }

    public ArrayList<Sound> getActiveSounds() {
        return activeSounds;
    }

    public ArrayList<String> getActiveSoundsNames() {
        return activeSoundsNames;
    }

    /**
     * Update the matrix of said sound in the list of active sounds
     * @param soundName name of updated sound
     * @param matrix current matrix of the sound
     */
    public void updateSound(String soundName, int[] matrix) {
        if (soundName.equals("empty"))
            return;

        Sound sound = activeSounds.get(activeSoundsNames.indexOf(soundName));
        sound.setMatrix(matrix);
    }

    /**
     * Add a sound to the list of active sounds
     * @param soundName name of added sound
     * @param soundId identifier of the sound resource
     */
    public void addSound(String soundName, int soundId) {
        if (activeSoundsNames.indexOf(soundName) == -1) {
            activeSoundsNames.add(soundName);
            activeSounds     .add(new Sound(soundName, soundId));

//            Log.i("BAR", "Sound " + soundName + " added");
        }
    }

    /**
     * Get a 4*4 matrix of the sound calls in selected bar
     * @param soundName name of the sound
     * @return  matrix of the sound
     */
    public int[] getSoundMatrix(String soundName) {
//        Log.i("MATRIX CHANGE SOUND", Arrays.toString(activeSounds.get(activeSoundsNames.indexOf(soundName)).getMatrix()));
        return activeSounds.get(activeSoundsNames.indexOf(soundName)).getMatrix();
    }

    @Override
    public String toString() {
        return "Bar{" +
                "activeSounds=" + activeSounds +
                ", activeSoundsNames=" + activeSoundsNames +
                '}';
    }
}
