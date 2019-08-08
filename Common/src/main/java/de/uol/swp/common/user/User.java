package de.uol.swp.common.user;

import java.util.Objects;

public class User implements IUser, Comparable<User> {

    private final String username;
    private final String password;
    private final String eMail;

    public User(String username, String password, String eMail) {
        Objects.nonNull(username);
        this.username = username;
        this.password = password;
        this.eMail = eMail;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getEMail() {
        return eMail;
    }

    @Override
    public IUser getWithoutPassword() {
        return new User(username, null, eMail);
    }

    @Override
    public int compareTo(User o) {
        return this.getUsername().compareTo(o.getUsername());
    }
}
