package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * A message containing the session (typically for a new logged in user)
 *
 * @author Marco Grawunder
 */
public class LoginSuccessfulResponse extends AbstractResponseMessage {

    private static final long serialVersionUID = -9107206137706636541L;

    private final User user;

    public LoginSuccessfulResponse(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
