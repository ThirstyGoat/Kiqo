package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Status;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Created by bradley on 19/08/15.
 */
public class StoryRowViewModel implements Loadable<Story>, ViewModel {

    private final int MAX_DESCRIPTION_LENGTH = 100;

    private GoatModelWrapper<Story> storyWrapper = new GoatModelWrapper<>();
    private Organisation organisation;
    private MainController mainController;

    private StringProperty storyName = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private IntegerProperty priority = new SimpleIntegerProperty();
    private StringProperty estimate = new SimpleStringProperty();

    // Task buckets
    private ObservableList<Task> toDoTasks = FXCollections.observableArrayList();
    private ObservableList<Task> inProgressTasks = FXCollections.observableArrayList();
    private ObservableList<Task> verifyTasks = FXCollections.observableArrayList();
    private ObservableList<Task> doneTasks = FXCollections.observableArrayList();

    private ObjectProperty<Organisation> organisationProperty = new SimpleObjectProperty<>();

    @Override
    public void load(Story story, Organisation organisation) {
        storyWrapper.set(story);
        organisationProperty.setValue(organisation);
        this.organisation = organisation;
        storyNameProperty().bind(story.shortNameProperty());
        descriptionProperty().bind(story.descriptionProperty());
        priorityProperty().bind(story.priorityProperty());
        estimateProperty().bind(Bindings.createStringBinding(() -> {
            return story.getScale().getEstimates()[story.getEstimate() - 1];
        }, story.estimateProperty(), story.scaleProperty()));


        setTasks(story.getTasks());
        story.getTasks().addListener((ListChangeListener<Task>) c -> setTasks(story.getTasks()));
    }

    private void setTasks(ObservableList<Task> tasks) {
        toDoTasks.clear();
        inProgressTasks.clear();
        verifyTasks.clear();
        doneTasks.clear();
        tasks.forEach(task -> {
            if (task.getStatus() == Status.NOT_STARTED) {
                // Add to toDoTasks
                toDoTasks.add(task);
            } else if (task.getStatus() == Status.IN_PROGRESS) {
                // Add to inProgressTasks
                inProgressTasks.add(task);
            } else if (task.getStatus() == Status.VERIFY) {
                // Add to verifyTasks
                verifyTasks.add(task);
            } else if (task.getStatus() == Status.DONE) {
                // Add to doneTasks
                doneTasks.add(task);
            }
        });
    }

    public String getStoryName() {
        return storyName.get();
    }

    public void setStoryName(String storyName) {
        this.storyName.set(storyName);
    }

    public StringProperty storyNameProperty() {
        return storyName;
    }

    public ObservableList<Task> getToDoTasks() {
        return toDoTasks;
    }

    public ObservableList<Task> getInProgressTasks() {
        return inProgressTasks;
    }

    public ObservableList<Task> getVerifyTasks() {
        return verifyTasks;
    }

    public ObservableList<Task> getDoneTasks() {
        return doneTasks;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public float getPriority() {
        return priority.get();
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public String getEstimate() {
        return estimate.get();
    }

    public StringProperty estimateProperty() {
        return estimate;
    }

    public void newTask() {
        mainController.createTask(storyWrapper.get());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void showStoryInDetailsPane() {
        MainController.focusedItemProperty.setValue(storyWrapper.get());
    }

    public ObjectProperty<Organisation> organisationProperty() {
        return organisationProperty;
    }
}