package de.uol.swp.server.usermanagement.store;

import de.uol.swp.common.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStore {

    /**
     * Find a user by username and password
     *
     * @param username
     * @param password
     * @return The User without password information, if found
     */
    Optional<User> findUser(String username, String password);

    /**
     * Find a user only by name
     * @param username
     * @return The User without password information, if found
     */
    Optional<User> findUser(String username);

    /**
     * Create a new user
     * @param username
     * @param password
     * @param eMail
     * @return The User without password information
     */
    User createUser(String username, String password, String eMail);

    /**
     * Update user. Update only given fields. Username cannot be changed
     * @param username
     * @param password
     * @param eMail
     * @return The User without password information
     */
    User updateUser(String username, String password, String eMail);

    /**
     * Remove user from store
     *
     * @param username
     */
    void removeUser(String username);


    /**
     * Retrieves the list of all users.
     * @return A list of all users without password information
     */
    List<User> getAllUsers();


}
