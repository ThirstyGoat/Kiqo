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
    private ListProperty<TeamMemberListItemViewModel> teamMemberViewModels =
                    new SimpleListProperty<>(FXCollections.observableArrayList());

    public ObjectProperty<MainController> mainControllerProperty() {
        return mainControllerProperty;
    }

    public ListProperty<TeamMemberListItemViewModel> teamMemberViewModels() {
        return teamMemberViewModels;
    }

    @Override
    public void afterLoad() {
        Runnable mapPeople = () -> teamMemberViewModels.setAll(teamMembersProperty().stream()
                        .map(TeamMemberListItemViewModel::new)
                        .collect(Collectors.toList()));
        modelWrapper.onModelChange(change -> Platform.runLater(mapPeople));
        mapPeople.run();
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

    public ObservableList<TeamMemberListItemViewModel> eligibleTeamMembers() {
        final Callback<ObservableList<Person>, ObservableList<TeamMemberListItemViewModel>> callback = allPeople -> allPeople.stream()
                .filter(person -> person.getTeam() == null || person.getTeam().equals(this.modelWrapper.get()))
                .map(TeamMemberListItemViewModel::new).collect(GoatCollectors.toObservableList());

        final ObservableList<TeamMemberListItemViewModel> list = callback.call(organisationProperty().get().getPeople());
        
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
