package seng302.group4.viewModel;

import com.sun.xml.internal.bind.v2.TODO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller for the primary view
 */
public class MainController implements Initializable {
    private Stage primaryStage;
    private AnchorPane listAnchorPane;
    private double dividerPosition;

    // FXML Injections
    @FXML
    private CheckMenuItem listToggleCheckMenuItem;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private ListView mainListView;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private MenuItem newProjectMenuItem;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setQuitMenuItem();
        setListToggleCheckMenuItem();
        setLayoutProperties();
        setNewProjectMenuItem();
    }

    /**
     * Sets layout specific properties
     */
    private void setLayoutProperties() {
        listAnchorPane = (AnchorPane) mainSplitPane.getItems().get(0);

    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Sets the functionality for the quit menu item
     */
    private void setQuitMenuItem() {
        quitMenuItem.setOnAction(event -> {
            primaryStage.close();
        });
    }

    /**
     * Sets the functionality for the toggle list view menu item
     */
    private void setListToggleCheckMenuItem() {
        listToggleCheckMenuItem.setSelected(true);
        listToggleCheckMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // shows the list view
                mainSplitPane.getItems().add(0, listAnchorPane);
                mainSplitPane.setDividerPosition(0, dividerPosition);
            } else {
                // hides the list view
                dividerPosition = mainSplitPane.getDividerPositions()[0];
                mainSplitPane.getItems().remove(listAnchorPane);
            }

        });
    }

    private void setNewProjectMenuItem() {
        newProjectMenuItem.setOnAction(event -> {
            newProjectDialog();
        });
    }

    private void newProjectDialog() {
        Stage stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/newProject.fxml"));
        BorderPane root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        NewProjectController newProjectController = loader.getController();
        newProjectController.setStage(stage);
    }
}
