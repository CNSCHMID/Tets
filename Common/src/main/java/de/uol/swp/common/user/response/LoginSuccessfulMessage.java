package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.IUser;

/**
 * A message containing the session (typically for a new logged in user)
 *
 * @author Marco Grawunder
 */
public class LoginSuccessfulMessage extends AbstractResponseMessage {

    private static final long serialVersionUID = -9107206137706636541L;

    final IUser user;

    public LoginSuccessfulMessage(IUser user) {
        this.user = user;
    }

    public IUser getUser() {
        return user;
    }

}
