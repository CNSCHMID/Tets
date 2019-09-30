package de.uol.swp.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.swp.common.user.dto.UserDTO;
import de.uol.swp.server.communication.Server;
import de.uol.swp.server.di.ServerModule;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ServerApp {

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

		// create components
		Injector injector = Guice.createInjector(new ServerModule());
		createServices(injector);
		injector.getInstance(Server.class).start(port);
	}

	private static void createServices(Injector injector) {
		UserManagement userManagement = injector.getInstance(UserManagement.class);

		// TODO: Remove after registration is implemented
		for (int i = 0; i < 5; i++) {
			userManagement.createUser(new UserDTO("test" + i, "test" + i, "test" + i + "@test.de"));
		}

		// Remark: As these services are not referenced by any other class
		// we will need to create instances here (and inject dependencies)
		injector.getInstance(UserService.class);
		injector.getInstance(AuthenticationService.class);
	}

}
