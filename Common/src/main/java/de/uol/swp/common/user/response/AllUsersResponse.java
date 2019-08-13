package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

public class AllUsersResponse extends AbstractResponseMessage {

    private ArrayList<UserDTO> users = new ArrayList<>();

    public AllUsersResponse(){
        // neeed for serialization
    }

    public AllUsersResponse(List<User> users) {
        for (User user : users) {
            this.users.add(UserDTO.createWithoutPassword(user));
        }
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserDTO> users) {
        this.users = users;
    }
}
