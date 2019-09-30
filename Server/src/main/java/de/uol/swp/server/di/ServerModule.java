package de.uol.swp.server.di;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;

public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        // All usermanagements and eventbusses must be the same instance (!)
        bind(UserManagement.class).toInstance(new UserManagement(new MainMemoryBasedUserStore()));
        bind(EventBus.class).toInstance(new EventBus());

    }
}
