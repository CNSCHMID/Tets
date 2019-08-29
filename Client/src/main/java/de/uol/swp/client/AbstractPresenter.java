package de.uol.swp.client;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.user.UserService;

import java.util.Objects;

public class AbstractPresenter {

    protected UserService userService;
    protected EventBus eventBus;

    public void setUserService(UserService userService) {
        Objects.requireNonNull(userService);
        this.userService = userService;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    public void clearEventBus(){
        this.eventBus.unregister(this);
        this.eventBus = null;
    }
}
