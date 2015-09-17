package com.thirstygoat.kiqo.gui.menuBar;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.*;
import javafx.scene.control.Label;
import javafx.scene.image.*;

public class AboutController implements Initializable {
    private Runnable closeAction;

    @FXML
    Label productLabel;
    @FXML
    Label companyLabel;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        productLabel.setText("Kiqo: A better Agilefant\n"
                + "Project management for the real world\n");
        
        companyLabel.setText("Created by Thirsty Goat (SENG302 2015 Team 4)\n"
                + "\tCarina Blair\n"
                + "\tJames Harrison\n"
                + "\tLeroy Hopson\n"
                + "\tBradley Kirwan\n"
                + "\tAmy Martin\n"
                + "\tSam Schofield");
        Image image = new Image(getClass().getClassLoader().getResourceAsStream("images/thirstyGoatLogo.png"), 200, 200, true, true);
        companyLabel.setGraphic(new ImageView(image));
    }

    public void close() {
        closeAction.run();
    }

    public void setCloseAction(Runnable closeAction) {
        this.closeAction = closeAction;
    }
}
