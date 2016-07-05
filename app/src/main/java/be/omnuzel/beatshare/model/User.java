package be.omnuzel.beatshare.model;

import java.util.ArrayList;

public class User {

    private String
            userName,
            password,
            email;

    private ArrayList<Role> roles;

    private long id;

    public User() {
        roles = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
