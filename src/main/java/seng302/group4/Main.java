package seng302.group4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import seng302.group4.viewModel.MainController;

/**
 * Main entry point for application
 */
public class Main extends Application {
    private Stage primaryStage;
    private BorderPane root;
    private MainController mainController;

    public static void main( String[] args ) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getClassLoader().getResource("main.fxml"));
        root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);
    }
}
