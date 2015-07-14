package com.thirstygoat.kiqo.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leroy on 20/05/15.
 */
public class Backlog extends Item {
    private StringProperty shortName;
    private StringProperty longName;
    private StringProperty description;
    private ObjectProperty <Scale> scale;
    private ObjectProperty<Person> productOwner;
    private ObjectProperty<Project> project;
    private final ObservableList<Story> stories = FXCollections.observableArrayList();

    public Backlog() {
        this.shortName = new SimpleStringProperty("");
        this.longName = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.productOwner = new SimpleObjectProperty<>(null);
        this.project = new SimpleObjectProperty<>(null);
        this.scale = new SimpleObjectProperty<>(Scale.FIBONACCI);

    }

    public Backlog(String shortName, String longName, String description, Person productOwner,
                   Project project, List<Story> stories, Scale scale) {
        this.shortName = new SimpleStringProperty(shortName);
        this.longName = new SimpleStringProperty(longName);
        this.description = new SimpleStringProperty(description);
        this.productOwner = new SimpleObjectProperty<>(productOwner);
        this.project = new SimpleObjectProperty<>(project);
        this.stories.addAll(stories);
        this.scale = new SimpleObjectProperty<>(scale);
    }

    public void setScale(Scale scale) {
        this.scale.set(scale);
    }

    public Scale getScale() {
        return scale.get();
    }

    public List<Story> getStories() {
        List<Story> stories1 = new ArrayList<>();
        stories1.addAll(stories);
        return stories1;
    }

    public ObservableList<Story> observableStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories.clear();
        this.stories.addAll(stories);
    }

    /*
    * Enum for the stories scale
    * */
    public enum Scale {
        FIBONACCI("Fibonacci"),
        TSHIRTSIZE("T-Shirt Size"),
        DOGBREEDS("Dog Breeds"),;

        private String label;

        Scale(String label) {
            this.label = label;
        }

        /**
         * Converts the scale string to uppercase so it will match the enum that exists in order to
         * take the string value from the combo box used for setting scale.
         */
        public static Scale getEnum(String str) {
            return Scale.valueOf(str.toUpperCase());
        }

        /*
        * Necessary to easily fill the combo box
        * @return Arraylist filled with the labels for each enum
        */
        public static ArrayList<String> getStrings() {
            ArrayList<String> strs = new ArrayList<>();
            for (Scale scale : Scale.values()) {
                strs.add(scale.toString());
            }
            return strs;
        }

        public String toString() {
            return label;
        }
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public String getLongName() {
        return longName.get();
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Person getProductOwner() {
        return productOwner.get();
    }

    public ObjectProperty<Person> productOwnerProperty() {
        return productOwner;
    }

    public void setProductOwner(Person productOwner) {
        this.productOwner.set(productOwner);
    }

    public Project getProject() {
        return project.get();
    }

    public ObjectProperty<Project> projectPropert() {
        return project;
    }

    public void setProject(Project project) {
        this.project.set(project);
    }
}
