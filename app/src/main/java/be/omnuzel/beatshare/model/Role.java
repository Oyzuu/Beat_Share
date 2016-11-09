package be.omnuzel.beatshare.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Role implements Parcelable {
    private long id;
    private String name;

    public Role() {
    }

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
        return String.format(
                "Role(%s, %s)",
                id, name
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeLong(id);
        destination.writeString(name);
    }

    public static Creator<Role> CREATOR = new Creator<Role>() {
        @Override
        public Role createFromParcel(Parcel source) {
            Role role = new Role();

            role.setId(source.readLong());
            role.setName(source.readString());

            return role;
        }

        @Override
        public Role[] newArray(int size) {
            return new Role[0];
        }
    };
}
