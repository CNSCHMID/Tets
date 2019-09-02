package de.uol.swp.client;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.demo.IConnectionListener;
import de.uol.swp.client.user.MainPresenter;
import de.uol.swp.client.user.LoginPresenter;
import de.uol.swp.common.message.ExceptionMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserService;
import de.uol.swp.common.user.response.LoginSuccessfulMessage;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

// TODO: MVC

public class ClientApplication extends Application implements IConnectionListener {

	private static final Logger LOG = LogManager.getLogger(ClientApplication.class);

	private String host;
	private int port;

	private UserService userService;

	private Stage primaryStage;
	private Scene loginScene;
	private Scene lobbyScene;
	private User user;

	private Client clientConnection;

	private final EventBus eventBus = new EventBus();

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
			System.err.println("Usage: " + Client.class.getSimpleName() + " host port");
			System.err.println("Using default port " + port + " on " + host);
		} else {
			host = args.get(0);
			port = Integer.parseInt(args.get(1));
		}

		this.userService = new de.uol.swp.client.user.UserService(eventBus);
		// init views after userService is available
		initViews();

		// do not establish connection here
		// if connection is established in this stage, no GUI is shown and
		// exceptions are only visible in console!
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("SWP Demo Application");
		clientConnection = new Client(host, port, eventBus);
		clientConnection.addConnectionListener(this);
		// Register this class for events (e.g. for exceptions)
		eventBus.register(this);
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
		showLoginScreen();
	}

	private void initViews() {
		initLoginView();
		initLobbyView();
	}

	private Parent initPresenter(String fxmlFile) {
		Parent rootPane;
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
		try {
			rootPane = loader.load();
		} catch (IOException e) {
			throw new RuntimeException("Could not load View!");
		}
		AbstractPresenter presenter = loader.getController();
		presenter.setEventBus(eventBus);
		presenter.setUserService(userService);
		return rootPane;
	}

	private void initLobbyView() {
		if (lobbyScene == null) {
			Parent rootPane = initPresenter(MainPresenter.fxml);
			lobbyScene = new Scene(rootPane, 800, 600);
		}
	}

	private void initLoginView() {
		if (loginScene == null) {
			Parent rootPane = initPresenter(LoginPresenter.fxml);
			loginScene = new Scene(rootPane, 600, 200);
		}
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
		LOG.info("Client shutdown");
	}


	@Subscribe
	public void userLoggedIn(LoginSuccessfulMessage message) {
		LOG.debug("user logged in sucessfully "+message.getUser().getUsername());
		this.user = message.getUser();
		showLobbyScreen();
	}

	@Subscribe
	public void serverException(ExceptionMessage message){
		showServerError(message.getException());
	}

	@Subscribe
	private void handleEventBusError(DeadEvent deadEvent){
		LOG.error("DeadEvent detected "+deadEvent);
	}

	@Override
	public void exceptionOccured(String e) {
		showServerError(e);
	}



	// -----------------------------------------------------
	// JavFX Help methods
	// -----------------------------------------------------

	private void showServerError(String e) {
		Platform.runLater(() -> {
			Alert a = new Alert(Alert.AlertType.ERROR, "Server returned an error:\n" + e);
			a.showAndWait();
		});
	}

	private void showLoginErrorScreen() {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR, "Error logging in to server");
			alert.showAndWait();
			showLoginScreen();
		});
	}

	private void showLobbyScreen() {
		// Show lobby window
		Platform.runLater(() -> {
			primaryStage.setTitle("SWP Demo Application for " + user.getUsername());
			primaryStage.setScene(lobbyScene);
		});

	}

	private void showLoginScreen() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				primaryStage.setScene(loginScene);
				primaryStage.show();
			}
		});

	}

	public static void main(String[] args) {
		launch(args);
	}

}
