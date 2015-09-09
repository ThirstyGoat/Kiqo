package com.thirstygoat.kiqo.gui.detailsPane;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bradley on 10/09/15.
 */
public class DetailsPaneInfoController implements Initializable {
    @FXML
    private Label toolbarShortcutLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (com.sun.javafx.PlatformUtil.isMac()) {
            toolbarShortcutLabel.setText("\u2318 + /");
        }
    }
}
