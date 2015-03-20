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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (department != null ? !department.equals(person.department) : person.department != null) return false;
        if (description != null ? !description.equals(person.description) : person.description != null) return false;
        if (emailAddress != null ? !emailAddress.equals(person.emailAddress) : person.emailAddress != null)
            return false;
        if (!longName.equals(person.longName)) return false;
        if (phoneNumber != null ? !phoneNumber.equals(person.phoneNumber) : person.phoneNumber != null) return false;
        if (!shortName.equals(person.shortName)) return false;
        if (userID != null ? !userID.equals(person.userID) : person.userID != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = shortName.hashCode();
        result = 31 * result + longName.hashCode();
        return result;
    }

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