package de.uol.swp.common.user;

import java.io.Serializable;

public interface User extends Serializable, Comparable<User> {
    String getUsername();
    String getPassword();
    String getEMail();

    User getWithoutPassword();
}
