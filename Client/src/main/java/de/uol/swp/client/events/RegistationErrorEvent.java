package de.uol.swp.client.events;

public class RegistationErrorEvent {
    private final String message;

    public RegistationErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
