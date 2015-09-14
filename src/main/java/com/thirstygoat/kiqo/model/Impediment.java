package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableField;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by james on 10/09/15.
 */
public class Impediment implements Searchable {
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
        return s -> new Observable[]{s.impedimentsProperty(), s.resolvedProperty()};
    }

    public String getImpediment() {
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

    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchString = new ArrayList<>();
        searchString.add(new SearchableField("impediments", impediments.toString()));
        return searchString;
    }
}
