package be.omnuzel.beatshare.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import be.omnuzel.beatshare.db.UserDAO;

public class Sequence {

    // In a perfect world, soundsMap would be an <SoundMap> object
    private TreeMap<Integer, ArrayList<Integer>> soundsMap;
    private ArrayList<Bar>                       bars;
    private Set<Integer>                         distinctSoundsId;


    private long     id;
    private String   name,
                     genre,
                     author;
    private int      bpm;
    private Location location;

    public Sequence() {
        this.bars             = new ArrayList<>();
        this.soundsMap        = new TreeMap<>();
        this.distinctSoundsId = new HashSet<>();

        this.name     = "";
        this.genre    = "";
        this.bpm      = 0;
        this.author   = null;
        this.location = null;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
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

    public String toJSON() throws JSONException {
        this.build();

        JSONObject jsonObject     = new JSONObject();

        jsonObject.put("name",   this.name);
        jsonObject.put("genre",  this.genre);
        jsonObject.put("bpm",    this.bpm);
        jsonObject.put("author", this.author);

        JSONObject location = new JSONObject();

        location.put("latitude",      this.location.getLatitude());
        location.put("longitude",     this.location.getLongitude());
        Neighbourhood neighbourhood = this.location.getNeighbourhood();
        location.put("neighbourhood", neighbourhood.getName());
        location.put("city",          neighbourhood.getCity().getName());
        location.put("country",       neighbourhood.getCity().getCountry().getName());

        jsonObject.put("location", location);

        JSONObject sequence = new JSONObject();

        for (Map.Entry<Integer, ArrayList<Integer>> entry : soundsMap.entrySet()) {
            JSONArray stepArray = new JSONArray();

            for (Integer soundId : entry.getValue())
                stepArray.put(soundId);

            sequence.put(entry.getKey() + "", stepArray);
        }

        jsonObject.put("sequence", sequence);

        Log.i("JSON", jsonObject.toString(4));
        return jsonObject.toString();
    }

    public void fromJSON(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        this.name   = jsonObject.getString("name");
        this.genre  = jsonObject.getString("genre");
        this.bpm    = jsonObject.getInt   ("bpm");
        this.author = jsonObject.getString("author");

        JSONObject locationObject = jsonObject.getJSONObject("location");

        double lat         = locationObject.getDouble("latitude");
        double lon         = locationObject.getDouble("longitude");
        String neighName   = locationObject.getString("neighbourhood");
        String cityName    = locationObject.getString("city");
        String countryName = locationObject.getString("country");

        Country country = new Country();
        country.setName(countryName);

        City city = new City();
        city.setName(cityName);
        city.setCountry(country);

        Neighbourhood neighbourhood = new Neighbourhood();
        neighbourhood.setName(neighName);
        neighbourhood.setCity(city);

        Location location = new Location();
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setNeighbourhood(neighbourhood);
    }
}
