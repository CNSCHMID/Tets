package de.uol.swp.client.demo;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.communication.object.Client;
import de.uol.swp.client.user.UserServiceFactory;
import de.uol.swp.common.user.IUserService;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.message.UsersListMessage;
import de.uol.swp.common.user.response.LoginSuccessfulMessage;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DemoApplication extends Thread implements IConnectionListener {

	static final Logger LOG = LogManager.getLogger(DemoApplication.class);

	static Channel channel = null;
	IUserService userService;

	@Override
	public void run() {
		// Wait for connection
		try {
			Thread.sleep(500);
		} catch (InterruptedException e2) {
		}

		System.out.println("Demo application started");
		userService = UserServiceFactory.getUserService();
		System.out.println("Calling login with wrong values");

		String userName = "egal";
		String password = "falsch";

		// In this simple example the service does not wait for a result and
		// sends
		// return values, so just call the method. A more complex example should
		// wait
		// for a server return value and deliver the session
		userService.login(userName, password);

		delay(1000);
		userService.retrieveAllUsers();

		delay(2000);
		// HashCode (so every client has its own username)
		userName = "test" + hashCode();
		password = "test" + hashCode();

		userService.login(userName, password);

		process();
		System.out.println("Demo application terminated");
	}

	private void delay(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void process() {
		while (!isInterrupted()) {

			// Simulate activity in gui
			synchronized (this) {
				try {
					this.wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	@Subscribe
	public void process(LoginSuccessfulMessage in){
		LOG.debug("Login for user " + in.getUser().getUsername()+ " successful " + in.getSession());
	}

	@Subscribe
	public void process(UserLoggedInMessage in) {
		if (in.getSession().isValid()) {
			LOG.debug("User " + in.getUsername()+ " logged in");
			userService.retrieveAllUsers();
		} else {
			System.err.println("Error logging in user!");
		}
	}

	@Subscribe
	public void receivedUsersList(UsersListMessage in) {
		System.out.println("Current list of logged in users " + in.getUsers());
	}



	@Subscribe
	public void userLeft(UserLoggedOutMessage in) {
		System.out.println("User " + in.getUsername() + " left");
	}




	@Override
	public void exceptionOccured(String e) {
		System.err.println("Server sends an exception " + e.getClass().getName() + " " + e);
		// e.printStackTrace();
	}

	@Override
	public void connectionEstablished(Channel ch) {
		channel = ch;
		synchronized (DemoApplication.class) {
			DemoApplication.class.notifyAll();
		}
	}

	public static void main(String[] args) throws Exception {
		DemoApplication appl = new DemoApplication();
		final String host;
		final int port;
		if (args.length != 2) {
			host = "localhost";
			port = 8889;
			System.err.println("Usage: " + Client.class.getSimpleName() + " host port");
			System.err.println("Using default port " + port + " on " + host);
		} else {
			host = args[0];
			port = Integer.parseInt(args[1]);
		}
		EventBus eventBus = new EventBus();
		Client clientConnection = new Client(host, port, eventBus);
		eventBus.register(clientConnection);
		clientConnection.addConnectionListener(appl);
		new Thread() {
			public void run() {
				try {
					clientConnection.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();

		// wait until connection is created
		while (channel == null) {
			synchronized (DemoApplication.class) {
				DemoApplication.class.wait(1000);
			}
		}

		UserServiceFactory.init(channel);

		appl.start();
	}

}
