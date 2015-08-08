package com.thirstygoat.kiqo.gui.nodes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Created by Carina Blair on 8/08/2015.
 */
public class GoatDatePickerSkin extends SkinBase<Control> {

        private Label dateLabel;
        private DatePicker datePicker;

        private Button editButton;
        private Button doneButton;

        private HBox mainView;
        private HBox displayView;
        private HBox editView;

        /**
         * Constructor for all SkinBase instances.
         *
         * @param control The control for which this Skin should attach to.
         */
        protected GoatDatePickerSkin(Control control) {
            super(control);

            mainView = createMainView();
            getChildren().add(mainView);

            datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue && !doneButton.isFocused()) {
                    showDisplay();
                }
            });
        }

        private HBox createMainView() {

            displayView = new HBox();
            displayView.setVisible(true);

            dateLabel = new Label();

            datePicker = new DatePicker();

            editButton = new Button();
            FontAwesomeIconView pencilIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);
            pencilIcon.setStyle("-fx-fill: grey");
            editButton.setGraphic(pencilIcon);
            editButton.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-padding: 0px;" +
                            "-fx-animated: true;"
            );

            // Make the edit button fade on hover
            final FadeTransition fade = new FadeTransition(Duration.millis(400), editButton);
            fade.setAutoReverse(true);
            fade.setFromValue(0);
            fade.setToValue(1);
            editButton.visibleProperty().bind(displayView.hoverProperty());
            displayView.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    fade.setCycleCount(1);
                    fade.playFromStart();
                } else {
                    fade.setCycleCount(2);
                    fade.playFrom(Duration.millis(400));
                }
            });

            displayView.getChildren().add(dateLabel);
            displayView.getChildren().add(editButton);

            editView = new HBox();
            editView.setVisible(false);

            datePicker = new DatePicker();
            doneButton = new Button();

            FontAwesomeIconView doneIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
            doneIcon.setStyle("-fx-fill: green");

            doneButton.setGraphic(doneIcon);

            editView.getChildren().add(datePicker);
            editView.getChildren().add(doneButton);

            HBox hBox = new HBox();
            StackPane stackPane = new StackPane();

            stackPane.getChildren().add(displayView);
            stackPane.getChildren().add(editView);
            hBox.getChildren().add(stackPane);

            displayView.setSpacing(5);

            datePicker.maxWidthProperty().bind(hBox.widthProperty());

             return hBox;
        }

        public Button getEditButton() {
            return editButton;
        }

        public Button getDoneButton() {
            return doneButton;
        }

        public final Label getDateLabel() {
            return dateLabel;
        }


        public final DatePicker getDatePicker() {
            return datePicker;
        }

        public final HBox getEditView() {
            return editView;
        }

        public final HBox getDisplayView() {
            return displayView;
        }

        public void showEdit() {
            displayView.setVisible(false);
            editView.setVisible(true);
        }

        public void showDisplay() {
            displayView.setVisible(true);
            editView.setVisible(false);
        }

        public void setValidator() {

        }
}
