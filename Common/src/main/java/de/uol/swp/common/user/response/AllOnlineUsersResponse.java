package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.dto.UserDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AllOnlineUsersResponse extends AbstractResponseMessage {

    final private ArrayList<UserDTO> users = new ArrayList<>();

    public AllOnlineUsersResponse(){
        // needed for serialization
    }

    public AllOnlineUsersResponse(Collection<User> users) {
        for (User user : users) {
            this.users.add(UserDTO.createWithoutPassword(user));
        }
    }

    public List<UserDTO> getUsers() {
        return users;
    }

}
