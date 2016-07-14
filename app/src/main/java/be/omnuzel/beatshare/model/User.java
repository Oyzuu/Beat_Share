package be.omnuzel.beatshare.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    private long id;

    private String
            userName,
            email,
            password;

    private ArrayList<Role> roles;

    public User() {
        roles = new ArrayList<>();
    }

    public String getName() {
        return userName;
    }

    public void setName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return String.format(
                "User(%s, %s, %s, %s, %s)",
                id, userName, email, password, roles
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags) {
        destination.writeLong     (id);
        destination.writeString   (userName);
        destination.writeString   (email);
        destination.writeString   (password);
        destination.writeTypedList(roles);
    }

    public static Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            User user = new User();

            user.setId      (source.readLong());
            user.setName(source.readString());
            user.setEmail   (source.readString());
            user.setPassword(source.readString());

            ArrayList<Role> arrayList = new ArrayList<>();
            source.readTypedList(arrayList, Role.CREATOR);
            user.setRoles(arrayList);

            return user;
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };
}
