package de.uol.swp.server;

import com.google.common.eventbus.EventBus;
import de.uol.swp.server.communication.Server;
import de.uol.swp.server.usermanagement.UserService;
import de.uol.swp.server.usermanagement.store.UserStore;
import de.uol.swp.server.usermanagement.store.SimpleUserStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationServer {

	static final Logger LOG = LogManager.getLogger(ApplicationServer.class);

	static UserService userService;

	public static void main(String[] args) throws Exception {
		int port = -1;
		if (args.length == 1){
			try{
				port = Integer.parseInt(args[0]);
			}catch(Exception e){
				port = -1;
			}
		}
		if (port < 0){
			port = 8889;
		}
		LOG.info("Starting Server on port "+port);

		// Create dependencies:
		EventBus eventBus = new EventBus();
		UserStore userStore = new SimpleUserStore();
		// avoid GC?
		userService = 	new UserService(eventBus, userStore);

		new Server(port, eventBus).start();
	}
	
}
