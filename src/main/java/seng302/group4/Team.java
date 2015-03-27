package seng302.group4;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bradley on 27/03/15.
 */
public class Team implements Serializable {
    private String shortName;
    private String description;
    private ArrayList<Person> teamMembers;

    public Team(String shortName, String description, ArrayList<Person> teamMembers) {
        this.shortName = shortName;
        this.description = description;
        this.teamMembers = teamMembers;

        setTeamAffiliations();
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Person> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(ArrayList<Person> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public void setTeamAffiliations() {
        for (Person person : teamMembers) {
            person.setTeam(this);
        }
    }
}
