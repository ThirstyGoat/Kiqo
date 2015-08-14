package com.thirstygoat.kiqo.gui.nodes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Simple dialog class that enables custom button labels.
 */
public final class GoatDialog {
    private static Stage stage;
    private static Label header;
    private static Label message;
    private static HBox hBox;
    private static VBox vBox;

    /**
     * Creates a dialog box and returns the name of the button the user clicked on.
     *
     * @param owner Stage that owns this dialog
     * @param title Title to appear for the window
     * @param headerText Header text shown in the dialog (Larger than message)
     * @param messageText Standard message text to be shown in the dialog
     * @param buttons String array of button labels
     * @return Button name that was clicked on
     */
    public static String createBasicButtonDialog(final Stage owner, final String title, final String headerText,
                                                 final String messageText, final String[] buttons) {
        final String[] selectedProperty = new String[1];
        selectedProperty[0] = "-1";
        setup(owner);
        GoatDialog.header.setText(headerText);
        GoatDialog.message.setText(messageText);

        final int buttonSpacing = 10;
        int i = 0;
        for (final String button : buttons) {
            final Button newButton = new Button(button);
            if (i == 0) {
                newButton.setDefaultButton(true);
            }
            GoatDialog.hBox.getChildren().add(newButton);
            HBox.setMargin(newButton, new Insets(0, 0, 0, buttonSpacing));

            newButton.setOnAction(e -> {
                selectedProperty[0] = button;
                GoatDialog.stage.close();
            });
            i++;
        }

        GoatDialog.stage.setTitle(title);
        GoatDialog.stage.showAndWait();

        return selectedProperty[0];
    }

    /**
     * Creates a dialog box and returns the name of the button the user clicked on.
     *
     * @param owner Stage that owns this dialog
     * @param title Title to appear for the window
     * @param headerText Header text shown in the dialog (Larger than message)
     * @param customNode Custom node to be displayed in the
     * @param buttons String array of button labels
     * @return Button name that was clicked on
     */
    public static String createCustomNodeDialog(final Stage owner, final String title, final String headerText,
                                                 final Node customNode, final String[] buttons) {
        final String[] selectedProperty = new String[1];
        selectedProperty[0] = "-1";
        setup(owner);
        GoatDialog.header.setText(headerText);
        GoatDialog.message.setVisible(false);
        GoatDialog.message.setManaged(false);

        VBox.setMargin(customNode, new Insets(10, 0, 10, 0));

        vBox.getChildren().add(customNode);

        final int buttonSpacing = 10;
        int i = 0;
        for (final String button : buttons) {
            final Button newButton = new Button(button);
            if (i == 0) {
                newButton.setDefaultButton(true);
            }
            GoatDialog.hBox.getChildren().add(newButton);
            HBox.setMargin(newButton, new Insets(0, 0, 0, buttonSpacing));

            newButton.setOnAction(e -> {
                selectedProperty[0] = button;
                GoatDialog.stage.close();
            });
            i++;
        }

        GoatDialog.stage.setTitle(title);
        GoatDialog.stage.showAndWait();

        return selectedProperty[0];
    }

    /**
     * Sets up the structure and properties of nodes in the window.
     * @param owner Stage that owns this dialog
     */
    public static void setup(final Stage owner) {
        final int fontSize = 16;
        final Insets vBoxPadding = new Insets(15, 20, 5, 20);
        final Insets vBoxMargin = new Insets(10, 0, 10, 0);
        final Insets hBoxPadding = new Insets(0, 20, 0, 20);
        final int prefRowHeight2 = 50;

        GoatDialog.stage = new Stage();
        StackPane stackPane = new StackPane();
        GridPane gridPane = new GridPane();
        GoatDialog.header = new Label();
        GoatDialog.message = new Label();
        vBox = new VBox();
        GoatDialog.hBox = new HBox();
        RowConstraints rowConstraints1 = new RowConstraints();
        RowConstraints rowConstraints2 = new RowConstraints();
        ColumnConstraints columnConstraint = new ColumnConstraints();

        GoatDialog.header.setFont(new Font("System Bold", fontSize));
        GoatDialog.message.setWrapText(true);
        vBox.setAlignment(Pos.CENTER_LEFT);
        vBox.getChildren().addAll(GoatDialog.header, GoatDialog.message);
        vBox.setPadding(vBoxPadding);
        VBox.setMargin(GoatDialog.message, vBoxMargin);
        GoatDialog.hBox.setAlignment(Pos.CENTER_RIGHT);
        GoatDialog.hBox.setStyle("-fx-background-color: #eee");
        GoatDialog.hBox.setPadding(hBoxPadding);
        rowConstraints2.setPrefHeight(prefRowHeight2);
        rowConstraints1.setVgrow(Priority.ALWAYS);
        rowConstraints2.setVgrow(Priority.ALWAYS);
        columnConstraint.setHgrow(Priority.ALWAYS);
        gridPane.getRowConstraints().addAll(rowConstraints1, rowConstraints2);
        gridPane.getColumnConstraints().addAll(columnConstraint);
        gridPane.add(vBox, 0, 0);
        gridPane.add(GoatDialog.hBox, 0, 1);
        stackPane.getChildren().add(gridPane);
        stackPane.setPrefWidth(500);
        GoatDialog.stage.setScene(new Scene(stackPane));
        GoatDialog.stage.setResizable(false);
        GoatDialog.stage.initStyle(StageStyle.DECORATED);
        GoatDialog.stage.initOwner(owner);
        GoatDialog.stage.initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * Display an alert dialog to the user.
     *
     * @param owner Stage that owns this dialog
     * @param title window title
     * @param headerText internal heading
     * @param message detail message
     */
    public static void showAlertDialog(final Stage owner, final String title, final String headerText,
                                       final String message) {
        setup(owner);
        GoatDialog.header.setText(headerText);
        GoatDialog.message.setText(message);

        final int buttonSpacing = 10;
        final Button newButton = new Button("OK");
        newButton.setDefaultButton(true);
        GoatDialog.hBox.getChildren().add(newButton);
        HBox.setMargin(newButton, new Insets(0, 0, 0, buttonSpacing));

        newButton.setOnAction(e -> GoatDialog.stage.close());

        GoatDialog.stage.setTitle(title);
        GoatDialog.stage.show();
    }
}