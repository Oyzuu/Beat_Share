package be.omnuzel.beatshare.model;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

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

    public static Location hydrateFromJSON(double latitude, double longitude, String json) throws JSONException {
        JSONObject jo = new JSONObject(json);

        JSONArray  results        = jo           .getJSONArray("results");
        JSONObject resultDetails  = results      .getJSONObject(0);
        JSONArray  addrComponents = resultDetails.getJSONArray("address_components");

        JSONObject neighbourhoodDetails = addrComponents.getJSONObject(2);
        JSONObject cityDetails          = addrComponents.getJSONObject(3);
        JSONObject countryDetails       = addrComponents.getJSONObject(4);

        String neighbourhoodName = neighbourhoodDetails.optString("long_name", "ERROR");
        String cityName          = cityDetails         .optString("long_name", "ERROR");
        String countryName       = countryDetails      .optString("long_name", "ERROR");

        Country country = new Country();
        country.setName(countryName);

        City city = new City();
        city.setName(cityName);
        city.setCountry(country);

        Neighbourhood neighbourhood = new Neighbourhood() ;
        neighbourhood.setName(neighbourhoodName);
        neighbourhood.setCity(city);

        Location location = new Location();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setNeighbourhood(neighbourhood);

        return location;
    }

    // Parcelable

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

            return null;
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[0];
        }
    };
}
