package com.thirstygoat.kiqo.model;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;


/**
 * Created by james on 10/09/15.
 */
public class Impediment {
    private final StringProperty impediments;
    private final BooleanProperty resolved;

    public Impediment() {
        impediments = new SimpleStringProperty("");
        resolved = new SimpleBooleanProperty(false);
    }

    public Impediment(final String impediments, final Boolean resolved) {
        this();
        setImpediments(impediments);
        setResolved(resolved);
    }

    public static Callback<Impediment, Observable[]> getWatchStrategy() {
        return s -> new Observable[] {s.impedimentsProperty(), s.resolvedProperty()};
    }

    public String getImpediments() {
        return impediments.get();
    }

    public void setImpediments(String impediments) {
        this.impediments.set(impediments);
    }

    public StringProperty impedimentsProperty() {
        return impediments;
    }

    public boolean getResolved() {
        return resolved.get();
    }

    public void setResolved(boolean resolved) {
        this.resolved.set(resolved);
    }

    public BooleanProperty resolvedProperty() {
        return resolved;
    }
}
