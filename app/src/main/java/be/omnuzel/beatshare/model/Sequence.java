package be.omnuzel.beatshare.model;

import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

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
        JSONObject jsonObject     = new JSONObject();

        jsonObject.put("name",   this.name);
        jsonObject.put("genre",  this.genre);
        jsonObject.put("bpm",    this.bpm);
        jsonObject.put("author", this.author);

        JSONObject locationObject = new JSONObject();

        locationObject.put("latitude",      this.location.getLatitude());
        locationObject.put("longitude",     this.location.getLongitude());
        Neighbourhood neighbourhood = this.location.getNeighbourhood();
        locationObject.put("neighbourhood", neighbourhood.getName());
        locationObject.put("city",          neighbourhood.getCity().getName());
        locationObject.put("country",       neighbourhood.getCity().getCountry().getName());

        jsonObject.put("location", locationObject);

        JSONObject sequenceObject = new JSONObject();

        int barNumber = 1;
        for (Bar bar : bars) {
            JSONObject barObject = new JSONObject();

            int soundNumber = 1;
            for (Sound sound : bar.getActiveSounds()) {
                JSONObject soundObject = new JSONObject();

                soundObject.put("name", sound.getName());
                soundObject.put("id",   sound.getId());

                JSONArray matrix;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    matrix = new JSONArray();

                    for (int n : sound.getMatrix())
                        matrix.put(n);
                }
                else
                    matrix = new JSONArray(sound.getMatrix());

                soundObject.put("matrix", matrix);
                barObject.put("sound" + soundNumber, soundObject);

                soundNumber++;
            }

            sequenceObject.put("bar" + barNumber, barObject);

            barNumber++;
        }

        jsonObject.put("sequence", sequenceObject);

        return jsonObject.toString();
    }

    public static Sequence fromJSON(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        String name   = jsonObject.getString("name");
        String genre  = jsonObject.getString("genre");
        int    bpm    = jsonObject.getInt   ("bpm");
        String author = jsonObject.getString("author");

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

        JSONObject sequenceObject = jsonObject.getJSONObject("sequence");

        Sequence sequence = new Sequence();
        sequence.setName    (name);
        sequence.setGenre   (genre);
        sequence.setBpm     (bpm);
        sequence.setAuthor  (author);
        sequence.setLocation(location);

        Log.i("FROM JSON", "sequence object length : " + sequenceObject.names().length());
        // bar loop
        for (int barNumber = 1; barNumber <= sequenceObject.names().length(); barNumber++) {
            JSONObject barObject = sequenceObject.getJSONObject("bar" + barNumber);
            Bar bar = new Bar();
            bar.getActiveSoundsNames().remove(0);

            Log.i("FROM JSON", "bar object length : " + barObject.names().length());
            // sound loop
            for (int soundNumber = 1; soundNumber <= barObject.names().length(); soundNumber++) {
                JSONObject soundObject = barObject.getJSONObject("sound" + soundNumber);

                String soundName = soundObject.getString("name");
                int    id        = soundObject.getInt("id");

                int[] matrix = new int[16];
                JSONArray matrixArray = soundObject.getJSONArray("matrix");

                for (int i = 0; i < matrixArray.length(); i++)
                    matrix[i] = matrixArray.getInt(i);

                bar.addSound(soundName, id);
                bar.updateSound(soundName, matrix);
                Log.i("SOUNDLOOP", soundNumber + "");
            }

            sequence.addBar(bar);
            Log.i("BARLOOP", barNumber + "");
        }

        Log.i("SEQ FROM JSON", sequence.toString());
        return sequence;
    }

    @Override
    public String toString() {
        return "Sequence{" +
                "bars=" + bars +
                ", name='" + name + '\'' +
                ", genre='" + genre + '\'' +
                ", author='" + author + '\'' +
                ", bpm=" + bpm +
                ", location=" + location +
                '}';
    }
}
