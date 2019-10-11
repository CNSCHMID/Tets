package de.uol.swp.client.register.event;

public class RegistrationErrorEvent {
    private final String message;

    public RegistrationErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
