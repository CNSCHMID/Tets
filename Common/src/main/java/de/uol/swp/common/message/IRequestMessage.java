package de.uol.swp.common.message;

/**
 * A base interface for all messages from client to server
 *
 * @Author: Marco Grawunder
 */

public interface IRequestMessage extends IMessage {

    /**
     * State, if this request can only be used, if
     * the user is authorized (typically has a valid login)
     * @return
     */
    boolean authorizationNeeded();

}
