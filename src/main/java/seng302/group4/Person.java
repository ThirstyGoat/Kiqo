package seng302.group4;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model class for Person.
 * Created by james on 17/03/15.
 */
public class Person extends Item {
    private StringProperty shortName;
    private StringProperty longName;
    private StringProperty description;
    private StringProperty userID;
    private StringProperty emailAddress;
    private StringProperty phoneNumber;
    private StringProperty department;
    private ObservableList<Skill> skills = FXCollections.observableArrayList();
    private Team team;

    /**
     * No-args constructor for JavaBeans(TM) compliance. Use at your own risk.
     */
    public Person() {
        this.shortName = new SimpleStringProperty();
        this.longName = new SimpleStringProperty();
        this.description = new SimpleStringProperty();
        this.userID = new SimpleStringProperty();
        this.emailAddress = new SimpleStringProperty();
        this.phoneNumber = new SimpleStringProperty();
        this.department = new SimpleStringProperty();
    }

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
        this.shortName = new SimpleStringProperty(shortName);
        this.longName = new SimpleStringProperty(longName);
        this.description = new SimpleStringProperty(description);

        this.userID = new SimpleStringProperty(userID);
        this.emailAddress = new SimpleStringProperty(emailAddress);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.department = new SimpleStringProperty(department);
        this.skills.addAll(skills);
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty userIDProperty() {
        return userID;
    }

    public StringProperty emailAddressProperty() {
        return emailAddress;
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public ObservableList<Skill> observableSkills() {
        return skills;
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
        return shortName.get();
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public String getLongName() {
        return longName.get();
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getUserID() {
        return userID.get();
    }

    public void setUserID(String userID) {
        this.userID.set(userID);
    }

    public String getEmailAddress() {
        return emailAddress.get();
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress.set(emailAddress);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public String getDepartment() {
        return department.get();
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public List<Skill> getSkills() {
        ArrayList<Skill> arrayList = new ArrayList<>();
        arrayList.addAll(skills);
        return arrayList;

    }

    public void setSkills(List<Skill> skills) {
        this.skills.clear();
        this.skills.addAll(skills);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
