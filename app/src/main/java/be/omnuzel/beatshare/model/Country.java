package be.omnuzel.beatshare.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Country implements Parcelable {

    private long   id;
    private String name;

    public Country() {}

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

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeLong  (id);
        destination.writeString(name);
    }

    public static Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel source) {
            Country country = new Country();

            country.setId  (source.readLong());
            country.setName(source.readString());
            return null;
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[0];
        }
    };
}
