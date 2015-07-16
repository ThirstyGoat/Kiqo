package com.thirstygoat.kiqo.nodes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.thirstygoat.kiqo.model.Item;

/**
 * A class so we can have nice headings in our tree view
 */
public class TreeNodeHeading extends Item {

    public String shortName;

    public TreeNodeHeading(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public StringProperty shortNameProperty() {
        return new SimpleStringProperty(shortName);
    }
}