package be.omnuzel.beatshare.model;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Location implements Parcelable {
    private long          id;
    private double        latitude,
                          longitude;
    private Neighbourhood neighbourhood;

    public Location() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Neighbourhood getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(Neighbourhood neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", neighbourhood=" + neighbourhood +
                '}';
    }

    /**
     * Return a hydrated Location from coordinates and a JSON string
     * @param latitude latitude of the location
     * @param longitude longitude of the location
     * @param json a JSON from reverse Geocoding API
     * @return Location
     * @throws JSONException
     */
    public static Location hydrateFromJSON(double latitude, double longitude, String json) throws JSONException {
        JSONObject jo = new JSONObject(json);

        JSONArray  results                 = jo     .getJSONArray("results");
        JSONObject resultFirstObject       = results.getJSONObject(0);
        JSONObject resultBeforeLastObject  = results.getJSONObject(results.length()-2);

        // splitting the complete address to get neighbourhood's name
        // is easier this way due to data model inconsistency
        String   fullAddress       = resultFirstObject.optString("formatted_address", "ERROR");
        String[] addressArray      = fullAddress.split(", ");
        String[] neighArray        = addressArray[1].split(" ");
        String   neighbourhoodName = neighArray[1];

        String   cityCountryString  = resultBeforeLastObject.optString("formatted_address", "ERROR");
        String[] cityCountryArray   = cityCountryString.split(", ");
        String   cityName           = cityCountryArray[0];
        String   countryName        = cityCountryArray[1];

        Log.i("LOC HYDRATE", neighbourhoodName);
        Log.i("LOC HYDRATE", cityName);
        Log.i("LOC HYDRATE", countryName);

        Country country = new Country();

        country.setName(countryName);

        City city = new City();

        city.setName   (cityName);
        city.setCountry(country);

        Neighbourhood neighbourhood = new Neighbourhood() ;

        neighbourhood.setName(neighbourhoodName);
        neighbourhood.setCity(city);

        Log.i("LOC HYDRATE", "Neigh state : " + neighbourhood);

        Location location = new Location();

        location.setLatitude     (latitude);
        location.setLongitude    (longitude);
        location.setNeighbourhood(neighbourhood);

        return location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeLong       (id);
        destination.writeDouble     (latitude);
        destination.writeDouble     (longitude);
        destination.writeTypedObject(neighbourhood, 0);
    }

    public static Creator<Location> CREATOR = new Creator<Location>() {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public Location createFromParcel(Parcel source) {
            Location location = new Location();

            location.setId           (source.readLong());
            location.setLatitude     (source.readDouble());
            location.setLongitude    (source.readDouble());
            location.setNeighbourhood(source.readTypedObject(Neighbourhood.CREATOR));

            return location;
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[0];
        }
    };
}
