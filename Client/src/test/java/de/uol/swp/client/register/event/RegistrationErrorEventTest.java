package de.uol.swp.client.register.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistrationErrorEventTest {

    @Test
    void createRegistrationErrorEvent() {
        RegistrationErrorEvent event = new RegistrationErrorEvent("Test");

        assertEquals(event.getMessage(), "Test");
    }

}