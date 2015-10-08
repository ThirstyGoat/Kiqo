package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.*;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;

/**
 * Created by sam on 14/08/15.
 *
 * A custom node used to display data in the form of a linear pie chart / stacked bar graph
 */
public class StoryCompletenessVisualiser extends HBox  {

    private ObservableList<Task> todoTasks = FXCollections.observableArrayList();
    private ObservableList<Task> inProgressTasks = FXCollections.observableArrayList();
    private ObservableList<Task> verifyTasks = FXCollections.observableArrayList();
    private ObservableList<Task> doneTasks = FXCollections.observableArrayList();

    private FloatProperty totalTodoEstimate = new SimpleFloatProperty();
    private FloatProperty totalInProgressTasksEstimate = new SimpleFloatProperty();
    private FloatProperty totalVerifyTasksEstimate = new SimpleFloatProperty();
    private FloatProperty totalDoneTasksEstimate = new SimpleFloatProperty();

    private HBox notStarted = new HBox();
    private HBox inProgress = new HBox();
    private HBox verify = new HBox();
    private HBox done = new HBox();

    public StoryCompletenessVisualiser() {
        setColours();
        setFillHeight(true);
        getChildren().addAll(notStarted, inProgress, verify, done);

        widthProperty().addListener((observable, oldValue, newValue) -> {
            calculateSizes();
        });

        ListChangeListener<Task> listener = c -> {
            totalTodoEstimate.set(calculateTotalEstimate(todoTasks));
            totalInProgressTasksEstimate.set(calculateTotalEstimate(inProgressTasks));
            totalVerifyTasksEstimate.set(calculateTotalEstimate(verifyTasks));
            totalDoneTasksEstimate.set(calculateTotalEstimate(doneTasks));
            calculateSizes();
        };

        todoTasks.addListener(listener);
        inProgressTasks.addListener(listener);
        verifyTasks.addListener(listener);
        doneTasks.addListener(listener);
    }

    private void setColours() {
        setStyle("-fx-background-color: #bbb; -fx-text-fill: #fff"); // Setting the default background
        notStarted.getStyleClass().add(Status.NOT_STARTED.getCssClass());
        inProgress.getStyleClass().add(Status.IN_PROGRESS.getCssClass());
        verify.getStyleClass().add(Status.VERIFY.getCssClass());
        done.getStyleClass().add(Status.DONE.getCssClass());
    }

    private void calculateSizes() {
        float widthPerEstimate = calculateSizePerEstimate();
        notStarted.setPrefWidth(widthPerEstimate * totalTodoEstimate.get());
        inProgress.setPrefWidth(widthPerEstimate * totalInProgressTasksEstimate.get());
        verify.setPrefWidth(widthPerEstimate * totalVerifyTasksEstimate.get());
        done.setPrefWidth(widthPerEstimate * totalDoneTasksEstimate.get());



        if (widthPerEstimate == 0.0 && !doneTasks.isEmpty()) {
            done.setPrefWidth(getWidth());
        }
    }

    private float calculateSizePerEstimate() {
        float totalEstimates = totalTodoEstimate.get()
                + totalInProgressTasksEstimate.get()
                + totalVerifyTasksEstimate.get()
                + totalDoneTasksEstimate.get();

        if (totalEstimates == 0) {
            return 0.0f;
        }
        return (float) (getWidth() / totalEstimates);
    }

    /**
     * Provides observable list of tasks used for calculating the size of the 'to-do' section of the visualiser
     * @param tasks
     */
    public void setTodoTasks(ObservableList<Task> tasks) {
        todoTasks.setAll(tasks);
    }

    /**
     * Provides observable list of tasks used for calculating the size of the 'in progress' section of the visualiser
     * @param tasks
     */
    public void setInProgressTasks(ObservableList<Task> tasks) {
        inProgressTasks.setAll(tasks);
    }

    /**
     * Provides observable list of tasks used for calculating the size of the 'verify' section of the visualiser
     * @param tasks
     */
    public void setVerifyTasks(ObservableList<Task> tasks) {
        verifyTasks.setAll(tasks);
    }

    /**
     * Provides observable list of tasks used for calculating the size of the 'done' section of the visualiser
     * @param tasks
     */
    public void setDoneTasks(ObservableList<Task> tasks) {
        doneTasks.setAll(tasks);
    }

    private float calculateTotalEstimate(ObservableList<Task> tasks) {
        float sum = 0;
        for (Task task : tasks) {
            sum += task.getEstimate();
        }
        return sum;
    }
}