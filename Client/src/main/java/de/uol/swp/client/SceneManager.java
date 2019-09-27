package de.uol.swp.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.main.MainPresenter;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.register.event.RegistationErrorEvent;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

class SceneManager {

    final private Stage primaryStage;
    final private EventBus eventBus;
    final private UserService userService;
    private Scene loginScene;
    private Scene registrationScene;
    private Scene mainScene;
    private Scene lastScene = null;
    private Scene currentScene = null;

    private User currentUser;


    public SceneManager(Stage primaryStage, EventBus eventBus, UserService userService){
        this.primaryStage = primaryStage;
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.userService = userService;
        initViews();
    }

    private void initViews() {
        initLoginView();
        initMainView();
        initRegistrationView();
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

    private void initMainView() {
        if (mainScene == null) {
            Parent rootPane = initPresenter(MainPresenter.fxml);
            mainScene = new Scene(rootPane, 800, 600);
        }
    }

    private void initLoginView() {
        if (loginScene == null) {
            Parent rootPane = initPresenter(LoginPresenter.fxml);
            loginScene = new Scene(rootPane, 400, 200);
        }
    }

    private void initRegistrationView(){
        if (registrationScene == null){
            Parent rootPane = initPresenter(RegistrationPresenter.fxml);
            registrationScene = new Scene(rootPane, 400,200);
        }
    }

    @Subscribe
    public void onShowRegistrationViewEvent(ShowRegistrationViewEvent event){
        showRegistrationScreen();
    }

    @Subscribe
    public void onShowLoginViewEvent(ShowLoginViewEvent event){
        showLoginScreen();
    }

    @Subscribe
    public void onRegistrationCanceledEvent(RegistrationCanceledEvent event){
        showScene(lastScene);
    }

    @Subscribe
    public void onRegistrationErrorEvent(RegistationErrorEvent event){
        showError(event.getMessage());
    }

    public void showError(String message, String e) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, message + e);
            a.showAndWait();
        });
    }


    public void showServerError(String e) {
        showError("Server returned an error:\n" , e);
    }

    public void showError(String e) {
        showError("Error:\n" , e);
    }

    public void showLoginScreen() {
        showScene(loginScene);
    }

    public void showRegistrationScreen() {
        showScene(registrationScene);
    }

    private void showScene(final Scene scene){
        this.lastScene = currentScene;
        this.currentScene = scene;
        Platform.runLater(() -> {
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    public void showLoginErrorScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error logging in to server");
            alert.showAndWait();
            showLoginScreen();
        });
    }

    public void showMainScreen(User currentUser) {
        this.currentUser = currentUser;
        Platform.runLater(() -> {
            primaryStage.setTitle("Welcome "+currentUser.getUsername());
        });
        showScene(mainScene);
    }



}
