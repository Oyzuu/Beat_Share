package be.omnuzel.beatshare.model;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable {
    private long   id;
    private double latitude,
                   longitude;
    private City   city;

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

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", city=" + city +
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
        destination.writeDouble     (latitude);
        destination.writeDouble     (longitude);
        destination.writeTypedObject(city, 0);
    }

    public static Creator<Location> CREATOR = new Creator<Location>() {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public Location createFromParcel(Parcel source) {
            Location location = new Location();

            location.setId       (source.readLong());
            location.setLatitude (source.readDouble());
            location.setLongitude(source.readDouble());
            location.setCity     (source.readTypedObject(City.CREATOR));

            return null;
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[0];
        }
    };
}
