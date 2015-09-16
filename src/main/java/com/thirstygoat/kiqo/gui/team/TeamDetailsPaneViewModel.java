package com.thirstygoat.kiqo.gui.team;

import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.*;
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
        final ObservableList<TeamMemberListItemViewModel> list;
        if (organisationProperty().get() != null) {
            list = organisationProperty().get().getPeople().stream()
                    .filter(person -> person.getTeam() == null || person.getTeam().equals(this.modelWrapper.get()))
                    .map(TeamMemberListItemViewModel::new).collect(GoatCollectors.toObservableList());
        } else {
            list = FXCollections.observableArrayList();
        }
        return list;
        
    }
}
