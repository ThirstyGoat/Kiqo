package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.Person;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

import java.util.stream.Collectors;

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
    public void reload() {
        super.reload();
        teamMemberViewModels.clear();
        teamMemberViewModels.setAll(teamMembersProperty().stream().map(TeamMemberListItemViewModel::new)
                        .collect(Collectors.toList()));
    }

    @Override
    public void commitEdit() {
        Command command = createCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    @Override
    public void cancelEdit() {
        reload();
    }
}
