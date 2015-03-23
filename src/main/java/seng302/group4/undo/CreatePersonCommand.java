package seng302.group4.undo;

import seng302.group4.Person;

/**
 * Command to create a project
 *
 * @author amy
 *
 */
public class CreatePersonCommand extends Command<Person> {
    private final String shortName;
    private final String longName;
    private final String description;
    private final String userID;
    private final String emailAddress;
    private final String phoneNumber;
    private final String department;

    private Person person = null;

    /**
     *
     * @param shortName
     * @param longName
     * @param description
     * @param userID
     * @param emailAddress
     * @param phoneNumber
     * @param department
     */
    public CreatePersonCommand(final String shortName, final String longName, final String description, final String userID,
                               final String emailAddress, final String phoneNumber, final String department                               ) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.userID = userID;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.department = department;
    }

    @Override
    public Person execute() {
        if (person == null) {
            person = new Person(shortName, longName, description, userID, emailAddress, phoneNumber, department);
        }
        return person;
    }

    @Override
    public void undo() {

    }

    @Override
    public String toString() {
        return "<Create Person: \"" + shortName + "\">";
    }

    public Person getPerson() {
        return person;
    }

    public String getType() {
        return "Create Person";
    }

}
