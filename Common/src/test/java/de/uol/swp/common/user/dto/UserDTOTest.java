package de.uol.swp.common.user.dto;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class UserDTOTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final User secondsUser = new UserDTO("marco2", "marco", "marco@grawunder.de");


    @Test
    void createUserWithEmptyName() {
        assertThrows(AssertionError.class, () -> new UserDTO(null, "", ""));
    }

    @Test
    void createUserWithEmptyPassword() {
        assertThrows(AssertionError.class, () -> new UserDTO("", null, ""));
    }

    @Test
    void createWithExistingUser() {

        User newUser = UserDTO.create(defaultUser);

        // Test with equals method
        assertEquals(newUser, defaultUser);

        // Test every attribute
        assertEquals(newUser.getUsername(), defaultUser.getUsername());
        assertEquals(newUser.getPassword(), defaultUser.getPassword());
        assertEquals(newUser.getEMail(), defaultUser.getEMail());
    }

    @Test
    void createWithExistingUserWithoutPassword() {
        User newUser = UserDTO.createWithoutPassword(defaultUser);

        // Test every attribute
        assertEquals(newUser.getUsername(), defaultUser.getUsername());
        assertEquals(newUser.getPassword(), "");
        assertEquals(newUser.getEMail(), defaultUser.getEMail());

        // Test with equals method
        assertEquals(newUser, defaultUser);
    }

    @Test
    void getWithoutPassword() {
        User userWithoutPassword = defaultUser.getWithoutPassword();

        assertEquals(userWithoutPassword.getUsername(), defaultUser.getPassword());
        assertEquals(userWithoutPassword.getPassword(), "");
        assertEquals(userWithoutPassword.getUsername(), defaultUser.getUsername());
    }

    @Test
    void usersNotEquals_User() {
        assertNotEquals(defaultUser, secondsUser);
    }

    @Test
    void usersNotEquals_String() {
        assertNotEquals(defaultUser, "Test");
    }

    @Test
    void userCompare() {
        assertEquals(defaultUser.compareTo(secondsUser), -1);
    }

    @Test
    void testHashCode() {
        User newUser = UserDTO.create(defaultUser);
        assertEquals(newUser.hashCode(), defaultUser.hashCode());

    }
}