package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.SearchableField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leroy on 15/05/15.
 */
public class Story extends Item {
    public static final int DEFAULT_PRIORITY = 0;
    public static final int MAX_PRIORITY = 1000;
    public static final int MIN_PRIORITY = -1000;

    private final StringProperty shortName;
    private final StringProperty longName;
    private final StringProperty description;
    private final ObjectProperty<Person> creator;
    private final ObjectProperty<Project> project;
    private final ObjectProperty<Backlog> backlog;
    private final IntegerProperty priority;
    private final IntegerProperty estimate;
    private final ObjectProperty<Scale> scale;
    private final ObservableList<AcceptanceCriteria> acceptanceCriteria;
    private final ObservableList<Story> dependencies;
    private final BooleanProperty isReady;
    private final ObservableList<Task> tasks;
    private final FloatProperty taskHours;

    /**
     * no-arg constructor for JavaBeans compliance
     */
    public Story() {
        this.shortName = new SimpleStringProperty("");
        this.longName = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.creator = new SimpleObjectProperty<>(null);
        this.project = new SimpleObjectProperty<>(null);
        this.backlog = new SimpleObjectProperty<>(null);
        this.priority = new SimpleIntegerProperty(DEFAULT_PRIORITY);
        this.acceptanceCriteria = FXCollections.observableArrayList(AcceptanceCriteria.getWatchStrategy());
        this.isReady = new SimpleBooleanProperty(false);
        this.estimate = new SimpleIntegerProperty(0);
        this.dependencies = FXCollections.observableArrayList();
        this.scale = new SimpleObjectProperty<>(Scale.FIBONACCI);
        this.tasks = FXCollections.observableArrayList(Task.getWatchStrategy());
        this.taskHours = new SimpleFloatProperty(0.0f);
//        setTasksListener();
    }

    public Story(String shortName, String longName, String description, Person creator, Project project,
                 Backlog backlog, Integer priority, Scale scale, Integer estimate, boolean isReady) {
        this.shortName = new SimpleStringProperty(shortName);
        this.longName = new SimpleStringProperty(longName);
        this.description = new SimpleStringProperty(description);
        this.creator = new SimpleObjectProperty<>(creator);
        this.project = new SimpleObjectProperty<>(project);
        this.backlog = new SimpleObjectProperty<>(backlog);
        this.priority = new SimpleIntegerProperty(priority);
        this.acceptanceCriteria = FXCollections.observableArrayList(AcceptanceCriteria.getWatchStrategy());
        this.isReady = new SimpleBooleanProperty(isReady);
        this.estimate = new SimpleIntegerProperty(estimate);
        this.scale = new SimpleObjectProperty<>(scale);
        this.dependencies = FXCollections.observableArrayList();
        this.dependencies.addAll(dependencies);
        this.tasks = FXCollections.observableArrayList(Task.getWatchStrategy());
        this.taskHours = new SimpleFloatProperty();
//        setTasksListener();
    }

    /**
     * @return a string array of the searchable fields for a model object
     */
    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchString = new ArrayList<>();
        searchString.addAll(Arrays.asList(new SearchableField("Short Name", getShortName()), new SearchableField("Long Name", getLongName()),
                new SearchableField("Description", getDescription())));
        return searchString;
    }

    /**
     * binds the taskHours property to the sum of estimates for each task for the story
     */
    private void setTasksListener() {
        taskHours.bind(Bindings.createFloatBinding(() -> {
            float totalVal = 0;
            for (Task task : tasks) {
                totalVal += task.getEstimate();
            }
            return totalVal;
        }, tasks));
    }

    public ObservableList<Task> observableTasks() {
        return tasks;
    }

    public FloatProperty taskHoursProperty() {
        return taskHours;
    }

    public Float getTaskHours() {
        return taskHours.get();
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    public String getLongName() {
        return longName.get();
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public Person getCreator() {
        return creator.get();
    }

    public void setCreator(Person creator) {
        this.creator.set(creator);
    }

    public ObjectProperty<Person> creatorProperty() {
        return creator;
    }

    public ObjectProperty<Project> projectProperty() { 
        return project; 
    }

    public Project getProject() { 
        return project.get(); 
    }

    public void setProject(Project project) {
        this.project.set(project);
    }

    public ObjectProperty<Backlog> backlogProperty() {
        return backlog;
    }

    public Backlog getBacklog() {
        return backlog.get();
    }

    public void setBacklog(Backlog backlog) {
        this.backlog.set(backlog);
    }

    public int getPriority() { 
        return priority.get();
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public ObservableList<AcceptanceCriteria> getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }



    public Integer getEstimate() {
        return estimate.get();
    }

    public void setEstimate(Integer estimate) {
        this.estimate.set(estimate);
    }

    public IntegerProperty estimateProperty() {
        return estimate;
    }

    public Scale getScale() {
        return scale.get();
    }

    public void setScale(Scale scale) {
        this.scale.setValue(scale);
    }

    public ObjectProperty<Scale> scaleProperty() {
        return scale;
    }

    public boolean getIsReady() { 
        return isReady.get(); 
    }

    public void setIsReady(boolean isReady) { 
        this.isReady.set(isReady); 
    }

    public List<Story> getDependencies() {
        List<Story> list = new ArrayList<>();
        list.addAll(this.dependencies);
        return list;
    }
    
    public void setDependencies(List<Story> dependencies) {
        this.dependencies.clear();
        this.dependencies.addAll(dependencies);
    }
    
    public ObservableList<Story> observableDependencies() {
        return this.dependencies;
    }
    
    public BooleanProperty isReadyProperty() { 
        return isReady; 
    }
}
