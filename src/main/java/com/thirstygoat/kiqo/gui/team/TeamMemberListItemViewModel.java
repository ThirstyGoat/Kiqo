package com.thirstygoat.kiqo.gui.team;

import javafx.beans.property.*;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.person.PersonViewModel;
import com.thirstygoat.kiqo.model.*;

/**
 * Created by leroy on 15/09/15.
 */
@Deprecated // TODO remove file
public class TeamMemberListItemViewModel extends PersonViewModel implements Editable {
    private final StringProperty roleNameProperty = new SimpleStringProperty();
    private final StringProperty roleBadgeStyleProperty = new SimpleStringProperty();
    private ObjectProperty<Person> person;

    TeamMemberListItemViewModel(Person person) {
        this.load(person, null);
        this.person.set(person);

        Team team = person.getTeam();
        if (team != null) {
            if (team.getProductOwner() != null && person.getTeam().getProductOwner().equals(person)) {
                roleNameProperty.set("Product Owner");
                roleBadgeStyleProperty.set("-fx-text-fill: blue;");
            } else if (team.getScrumMaster() != null && team.getScrumMaster().equals(person)) {
                roleNameProperty.set("Scrum Master");
                roleBadgeStyleProperty.set("-fx-text-fill: red;");
            } else if (team.getDevTeam() != null && team.getDevTeam().contains(person)) {
                roleNameProperty.set("Developer");
                roleBadgeStyleProperty.set("-fx-text-fill: green;");
            } else {
                roleNameProperty.set("Other");
                roleBadgeStyleProperty.set("-fx-text-fill: grey;");
            }
        } else {
            roleNameProperty.set("");
            roleBadgeStyleProperty.set("-fx-text-fill: white;");
        }
    }

    public StringProperty roleNameProperty() {
        return roleNameProperty;
    }

    public StringProperty roleColorProperty() {
        return roleBadgeStyleProperty;
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
