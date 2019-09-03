package de.uol.swp.common.user.exception;

import de.uol.swp.common.message.AbstractResponseMessage;

public class RegistrationExceptionMessage extends AbstractResponseMessage {

    private final String message;

    public RegistrationExceptionMessage(String message){
        this.message = message;
    }

    @Override
    public String toString() {
        return "RegistrationExceptionMessage "+message;
    }
}
