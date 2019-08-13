package de.uol.swp.common.user.dto;

import de.uol.swp.common.user.User;

import java.util.Objects;

public class UserDTO implements User, Comparable<UserDTO> {

    private final String username;
    private final String password;
    private final String eMail;

    public UserDTO(String username, String password, String eMail) {
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
    public User getWithoutPassword() {
        return new UserDTO(username, null, eMail);
    }

    @Override
    public int compareTo(UserDTO o) {
        return this.getUsername().compareTo(o.getUsername());
    }
}
