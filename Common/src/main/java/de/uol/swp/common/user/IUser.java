package de.uol.swp.common.user;

public interface IUser {
    String getUsername();
    String getPassword();
    String getEMail();

    IUser getWithoutPassword();
}
