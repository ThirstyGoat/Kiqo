package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableField;
import com.thirstygoat.kiqo.util.BoundPropertySupport;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;

import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leroy on 17/09/15.
 */
public class Effort implements BoundProperties, Searchable {
    private final ObjectProperty<Person> person;
    private final ObjectProperty<Task> task;
    private final ObjectProperty<LocalDateTime> logTime;
    private final ObjectProperty<LocalDateTime> endTime;
    private final FloatProperty duration;
    private final StringProperty comment;

    private final BoundPropertySupport bps = new BoundPropertySupport(this);

    public Effort() {
        this.person = new SimpleObjectProperty<>(null);
        this.task = new SimpleObjectProperty<>(null);
        this.logTime = new SimpleObjectProperty<>(LocalDateTime.now());
        this.endTime = new SimpleObjectProperty<>(LocalDateTime.now());
        this.duration = new SimpleFloatProperty(0);
        this.comment = new SimpleStringProperty("");
    }

    public Effort(Person person, Task task, LocalDateTime endTime, Float duration, String comment) {
        this.person = new SimpleObjectProperty<>(person);
        this.task = new SimpleObjectProperty<>(task);
        this.logTime = new SimpleObjectProperty<>(LocalDateTime.now());
        this.endTime = new SimpleObjectProperty<>(endTime);
        this.duration = new SimpleFloatProperty(duration);
        this.comment = new SimpleStringProperty(comment);
    }

    public void initBoundPropertySupport() {
        bps.addPropertyChangeSupportFor(person);
        bps.addPropertyChangeSupportFor(task);
        bps.addPropertyChangeSupportFor(logTime);
        bps.addPropertyChangeSupportFor(endTime);
        bps.addPropertyChangeSupportFor(duration);
        bps.addPropertyChangeSupportFor(comment);
    }

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        this.bps.addChangeListener(listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        this.bps.removeChangeListener(listener);
    }

    public ObjectProperty<Person> personProperty() {
        return person;
    }

    public Person getPerson() {
        return person.get();
    }

    public void setPerson(Person person) {
        this.person.set(person);
    }

    public Task getTask() {
        return task.get();
    }

    public ObjectProperty<Task> taskProperty() {
        return task;
    }

    public void setTask(Task task) {
        this.task.set(task);
    }

    public LocalDateTime getLogTime() {
        return logTime.get();
    }

    public ObjectProperty<LocalDateTime> logTime() {
        return logTime;
    }

    public void setLogTime(LocalDateTime logTime) {
        this.logTime.set(logTime);
    }

    public ObjectProperty<LocalDateTime> endTimeProperty() {
        return endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime.get();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime.set(endTime);
    }

    public FloatProperty durationProperty() {
        return duration;
    }

    public Float getDuration() {
        return duration.get();
    }

    public void setDuration(Float duration) {
        this.duration.set(duration);
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public String getComment() {
        return comment.get();
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchStrings = new ArrayList<>();
        return searchStrings;
    }

    public static Callback<Effort, Observable[]> getWatchStrategy() {
        return p -> new Observable[] {p.duration};
    }
}
