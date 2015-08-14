package com.thirstygoat.kiqo.gui.nodes.scrumboard;

import com.thirstygoat.kiqo.model.Story;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bradley on 14/08/15.
 */
public class Scrumboard extends VBox {

    // Maps story objects to Story Row objects
    Map<Story, Label> map = new HashMap<>();
    private ListProperty<Story> stories = new SimpleListProperty<>();
    private ObservableList<Label> storyRows = FXCollections.observableArrayList();

    public Scrumboard() {
        draw();
    }

    private void draw() {
        stories.forEach(this::addStoryRow);

        stories.addListener((ListChangeListener<Story>) c -> {
            c.next();
            c.getAddedSubList().forEach(this::addStoryRow);
            c.getRemoved().forEach(this::removeStoryRow);
        });

        Bindings.bindContent(getChildren(), storyRows);
    }

    private void addStoryRow(Story story) {
        Label label = new Label();
        label.textProperty().bind(story.shortNameProperty());
        map.put(story, label);
        storyRows.add(label);
    }

    private void removeStoryRow(Story story) {
        // Lookup the corresponding StoryRow that this Story relates to
        Label label = map.get(story);
        // Remove that StoryRow from the Scrumboard
        storyRows.remove(label);
        // Clean up after ourselves
        map.remove(story);
    }

    public javafx.collections.ObservableList<Story> getStories() {
        return stories.get();
    }

    public void setStories(javafx.collections.ObservableList<Story> stories) {
        this.stories.set(stories);
    }

    public ListProperty<Story> storiesProperty() {
        return stories;
    }
}
