package de.uol.swp.server.usermanagement.store;


import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public abstract class AbstractUserStore implements UserStore {

    protected String hash(String toHash){
        return Hashing.sha256()
                .hashString(toHash, StandardCharsets.UTF_8)
                .toString();
    }

}
