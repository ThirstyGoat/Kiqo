package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.person.PersonViewModel;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Team;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by leroy on 15/09/15.
 */
public class TeamMemberListItemViewModel extends PersonViewModel implements Editable {
    private final StringProperty roleNameProperty = new SimpleStringProperty();
    private final StringProperty roleBadgeStyleProperty = new SimpleStringProperty();

    TeamMemberListItemViewModel(Person person) {
        this.load(person, null);

            Team team = person.getTeam();
            if (team != null) {
                if (team.getProductOwner() != null && person.getTeam().getProductOwner().equals(person)) {
                    roleNameProperty.set("Product Owner");
                    roleBadgeStyleProperty.set("-fx-background-color: blue; -fx-padding: 10px;");
                } else if (team.getScrumMaster() != null && team.getScrumMaster().equals(person)) {
                    roleNameProperty.set("Scrum Master");
                    roleBadgeStyleProperty.set("-fx-background-color: red; -fx-padding: 10px;");
                } else if (team.getDevTeam() != null && team.getDevTeam().contains(person)) {
                    roleNameProperty.set("Developer");
                    roleBadgeStyleProperty.set("-fx-background-color: green; -fx-padding: 10px;");
                } else {
                    roleNameProperty.set("Other");
                    roleBadgeStyleProperty.set("-fx-background-color: grey; -fx-padding: 10px;");
                }
            } else {
                roleNameProperty.set("");
                roleBadgeStyleProperty.set("-fx-background-color: white; -fx-padding: 10px;");
            }
    }

    public StringProperty roleNameProperty() {
        return roleNameProperty;
    }

    public StringProperty roleColorProperty() {
        return roleBadgeStyleProperty;
    }

    public void commitEdit() {
        Command command = getCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    public void cancelEdit() {
        reload();
    }
}
