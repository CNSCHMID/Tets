package de.uol.swp.common.user;

public interface ISession {
    String getSessionId();
    boolean isValid();
}
