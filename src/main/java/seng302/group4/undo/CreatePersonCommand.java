package seng302.group4.undo;

import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;

import java.util.ArrayList;

/**
 * Command to create a project
 *
 * @author amy, Edited by Bradley 10/4/15
 *
 */
public class CreatePersonCommand extends Command<Person> {

    private Person person;
    private Project project;

    /**
     *
     * @param person Person to be added to a project
     * @param project Project for which person is to be added to
     */
    public CreatePersonCommand(final Person person, final Project project) {
        this.person = person;
        this.project = project;
    }

    @Override
    public Person execute() {
        project.getPeople().add(person);
        return person;
    }

    @Override
    public void undo() {
        // Goodbye person
        project.getPeople().remove(person);
    }

    @Override
    public String toString() {
        return "<Create Person: \"" + person.getShortName() + "\">";
    }

    public Person getPerson() {
        return person;
    }

    public String getType() {
        return "Create Person";
    }

}
