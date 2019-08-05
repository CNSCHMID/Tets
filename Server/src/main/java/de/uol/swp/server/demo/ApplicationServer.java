package de.uol.swp.server.demo;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.user.IUserService;
import de.uol.swp.server.communication.Server;
import de.uol.swp.server.usermanagement.IUserStore;
import de.uol.swp.server.usermanagement.SimpleUserStore;
import de.uol.swp.server.usermanagement.UserService;

public class ApplicationServer {

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
		System.out.println("Starting Server on port "+port);

		// Create dependencies:
		EventBus eventBus = new EventBus();
		IUserStore userStore = new SimpleUserStore();
		IUserService userService = 	new UserService(eventBus, userStore);

		new Server(port,userService, eventBus).start();
	}
	
}
