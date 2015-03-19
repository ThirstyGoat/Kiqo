package seng302.group4;

/**
 * Model class for Person.
 * Created by james on 17/03/15.
 */
public class Person {
    private String shortName;
    private String longName;
    private String description;
    private String userID;
    private String emailAddress;
    private String phoneNumber;
    private String department;

    /* TODO
     * Check short names == unique
     * Create / edit person dialog has save button to save person in project directory
     * Open project > opens all people
     * View menu > show people
     */

    /**
     * Create new Person
     * @param shortName a unique short name for the person
     * @param longName a long/full name for the person
     * @param description a description for the person
     * @param userID a userID for the person
     * @param emailAddress a email address for the person
     * @param phoneNumber a phone number for the person
     * @param department a department the person works in
     */
    public Person(String shortName, String longName, String description, String userID, String emailAddress,
                  String phoneNumber, String department) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.userID = userID;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.department = department;
    }

    /**
     * To string method for a person. 
     * Will return the fields that are not set to null
     * @return the string representation of a Person object
     */
    @Override
    public String toString() {
        String str = "Person{"  + "shortName='" + this.shortName + '\'' + ", longName='" + this.longName;
            if (description != null) {
                str += '\'' + ", description='" + description;
            } if (userID != null) {
                str += '\'' + ", userID='" + userID;
            } if (emailAddress != null) {
                str += '\'' + ", emailAddress='" + emailAddress;
            } if (phoneNumber != null) {
                str += '\'' + ", phoneNumber='" + phoneNumber;
            } if (department != null) {
                str += '\'' + ", department='" + department;
            }
        return str + '\'' + '}';
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getDescription() {
        return description;
    }

    public String getUserID() {
        return userID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDepartment() {
        return department;
    }
}