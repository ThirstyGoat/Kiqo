package seng302.group4;

import java.util.List;

/**
 * Model class for Person.
 * Created by james on 17/03/15.
 */
public class Person extends Item {
    private String shortName;
    private String longName;
    private String description;
    private String userID;
    private String emailAddress;
    private String phoneNumber;
    private String department;
    private List<Skill> skills;
    private Team team;

    /**
     * Create new Person
     *
     * @param shortName a unique short name for the person
     * @param longName a long/full name for the person
     * @param description a description for the person
     * @param userID a userID for the person
     * @param emailAddress a email address for the person
     * @param phoneNumber a phone number for the person
     * @param department a department the person works in
     * @param skills list of skills the person has
     */
    public Person(String shortName, String longName, String description, String userID, String emailAddress, String phoneNumber,
            String department, List<Skill> skills) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;

        this.userID = userID;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.skills = skills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Person person = (Person) o;

        if (department != null ? !department.equals(person.department) : person.department != null) {
            return false;
        }
        if (description != null ? !description.equals(person.description) : person.description != null) {
            return false;
        }
        if (emailAddress != null ? !emailAddress.equals(person.emailAddress) : person.emailAddress != null) {
            return false;
        }
        if (!longName.equals(person.longName)) {
            return false;
        }
        if (phoneNumber != null ? !phoneNumber.equals(person.phoneNumber) : person.phoneNumber != null) {
            return false;
        }
        if (!shortName.equals(person.shortName)) {
            return false;
        }
        if (userID != null ? !userID.equals(person.userID) : person.userID != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = shortName.hashCode();
        result = 31 * result + longName.hashCode();
        return result;
    }

    /**
     * To string method for a person.
     * Will return the fields that are not set to null
     * @return the string representation of a Person object
     */
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Person{shortName='" + shortName + "\', longName='" + longName + "\'");
        stringBuilder.append(", description='" + description + "\'");
        stringBuilder.append(", userID='" + userID + "\'");
        stringBuilder.append(", emailAddress='" + emailAddress + "\'");
        stringBuilder.append(", phoneNumber='" + phoneNumber + "\'");
        stringBuilder.append(", department='" + department + "\'");
        stringBuilder.append(", team='" + ((team == null) ? "null" : team.getShortName()) + "'}");
        return stringBuilder.toString();
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}