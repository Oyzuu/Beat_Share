package be.omnuzel.beatshare.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Sequence {

    private TreeMap<Integer, ArrayList<Integer>> soundsMap;
    private ArrayList<Bar>                       bars;
    private Set<Integer>                         distinctSoundsId;

    private String   name;
    private User     author;
    private Location location;

    public Sequence() {
        this.bars             = new ArrayList<>();
        this.soundsMap        = new TreeMap<>();
        this.distinctSoundsId = new HashSet<>();

        this.name             = "";
        this.author           = null;
        this.location         = null;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void addBar(Bar bar) {
        bars.add(bar);
    }

    /**
     * Build a map with sequence step as KEY and sounds to play at that step as VALUE
     */
    public void build() {
        for (int i = 0; i < bars.size() * 16; i++)
            soundsMap.put(i, new ArrayList<Integer>());

        int i = 0;
        for (Bar bar : bars) {
            if (bar.getActiveSounds().size() == 0)
                break;

            for (Sound sound : bar.getActiveSounds()) {
                int[] matrix = sound.getMatrix();
                distinctSoundsId.add(sound.getId());

                for (int j = 0; j < matrix.length; j++) {
                    if (matrix[j] == 1)
                        soundsMap.get(j + (16 * i)).add(sound.getId());
                }
            }

            i++;
        }
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

    public String toJSON() throws JSONException{
        this.build();

        JSONObject json     = new JSONObject();

        json.put("name",   this.name);
        json.put("author", this.author.getName());

        JSONObject location = new JSONObject();

        location.put("latitude",      this.location.getLatitude());
        location.put("longitude",     this.location.getLongitude());
        Neighbourhood neighbourhood = this.location.getNeighbourhood();
        location.put("neighbourhood", neighbourhood.getName());
        location.put("city",          neighbourhood.getCity().getName());
        location.put("country",       neighbourhood.getCity().getCountry().getName());

        json.put("location", location);

        JSONObject sequence = new JSONObject();

        for (Map.Entry<Integer, ArrayList<Integer>> entry : soundsMap.entrySet()) {
            JSONArray stepArray = new JSONArray();

            for (Integer soundId : entry.getValue())
                stepArray.put(soundId);

            sequence.put(entry.getKey() + "", stepArray);
        }

        json.put("sequence", sequence);

        System.out.println(json.toString(4));
        return json.toString();
    }
}
