package be.omnuzel.beatshare.model.alternate;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isdc on 9/11/16.
 */

public class SugarBar extends SugarRecord {

    private List<SugarSound> sounds;

    public SugarBar() {
    }

    public void init() {
        sounds = new ArrayList<>();
    }

    public List<SugarSound> getSounds() {
        return sounds;
    }

    public void setSounds(List<SugarSound> sounds) {
        this.sounds = sounds;
    }

    public List<String> getNames() {
        ArrayList<String> names = new ArrayList<>();

        if (sounds.size() == 0) {
            names.add("empty");
        } else {
            for (SugarSound sound : sounds) {
                names.add(sound.getName());
            }
        }

        return names;
    }
}
