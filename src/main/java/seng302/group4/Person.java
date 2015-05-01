package seng302.group4;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model class for Person. Created by james on 17/03/15.
 */
public class Person extends Item {
    private final StringProperty shortName;
    private final StringProperty longName;
    private final StringProperty description;
    private final StringProperty userID;
    private final StringProperty emailAddress;
    private final StringProperty phoneNumber;
    private final StringProperty department;
    private final ObservableList<Skill> skills = FXCollections.observableArrayList();
    private Team team;

    /**
     * No-args constructor for JavaBeans(TM) compliance. Use at your own risk.
     */
    public Person() {
        shortName = new SimpleStringProperty();
        longName = new SimpleStringProperty();
        description = new SimpleStringProperty();
        userID = new SimpleStringProperty();
        emailAddress = new SimpleStringProperty();
        phoneNumber = new SimpleStringProperty();
        department = new SimpleStringProperty();
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

    @Override
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

        if (!getShortName().equals(person.getShortName())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return getShortName().hashCode();
    }

    /**
     * To string method for a person. Will return the fields that are not set to null
     * 
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
        final ArrayList<Skill> arrayList = new ArrayList<>();
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
