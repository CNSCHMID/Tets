package de.uol.swp.server;

import com.google.common.eventbus.EventBus;
import de.uol.swp.server.communication.Server;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.UserService;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("UnstableApiUsage")
public class ServerApp {

	private static final Logger LOG = LogManager.getLogger(ServerApp.class);
	
	public static void main(String[] args) throws Exception {
		int port = - 1;
		if (args.length == 1){
			try{
				port = Integer.parseInt(args[0]);
			}catch(Exception e){
				// Ignore and use default value
			}
		}
		if (port < 0){
			port = 8889;
		}
		LOG.info("Starting Server on port "+port);

		// Create dependencies:
		EventBus eventBus = new EventBus();
		UserStore userStore = new MainMemoryBasedUserStore();
		// FIXME: Remove after registration is implemented
		userStore.createUser("test","test","test@test.de");
		userStore.createUser("test1","test1","test1@test.de");
		userStore.createUser("test2","test2","test2@test.de");
		userStore.createUser("test3","test3","test3@test.de");

		// create components
		UserManagement userManagement = new UserManagement(userStore);
		new AuthenticationService(eventBus, userManagement);
		new UserService(eventBus, userManagement);
		new Server(port, eventBus).start();
	}
	
}
