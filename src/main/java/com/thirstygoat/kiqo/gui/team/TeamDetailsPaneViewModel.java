package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.*;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Team;
import javafx.beans.property.*;

public class TeamDetailsPaneViewModel extends TeamViewModel implements Editable {
    private ObjectProperty<Team> teamProperty = new SimpleObjectProperty<>();

    @Override
    public void load(Team team, Organisation organisation) {
        super.load(team, organisation);
        teamProperty.set(team);
    }

    public ObjectProperty<Team> teamProperty() {
        return teamProperty;
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
}
