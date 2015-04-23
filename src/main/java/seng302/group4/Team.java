package seng302.group4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bradley on 27/03/15.
 */
public class Team extends Item {
    private final List<Person> devTeam = new ArrayList<>();
    private String shortName;
    private String description;
    private Person productOwner;
    private Person scrumMaster;
    private List<Person> teamMembers;
    private final ArrayList<Allocation> allocations = new ArrayList<>();

    public Team(String shortName, String description, List<Person> teamMembers) {
        this.shortName = shortName;
        this.description = description;
        this.teamMembers = teamMembers;
    }

    @Override
    public String toString() {
        return "Team{" +
                "shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
 ", productOwner="
                + productOwner.getShortName() + ", scrumMaster=" + scrumMaster.getShortName() +
                ", teamMembers=" + teamMembers +
                ", devTeam=" + devTeam +
                '}';
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public List<Person> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<Person> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Person getProductOwner() {
        return productOwner;
    }

    public void setProductOwner(Person productOwner) {
        this.productOwner = productOwner;
    }

    public Person getScrumMaster() {
        return scrumMaster;
    }

    public void setScrumMaster(Person scrumMaster) {
        this.scrumMaster = scrumMaster;
    }

    public List<Person> getDevTeam() {
        return devTeam;
    }

    public void setDevTeam(List<Person> devTeam) {
        this.devTeam.clear();
        this.devTeam.addAll(devTeam.stream().collect(Collectors.toList()));
    }

    public ArrayList<Allocation> getAllocations() {
        return allocations;
    }
}
