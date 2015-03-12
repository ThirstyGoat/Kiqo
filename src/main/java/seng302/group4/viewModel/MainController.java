package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller
 */
public class MainController implements Initializable {
    private Stage primaryStage;
    @FXML
    private CheckMenuItem listToggleCheckMenuItem;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private ListView mainListView;
    @FXML
    private SplitPane mainSplitPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setQuitMenuItem();
        setListToggleCheckMenuItem();

    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void setQuitMenuItem() {

    }

    private void setListToggleCheckMenuItem() {
        listToggleCheckMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("asdasd");
            if (newValue) {
                mainSplitPane.setDividerPosition(0, 0.25);
            } else {
                mainSplitPane.setDividerPosition(0, 0);
            }
        });
    }
}
