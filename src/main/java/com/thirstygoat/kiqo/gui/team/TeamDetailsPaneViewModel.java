package com.thirstygoat.kiqo.gui.team;

import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.util.Callback;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.*;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.util.GoatCollectors;

public class TeamDetailsPaneViewModel extends TeamViewModel implements Editable {

    private ObjectProperty<MainController> mainControllerProperty = new SimpleObjectProperty<>();
    private ListProperty<PersonListItemViewModel> teamMemberViewModels =
                    new SimpleListProperty<>(FXCollections.observableArrayList());

    public ObjectProperty<MainController> mainControllerProperty() {
        return mainControllerProperty;
    }

    public ListProperty<PersonListItemViewModel> teamMemberViewModels() {
        return teamMemberViewModels;
    }

    @Override
    public void commitEdit() {
        Command command = getCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    @Override
    public void cancelEdit() {
        reload();
    }

    public ListProperty<Person> eligibleTeamMembers() {
        final Callback<ObservableList<Person>, ObservableList<Person>> callback = allPeople -> allPeople.stream()
                .filter(person -> person.getTeam() == null || person.getTeam().equals(this.modelWrapper.get()))
                .collect(GoatCollectors.toObservableList());

        final ListProperty<Person> list = new SimpleListProperty<>(FXCollections.observableArrayList());

        organisationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                list.setAll(callback.call(newValue.getPeople()));
            } else {
                list.clear();
            }
        });

        return list;
    }
}
