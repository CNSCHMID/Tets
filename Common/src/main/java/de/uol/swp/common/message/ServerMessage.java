package de.uol.swp.common.message;

import de.uol.swp.common.user.Session;

import java.util.List;

/**
 * A message from server to a number of clients that
 * is not necessary a response to a request (ska server push)
 */
public interface ServerMessage extends Message {
    List<Session> getReceiver();

    void setReceiver(List<Session> receiver);
}
