package be.omnuzel.beatshare.model;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class Neighbourhood implements Parcelable {

    private long   id;
    private String name;
    private City   city;

    public Neighbourhood() {}

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
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Neighbourhood{" +
                "id=" + id +
                ", name='" + name + '\'' +
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
        destination.writeString     (name);
        destination.writeTypedObject(city, 0);
    }

    public static Creator<Neighbourhood> CREATOR = new Creator<Neighbourhood>() {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public Neighbourhood createFromParcel(Parcel source) {
            Neighbourhood neighbourhood = new Neighbourhood();

            neighbourhood.setId  (source.readLong());
            neighbourhood.setName(source.readString());
            neighbourhood.setCity(source.readTypedObject(City.CREATOR));

            return neighbourhood;
        }

        @Override
        public Neighbourhood[] newArray(int size) {
            return new Neighbourhood[0];
        }
    };
}
