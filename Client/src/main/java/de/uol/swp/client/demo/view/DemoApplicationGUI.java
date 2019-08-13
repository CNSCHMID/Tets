package de.uol.swp.client.demo.view;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.communication.object.Client;
import de.uol.swp.client.demo.IConnectionListener;
import de.uol.swp.client.user.LoginPresenter;
import de.uol.swp.client.user.UserServiceFactory;
import de.uol.swp.common.message.ExceptionMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserService;
import de.uol.swp.common.user.dto.UserDTO;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.AllUsersResponse;
import de.uol.swp.common.user.response.LoginSuccessfulMessage;
import io.netty.channel.Channel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

// TODO: MVC

public class DemoApplicationGUI extends Application implements IConnectionListener {

	private static final Logger LOG = LogManager.getLogger(DemoApplicationGUI.class);

	private String host;
	private int port;

	private UserService userService;

	private Stage primaryStage;
	private Scene loginScene;
	private Scene lobbyScene;
	private ObservableList<String> users;
	private User user;

	Client clientConnection;

	final EventBus eventBus = new EventBus();

	// -----------------------------------------------------
	// Java FX Methods
	// ----------------------------------------------------

	@Override
	public void init() throws Exception {
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

		// do not establish connection here
		// if connection is established in this stage, no GUI is shown and
		// exceptions are only visible in console!
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("SWP Demo Application");
		clientConnection = new Client(host, port, eventBus);
		clientConnection.addConnectionListener(this);
		// Register this class for events (e.g. for exceptions)
		eventBus.register(this);
		// JavaFX Thread should not be blocked to long!
		Thread t = new Thread() {
			public void run() {
				try {
					clientConnection.start();
				} catch (Exception e) {
					exceptionOccured(e.getMessage());
				}
			};
		};
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void stop() throws Exception {
		if (userService != null && user != null) {
			userService.logout(user);
			user = null;
		}
		// Important: Close connection so connection thread can terminate
		// else client application will not stop
		System.err.println("Shutting down client ...");
		if (clientConnection != null) {
			clientConnection.close();
		}
		System.err.println("Shutting down client done");
	}

	// -----------------------------------------------------
	// UserDTO Management Events
	// -----------------------------------------------------

	@Subscribe
	public void userLoggedIn(LoginSuccessfulMessage message) {
		LOG.debug("user logged in sucessfully "+message.getUser().getUsername());
		this.user = message.getUser();
		showLobbyScreen();
	}

	@Subscribe
	public void newUser(UserLoggedInMessage message) {
		LOG.debug("New user "+message.getUsername()+" logged in");
		userService.retrieveAllUsers();
	}

	@Subscribe
	public void userLeft(UserLoggedOutMessage message) {
		LOG.debug("User "+message.getUsername()+" logged out");
		userService.retrieveAllUsers();
	}

	@Subscribe
	public void userList(AllUsersResponse allUsersResponse) {
		LOG.debug("Update of user list "+allUsersResponse.getUsers());
		updateUsersList(allUsersResponse.getUsers());
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

	@Override
	public void connectionEstablished(Channel ch) {
		UserServiceFactory.init(ch);
		// When connection is established, the user service is available
		this.userService = UserServiceFactory.getUserService();
		// register user service as listener to eventbus
		eventBus.register(userService);

		// TODO: Replace with better dependency injection with Google Guice --> later
		LoginPresenter.setUserService(userService);

		showLoginScreen();
	}

	// -----------------------------------------------------
	// JavFX Help methods
	// -----------------------------------------------------

	public void showServerError(String e) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert a = new Alert(Alert.AlertType.ERROR, "Server returned an error:\n" + e);
				a.showAndWait();
			}
		});
	}

	private void showLoginErrorScreen() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Error logging in to server");
				alert.showAndWait();
				showLoginScreen();
			}
		});
	}

	private void showLobbyScreen() {
		// Show lobby window
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				primaryStage.setTitle("SWP Demo Application for " + user);
				if (lobbyScene == null) {
					GridPane rootPane = new GridPane();
					lobbyScene = new Scene(rootPane, 800, 600);
					users = FXCollections.observableArrayList();
					ListView<String> usersView = new ListView<String>(users);
					rootPane.add(usersView, 1, 1);
				}
				primaryStage.setScene(lobbyScene);
			}
		});

	}

	private void showLoginScreen() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				if (loginScene == null) {
					Parent rootPane;
					try {
						rootPane = FXMLLoader.load(getClass().getResource(LoginPresenter.fxml));

					} catch (IOException e) {
						throw new RuntimeException("Could not load LoginView!");
					}

					loginScene = new Scene(rootPane, 600, 200);
				}

				primaryStage.setScene(loginScene);
				primaryStage.show();
			}
		});

	}

	private void updateUsersList(List<UserDTO> userList) {
		// Attention: This must be done on the FX Thread!
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				users.clear();
				userList.forEach(u -> users.add(u.getUsername()));
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

}
