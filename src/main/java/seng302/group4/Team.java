package seng302.group4;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by bradley on 27/03/15.
 */
public class Team implements Serializable {
    private String shortName;
    private String description;
    private ArrayList<Person> teamMembers = new ArrayList<Person>();

    public Team(String shortName, String description) {
        this.shortName = shortName;
        this.description = description;
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

    public void setTeamMembers(Person person) {
        person.setTeam(this);
        this.teamMembers.add(person);
    }
}
