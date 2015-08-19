package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Story;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by bradley on 19/08/15.
 */
public class StoryRowViewModel implements Loadable<Story>, ViewModel {

    private StringProperty storyName = new SimpleStringProperty();

    @Override
    public void load(Story story, Organisation organisation) {
        storyNameProperty().bind(story.shortNameProperty());
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
}