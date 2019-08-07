package de.uol.swp.common.user;

import java.io.Serializable;

public interface IUser extends Serializable {
    String getUsername();
    String getPassword();
    String getEMail();

    IUser getWithoutPassword();
}
