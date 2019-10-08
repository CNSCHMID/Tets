package de.uol.swp.common.lobby;

import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.dto.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyDTOTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final User notInLobbyUser = new UserDTO("no", "marco", "no@grawunder.de");

    private static final int NO_USERS = 10;
    private static final List<UserDTO> users;

    static {
        users = new ArrayList<>();
        for (int i = 0; i < NO_USERS; i++) {
            users.add(new UserDTO("marco" + i, "marco" + i, "marco" + i + "@grawunder.de"));
        }
        Collections.sort(users);
    }

    @Test
    void createLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);

        assertEquals(lobby.getName(), "test");
        assertEquals(lobby.getUsers().size(), 1);
        assertEquals(lobby.getUsers().iterator().next(), defaultUser);

    }

    @Test
    void joinUserLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);

        lobby.joinUser(users.get(0));
        assertEquals(lobby.getUsers().size(), 2);
        assertTrue(lobby.getUsers().contains(users.get(0)));

        lobby.joinUser(users.get(0));
        assertEquals(lobby.getUsers().size(), 2);

        lobby.joinUser(users.get(1));
        assertEquals(lobby.getUsers().size(), 3);
        assertTrue(lobby.getUsers().contains(users.get(1)));
    }

    @Test
    void leaveUserLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);
        users.forEach(lobby::joinUser);

        assertEquals(lobby.getUsers().size(), users.size() + 1);
        lobby.leaveUser(users.get(5));

        assertEquals(lobby.getUsers().size(), users.size() + 1 - 1);
        assertFalse(lobby.getUsers().contains(users.get(5)));
    }

    @Test
    void removeOwnerFromLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);
        users.forEach(lobby::joinUser);

        lobby.leaveUser(defaultUser);

        assertNotEquals(lobby.getOwner(), defaultUser);
        assertTrue(users.contains(lobby.getOwner()));

    }

    @Test
    void updateOwnerTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);
        users.forEach(lobby::joinUser);

        lobby.updateOwner(users.get(6));
        assertEquals(lobby.getOwner(), users.get(6));

        assertThrows(IllegalArgumentException.class, () -> lobby.updateOwner(notInLobbyUser));
    }

    @Test
    void assureNonEmptyLobbyTest() {
        Lobby lobby = new LobbyDTO("test", defaultUser);

        assertThrows(IllegalArgumentException.class, () -> lobby.leaveUser(defaultUser));
    }


}
