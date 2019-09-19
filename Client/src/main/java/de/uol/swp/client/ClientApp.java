package de.uol.swp.client;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.uol.swp.client.di.ClientModule;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserService;
import de.uol.swp.common.user.exception.RegistrationExceptionMessage;
import de.uol.swp.common.user.response.LoginSuccessfulMessage;
import de.uol.swp.common.user.response.RegistrationSuccessfulEvent;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ClientApp extends Application implements ConnectionListener {

	private static final Logger LOG = LogManager.getLogger(ClientApp.class);

	private String host;
	private int port;

	private UserService userService;

	private User user;

	private ClientConnection clientConnection;

	private EventBus eventBus;

	private SceneManager sceneManager;

	// -----------------------------------------------------
	// Java FX Methods
	// ----------------------------------------------------

	@Override
	public void init() {
		Parameters p = getParameters();
		List<String> args = p.getRaw();

		if (args.size() != 2) {
			host = "localhost";
			port = 8889;
			System.err.println("Usage: " + ClientConnection.class.getSimpleName() + " host port");
			System.err.println("Using default port " + port + " on " + host);
		} else {
			host = args.get(0);
			port = Integer.parseInt(args.get(1));
		}

		// do not establish connection here
		// if connection is established in this stage, no GUI is shown and
		// exceptions are only visible in console!
	}


	@Override
	public void start(Stage primaryStage) {

        // Client app is created by java, so injection must
        // be handled here manually
		Injector injector = Guice.createInjector(new ClientModule());

        // get user service from guice, is needed for logout
        this.userService = injector.getInstance(UserService.class);

        // get event bus from guice
		eventBus = injector.getInstance(EventBus.class);
		// Register this class for de.uol.swp.client.events (e.g. for exceptions)
		eventBus.register(this);

		// Client app is created by java, so injection must
		// be handled here manually
		SceneManagerFactory sceneManagerFactory = injector.getInstance(SceneManagerFactory.class);
		this.sceneManager = sceneManagerFactory.create(primaryStage);

		ClientConnectionFactory connectionFactory = injector.getInstance(ClientConnectionFactory.class);
		clientConnection = connectionFactory.create(host, port);
		clientConnection.addConnectionListener(this);
		// JavaFX Thread should not be blocked to long!
		Thread t = new Thread(() -> {
			try {
				clientConnection.start();
			} catch (Exception e) {
				exceptionOccured(e.getMessage());
			}
		});
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void connectionEstablished(Channel ch) {
		sceneManager.showLoginScreen();
	}



	@Override
	public void stop() {
		if (userService != null && user != null) {
			userService.logout(user);
			user = null;
		}
		// Important: Close connection so connection thread can terminate
		// else client application will not stop
		LOG.trace("Trying to shutting down client ...");
		if (clientConnection != null) {
			clientConnection.close();
		}
		LOG.info("ClientConnection shutdown");
	}

	//
	@Subscribe
	public void userLoggedIn(LoginSuccessfulMessage message) {
		LOG.debug("user logged in sucessfully "+message.getUser().getUsername());
		this.user = message.getUser();
		sceneManager.showMainScreen(user);
	}

	@Subscribe
	public void onRegistrationExceptionMessage(RegistrationExceptionMessage message){
		sceneManager.showServerError("Registation error "+message);
		LOG.error("Registation error "+message);
	}

	@Subscribe
	public void onRegistrationSuccessfulMessage(RegistrationSuccessfulEvent message){
		LOG.info("Registration successful.");
		sceneManager.showLoginScreen();
	}

	@Subscribe
	private void handleEventBusError(DeadEvent deadEvent){
		LOG.error("DeadEvent detected "+deadEvent);
	}

	@Override
	public void exceptionOccured(String e) {
		sceneManager.showServerError(e);
	}



	// -----------------------------------------------------
	// JavFX Help methods
	// -----------------------------------------------------


	public static void main(String[] args) {
		launch(args);
	}

}
