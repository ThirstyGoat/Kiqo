package seng302.group4.undo;

import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Team;

public class DeletePersonCommand extends Command<Person> {

    private Project project;
    private Person person;
    private Team team;
    private boolean isProductOwner = false;
    private boolean isScrumMaster = false;
    private boolean inDevTeam = true;

    /**
     * @param person Person to be deleted
     * @param project Project to which the skill belongs
     */
    public DeletePersonCommand(final Person person, final Project project) {
        this.person = person;
        this.project = project;
        team = person.getTeam();
        if (team != null) {
            if (team.getProductOwner() == person) {
                isProductOwner = true;
            }
            if (team.getScrumMaster() == person) {
                isScrumMaster = true;
            }
            if (team.getDevTeam().contains(person)) {
                inDevTeam = true;
            }
        }
    }

    @Override
    public Person execute() {
        // Remove the person first from their team, working the way up the hierarchy
        if (team != null) {
            // Remove person from PO/SM/Dev Role if they're there, and remember where they are
            if (isProductOwner) {
                team.setProductOwner(null);
            }
            if (isScrumMaster) {
                team.setScrumMaster(null);
            }
            if (inDevTeam) {
                team.getDevTeam().remove(person);
            }

            // Remove person from Team Members
            team.getTeamMembers().remove(person);
        }

        // Remove the person from the project
        project.getPeople().remove(person);
        return person;
    }

    @Override
    public void undo() {
        // Repeat execute steps backwards
        // Add person back to project
        project.getPeople().add(person);
        if (team != null) {
            // Add person to Team Members
            team.getTeamMembers().add(person);

            if (isProductOwner) {
                team.setProductOwner(person);
            }
            if (isScrumMaster) {
                team.setScrumMaster(person);
            }
            if (inDevTeam) {
                team.getDevTeam().add(person);
            }
        }
    }

    @Override
    public String toString() {
        return "<Delete Person: \"" + person.getShortName() + "\">";
    }

    public Person getPerson() {
        return person;
    }

    public String getType() {
        return "Delete Person";
    }
}
