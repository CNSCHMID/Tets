package de.uol.swp.client.di;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import de.uol.swp.client.user.UserService;
import javafx.fxml.FXMLLoader;

public class ClientModule extends AbstractModule {
    EventBus eventBus = new EventBus();

    @Override
    protected void configure() {
        bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);
        bind(EventBus.class).toInstance(eventBus);
        bind(de.uol.swp.common.user.UserService.class).to(UserService.class);
    }
}
