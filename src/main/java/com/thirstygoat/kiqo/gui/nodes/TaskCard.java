package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.gui.scrumBoard.TaskCardExpandedView;
import com.thirstygoat.kiqo.gui.scrumBoard.TaskCardViewModel;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Task;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;


/**
 * Created by james on 14/08/15.
 */
public class TaskCard extends VBox implements FxmlView<TaskCardViewModel> {
    final private StringProperty shortNameProperty;
    final private FloatProperty hoursProperty;
    final private FloatProperty spentEffortProperty;
    final private BooleanProperty impedanceProperty;
    final private BooleanProperty isBlockedProperty;
    final private Task task;
    final private ObjectProperty<Organisation> organisationProperty;

    public TaskCard(Task task, Organisation organisation) {
        this.task = task;
        organisationProperty = new SimpleObjectProperty<>();
        organisationProperty.setValue(organisation);
        shortNameProperty = new SimpleStringProperty("");
        hoursProperty = new SimpleFloatProperty();
        spentEffortProperty = new SimpleFloatProperty();
        impedanceProperty = new SimpleBooleanProperty(false);
        isBlockedProperty = new SimpleBooleanProperty();
        isBlockedProperty.bindBidirectional(task.blockedProperty());
        draw();
        shortNameProperty().bind(task.shortNameProperty());
        hoursProperty().bind(task.estimateProperty());
        spentEffortProperty.bind(task.spentEffortProperty());
        getStyleClass().add("task-card");
    }

    /**
     * Sets up the view for the expanded task card.
     */
    private void draw() {
        GridPane gridPane = new GridPane();
        BorderPane borderPane = new BorderPane();
        Label shortNameLabel = new Label();

        Text effortText = new Text();
        Text divider = new Text("/");
        Text estimateText = new Text();
        TextFlow timeTextFlow = new TextFlow(effortText, divider, estimateText);


        effortText.getStyleClass().add("task-card-label");
        divider.getStyleClass().add("task-card-label");
        estimateText.getStyleClass().add("task-card-label");

        HBox iconBox = new HBox();

        iconBox.setAlignment(Pos.BOTTOM_RIGHT);

        timeTextFlow.getStyleClass().add("task-card-minimised-hours");

        shortNameLabel.getStylesheets().add("css/styles.css");
        shortNameLabel.getStyleClass().add("task-card-minimised-shortName");
        shortNameLabel.setWrapText(true);
        shortNameLabel.setMaxWidth(130);

        shortNameLabel.textProperty().bind(shortNameProperty);
        estimateText.textProperty().bind(hoursProperty.asString());
        effortText.textProperty().bind(spentEffortProperty.asString());

        Insets mainInset = new Insets(5, 5, 5, 5);
        Insets shortNameInset = new Insets(10, 0, 0, 0);
        Insets hourInset = new Insets(0, 0, 0, 0);

        shortNameLabel.setPadding(shortNameInset);
        timeTextFlow.setPadding(hourInset);

        FontAwesomeIconView impedanceIcon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
        impedanceIcon.setSize("15px");
        impedanceIcon.getStyleClass().add("task-impedance-icon");

        // open the expanded card
        setOnMouseClicked(event -> newExpandedCard());

        impedanceIcon.visibleProperty().bind(Bindings.isNotEmpty(task.getImpediments()));

        iconBox.getChildren().add(impedanceIcon);

        ColumnConstraints columnConstraints = new ColumnConstraints(10, 100, 100);
        ColumnConstraints columnConstraints2 = new ColumnConstraints(10, 25, 100);
        columnConstraints.setHgrow(Priority.SOMETIMES);
        columnConstraints.setHalignment(HPos.RIGHT);
        columnConstraints2.setHgrow(Priority.SOMETIMES);
        columnConstraints2.setHalignment(HPos.RIGHT);

        RowConstraints rowConstraints = new RowConstraints(10);
        rowConstraints.setVgrow(Priority.SOMETIMES);

        gridPane.add(timeTextFlow, 0, 0, 2, 1);
        gridPane.add(shortNameLabel, 0, 1, 2, 1);
        gridPane.getColumnConstraints().addAll(columnConstraints, columnConstraints2);
        gridPane.getRowConstraints().add(rowConstraints);

        setPrefHeight(USE_COMPUTED_SIZE);
        setMaxHeight(150);
        setPrefWidth(USE_COMPUTED_SIZE);
        setMaxWidth(150);

        borderPane.setPadding(mainInset);
        borderPane.setCenter(gridPane);
        borderPane.setBottom(iconBox);

        setVgrow(borderPane, Priority.ALWAYS);

        getChildren().add(borderPane);
    }

    /**
     * Display the expanded task card
     */
    private void newExpandedCard() {
        Platform.runLater(() -> {
            // probably should add one expanded task card to the scrumboard and load the task into it rather than independent tuples for each one
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            Window parentWindow = getParent().getScene().getWindow();
            stage.initOwner(parentWindow);
            ViewTuple<TaskCardExpandedView, TaskCardViewModel> viewTuple = FluentViewLoader.fxmlView(TaskCardExpandedView.class).load();
            viewTuple.getViewModel().load(task, organisationProperty.get());
            viewTuple.getViewModel().setStage(stage);
            Parent view = viewTuple.getView();
            Scene scene = new Scene(view);
            stage.setScene(scene);

            double parentWidth = parentWindow.getWidth();
            double parentHeight = parentWindow.getHeight();
            double parentX = parentWindow.getX();
            double parentY = parentWindow.getY();

            double stageWidth = 400;
            double stageHeight = 400;

            stage.setX(parentX + parentWidth / 2 - stageWidth / 2);
            stage.setY(parentY + parentHeight / 2 - stageHeight / 2);
            stage.show();

            parentWindow.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue) {
                    Platform.runLater(stage::close);
                }
            });
        });
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

    public BooleanProperty isBlockedProperty() {
        return isBlockedProperty;
    }

    public Task getTask() {
        return task;
    }
}
