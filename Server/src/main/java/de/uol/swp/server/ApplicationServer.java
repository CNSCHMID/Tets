package de.uol.swp.server;

import com.google.common.eventbus.EventBus;
import de.uol.swp.server.communication.Server;
import de.uol.swp.server.usermanagement.UserService;
import de.uol.swp.server.usermanagement.store.UserStore;
import de.uol.swp.server.usermanagement.store.SimpleUserStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("UnstableApiUsage")
public class ApplicationServer {

	private static final Logger LOG = LogManager.getLogger(ApplicationServer.class);
	
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
		UserStore userStore = new SimpleUserStore();
		// FIXME: Remove after registration is implemented
		userStore.createUser("test","test","test@test.de");
		userStore.createUser("test1","test1","test1@test.de");
		userStore.createUser("test2","test2","test2@test.de");
		userStore.createUser("test3","test3","test3@test.de");

		// create components (linked by eventBus)
		new UserService(eventBus, userStore);
		new Server(port, eventBus).start();
	}
	
}
