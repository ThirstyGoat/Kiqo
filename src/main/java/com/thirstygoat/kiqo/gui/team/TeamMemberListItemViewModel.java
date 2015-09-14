package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Team;

/**
 * Created by leroy on 15/09/15.
 */
public class TeamMemberListItemViewModel {
    private final Person person;
    private final Role role;

    public enum Role {
        PO,
        SM,
        DEV,
        OTHER
    }

    TeamMemberListItemViewModel(Person person) {
        this.person = person;

        Team team = person.getTeam();
        if (team.getProductOwner() != null && person.getTeam().getProductOwner().equals(person)) {
            role = Role.PO;
        } else if (team.getScrumMaster() != null && team.getScrumMaster().equals(person)) {
            role = Role.SM;
        } else if (team.getDevTeam() != null && team.getDevTeam().contains(person)) {
            role = Role.DEV;
        } else {
            role = Role.OTHER;
        }
    }
}
