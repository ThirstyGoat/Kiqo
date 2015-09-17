package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableField;
import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by leroy on 17/09/15.
 */
public class Effort {
    private final ObjectProperty<Person> person;
    private final ObjectProperty<Task> task;
    private final ObjectProperty<LocalDateTime> logTime;
    private final ObjectProperty<LocalDateTime> endTime;
    private final FloatProperty duration;
    private final StringProperty comment;

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

    public void setPerson(Person person) {
        this.person.set(person);
    }

    public ObjectProperty<Person> personProperty() {
        return person;
    }

    public Person getPerson() {
        return person.get();
    }

    public Task getTask() {
        return task.get();
    }

    public LocalDateTime getLogTime() {
        return logTime.get();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime.set(endTime);
    }

    public ObjectProperty<LocalDateTime> endTimeProperty() {
        return endTime;
    }

    public LocalDateTime getEndTime() {
        return endTime.get();
    }

    public void setDuration(Float duration) {
        this.duration.set(duration);
    }

    public FloatProperty durationProperty() {
        return duration;
    }

    public Float getDuration() {
        return duration.get();
    }

    public void setComment(String comment) {
        this.comment.set(comment);
    }

    public StringProperty commentProperty() {
        return comment;
    }

    public String getComment() {
        return comment.get();
    }
}
