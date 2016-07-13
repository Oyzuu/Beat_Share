package be.omnuzel.beatshare.model;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class City implements Parcelable {

    private long    id;
    private String  name;
    private Country country;

    public City() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        Log.i("LOC-CITY", "City name : " + name);
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
        Log.i("LOC-CITY", "Country : " + country);
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", country=" + country +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeLong       (id);
        destination.writeString     (name);
        destination.writeTypedObject(country, 0);
    }

    public static Creator<City> CREATOR = new Creator<City>() {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public City createFromParcel(Parcel source) {
            City city = new City();

            city.setId     (source.readLong());
            city.setName   (source.readString());
            city.setCountry(source.readTypedObject(Country.CREATOR));

            return city;
        }

        @Override
        public City[] newArray(int size) {
            return new City[0];
        }
    };
}
