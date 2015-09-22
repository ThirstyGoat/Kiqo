package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableField;
import com.thirstygoat.kiqo.util.BoundPropertySupport;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leroy on 17/09/15.
 */
public class Effort implements BoundProperties, Searchable {
    private final ObjectProperty<Person> person;
    private final ObjectProperty<Task> task;
    private final ObjectProperty<LocalDateTime> endDateTime;
    private final ObjectProperty<Duration> duration;
    private final StringProperty comment;
    private final ObjectProperty<LocalDateTime> logTimeStamp;
    private final BoundPropertySupport bps = new BoundPropertySupport(this);

    public Effort() {
        this.person = new SimpleObjectProperty<>(null);
        this.task = new SimpleObjectProperty<>(null);
        this.logTimeStamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.endDateTime = new SimpleObjectProperty<>(LocalDateTime.now());
        this.duration = new SimpleObjectProperty<>(Duration.ofMinutes(0));
        this.comment = new SimpleStringProperty("");
    }

    public Effort(Person person, Task task, LocalDateTime endDateTime, Duration duration, String comment) {
        this.person = new SimpleObjectProperty<>(person);
        this.task = new SimpleObjectProperty<>(task);
        this.logTimeStamp = new SimpleObjectProperty<>(LocalDateTime.now());
        this.endDateTime = new SimpleObjectProperty<>(endDateTime);
        this.duration = new SimpleObjectProperty<>(duration);
        this.comment = new SimpleStringProperty(comment);
    }

    public void initBoundPropertySupport() {
        bps.addPropertyChangeSupportFor(person);
        bps.addPropertyChangeSupportFor(task);
        bps.addPropertyChangeSupportFor(logTimeStamp);
        bps.addPropertyChangeSupportFor(endDateTime);
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

    public LocalDateTime getLogTimeStamp() {
        return logTimeStamp.get();
    }

    public ObjectProperty<LocalDateTime> logTime() {
        return logTimeStamp;
    }

    public void setLogTimeStamp(LocalDateTime logTimeStamp) {
        this.logTimeStamp.set(logTimeStamp);
    }

    public ObjectProperty<LocalDateTime> endDateTimeProperty() {
        return endDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime.get();
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime.set(endDateTime);
    }

    public ObjectProperty<Duration> durationProperty() {
        return duration;
    }

    public Duration getDuration() {
        return duration.get();
    }

    public double getDurationAsNumber() {
        return duration.get().toMinutes();
    }

    public void setDuration(Duration duration) {
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
