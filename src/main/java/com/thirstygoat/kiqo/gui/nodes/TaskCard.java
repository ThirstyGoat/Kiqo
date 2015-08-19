package com.thirstygoat.kiqo.gui.nodes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;


/**
 * Created by james on 14/08/15.
 */
public class TaskCard extends VBox {
    final private StringProperty shortNameProperty;
    final private FloatProperty hoursProperty;
    final private BooleanProperty impedanceProperty;

    public TaskCard() {
        shortNameProperty = new SimpleStringProperty("");
        hoursProperty = new SimpleFloatProperty();
        impedanceProperty = new SimpleBooleanProperty(false);
        draw();
    }

    public TaskCard(final String shortName, final float hours, final boolean impedance) {
        shortNameProperty = new SimpleStringProperty(shortName);
        hoursProperty = new SimpleFloatProperty(hours);
        impedanceProperty = new SimpleBooleanProperty(impedance);
        draw();
    }

    private void draw() {
        GridPane gridPane = new GridPane();
        BorderPane borderPane = new BorderPane();

        Label shortNameLabel = new Label();
        Label hourLabel = new Label();

        HBox iconBox = new HBox();

        iconBox.setAlignment(Pos.BOTTOM_RIGHT);

        hourLabel.getStylesheets().add("css/styles.css");
        hourLabel.getStyleClass().add("task-card-minimised-hours");

        shortNameLabel.getStylesheets().add("css/styles.css");
        shortNameLabel.getStyleClass().add("task-card-minimised-shortName");
        shortNameLabel.setWrapText(true);
        shortNameLabel.setMaxWidth(130);

        shortNameLabel.textProperty().bind(shortNameProperty);
        hourLabel.textProperty().bind(hoursProperty.asString());

        Insets mainInset = new Insets(10, 10, 10, 10);
        Insets shortNameInset = new Insets(15, 0, 0, 0);
        Insets hourInset = new Insets(0, 0, 0, 0);

        shortNameLabel.setPadding(shortNameInset);
        hourLabel.setPadding(hourInset);

        FontAwesomeIconView impedanceIcon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
        impedanceIcon.setSize("15px");
        impedanceIcon.setFill(Color.ORANGERED);

        impedanceIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //TODO add impedance event handling stuff - expanding or whatever
            }
        });

        impedanceIcon.visibleProperty().bind(impedanceProperty);

        iconBox.getChildren().add(impedanceIcon);

        ColumnConstraints columnConstraints = new ColumnConstraints(10, 100, 100);
        ColumnConstraints columnConstraints2 = new ColumnConstraints(10, 25, 100);
        columnConstraints.setHgrow(Priority.SOMETIMES);
        columnConstraints.setHalignment(HPos.RIGHT);
        columnConstraints2.setHgrow(Priority.SOMETIMES);
        columnConstraints2.setHalignment(HPos.RIGHT);

        RowConstraints rowConstraints = new RowConstraints(10);
        rowConstraints.setVgrow(Priority.SOMETIMES);

        gridPane.add(hourLabel, 0, 0, 2, 1);
        gridPane.add(shortNameLabel, 0, 1, 2, 1);
        gridPane.getColumnConstraints().addAll(columnConstraints, columnConstraints2);
        gridPane.getRowConstraints().add(rowConstraints);

        setPrefHeight(150);
        setMaxHeight(150);
        setPrefWidth(150);
        setMaxWidth(150);

        borderPane.setPadding(mainInset);
        borderPane.setCenter(gridPane);
        borderPane.setBottom(iconBox);

        setVgrow(borderPane, Priority.ALWAYS);

        getChildren().add(borderPane);
    }

    public StringProperty shortNameProperty() {
        return shortNameProperty;
    }

    public String getShortNameProperty() {
        return shortNameProperty.get();
    }

    public void setShortNameProperty(String shortName) {
        shortNameProperty.set(shortName);
    }

    public BooleanProperty impedanceProperty() {
        return  impedanceProperty;
    }

    public boolean getImpedanceProperty() {
        return impedanceProperty.get();
    }

    public void setImpedanceProperty(boolean impendance) {
        impedanceProperty.set(impendance);
    }

    public FloatProperty hoursProperty() {
        return hoursProperty;
    }

    public Float getHoursProperty() {
        return hoursProperty.get();
    }

    public void setHoursProperty(Float hours) {
        hoursProperty.set(hours);
    }

}
