package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.scrumBoard.TaskCardExpandedView;
import com.thirstygoat.kiqo.gui.scrumBoard.TaskCardViewModel;
import com.thirstygoat.kiqo.gui.sprint.StoryRowViewModel;
import com.thirstygoat.kiqo.model.Task;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by james on 14/08/15.
 */
public class TaskCard extends VBox implements FxmlView<TaskCardViewModel> {
    final private StringProperty shortNameProperty;
    final private FloatProperty hoursProperty;
    final private BooleanProperty impedanceProperty;
    final private Task task;

    public TaskCard(Task task) {
        shortNameProperty = new SimpleStringProperty("");
        hoursProperty = new SimpleFloatProperty();
        impedanceProperty = new SimpleBooleanProperty(false);
        draw();
        this.task = task;
        shortNameProperty().bind(task.shortNameProperty());
        hoursProperty().bind(task.estimateProperty());
        getStyleClass().add("task-card");
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

        Insets mainInset = new Insets(5, 5, 5, 5);
        Insets shortNameInset = new Insets(10, 0, 0, 0);
        Insets hourInset = new Insets(0, 0, 0, 0);

        shortNameLabel.setPadding(shortNameInset);
        hourLabel.setPadding(hourInset);

        FontAwesomeIconView impedanceIcon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE);
        impedanceIcon.setSize("15px");
        impedanceIcon.getStyleClass().add("task-impedance-icon");

        impedanceIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                newExpandedCard();
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

    private void newExpandedCard() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            ViewTuple<TaskCardExpandedView, TaskCardViewModel> viewTuple = FluentViewLoader.fxmlView(TaskCardExpandedView.class).load();
            viewTuple.getViewModel().load(task);
            viewTuple.getViewModel().setStage(stage);
            Parent view = viewTuple.getView();


            stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue) {
                    stage.close();
                }
            });

            Scene scene = new Scene(view);

//            view.setScaleX(0);
//            view.setScaleY(0);
            stage.setScene(scene);
//            stage.setWidth(0.1);
//            stage.setHeight(0.1);
//            stage.setOpacity(0.5);
//
//            Timeline timeline = new Timeline();
//            Duration transitionTime = Duration.millis(500);
////            timeline.setRate(3);
//
//            KeyValue kv_width = new KeyValue(stage.minWidthProperty(), 400);
//            KeyFrame kf_width = new KeyFrame(transitionTime, kv_width);
//            KeyValue kv_height = new KeyValue(stage.minHeightProperty(), 400);
//            KeyFrame kf_height = new KeyFrame(transitionTime, kv_height);
//
//
//            KeyValue kv_scale_width = new KeyValue(view.scaleXProperty(), 1);
//            KeyFrame kf_scale_width = new KeyFrame(transitionTime, kv_scale_width);
//            KeyValue kv_scale_height = new KeyValue(view.scaleYProperty(), 1);
//            KeyFrame kf_scale_height = new KeyFrame(transitionTime, kv_scale_height);
//
//
//            KeyValue kv_stage_opacity = new KeyValue(stage.opacityProperty(), 1);
//            KeyFrame kf_stage_opacity = new KeyFrame(transitionTime.multiply(1.75), kv_stage_opacity);
//
//            Stage primaryStage = MainController.getPrimaryStage();
//
//            stage.widthProperty().addListener((observable, oldValue, newValue) -> {
//                stage.setX(primaryStage.getX() + (primaryStage.getWidth() / 2) - (newValue.doubleValue() / 2));
//            });
//
//            stage.heightProperty().addListener((observable, oldValue, newValue) -> {
//                stage.setY(primaryStage.getY() + (primaryStage.getHeight() / 2) - (stage.getHeight() / 2));
//            });
//
//            timeline.getKeyFrames().addAll(kf_width, kf_height, kf_scale_width, kf_scale_height, kf_stage_opacity);
//            timeline.play();

            stage.show();
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

    public Task getTask() {
        return task;
    }
}
