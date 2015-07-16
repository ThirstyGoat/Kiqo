package com.thirstygoat.kiqo.model;

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
    private final StringProperty userId;
    private final StringProperty emailAddress;
    private final StringProperty phoneNumber;
    private final StringProperty department;
    private final ObservableList<Skill> skills;
    private Team team;

    /**
     * No-args constructor for JavaBeans(TM) compliance. Use at your own risk.
     */
    public Person() {
        shortName = new SimpleStringProperty();
        longName = new SimpleStringProperty();
        description = new SimpleStringProperty();
        userId = new SimpleStringProperty();
        emailAddress = new SimpleStringProperty();
        phoneNumber = new SimpleStringProperty();
        department = new SimpleStringProperty();
        skills = FXCollections.observableArrayList(Item.getWatchStrategy());
    }

    /**
     * Create new Person
     *
     * @param shortName a unique short name for the person
     * @param longName a long/full name for the person
     * @param description a description for the person
     * @param userId a userId for the person
     * @param emailAddress a email address for the person
     * @param phoneNumber a phone number for the person
     * @param department a department the person works in
     * @param skills list of skills the person has
     */
    public Person(String shortName, String longName, String description, String userId, String emailAddress, String phoneNumber,
            String department, List<Skill> skills) {
        this.shortName = new SimpleStringProperty(shortName);
        this.longName = new SimpleStringProperty(longName);
        this.description = new SimpleStringProperty(description);

        this.userId = new SimpleStringProperty(userId);
        this.emailAddress = new SimpleStringProperty(emailAddress);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.department = new SimpleStringProperty(department);
        this.skills = FXCollections.observableArrayList(Item.getWatchStrategy());
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

    public StringProperty userIdProperty() {
        return userId;
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

    /**
     * To string method for a person. Will return the fields that are not set to null
     *
     * @return the string representation of a Person object
     */
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Person{shortName='" + getShortName() + "\', longName='" + getLongName() + "\'");
        stringBuilder.append(", description='" + getDescription() + "\'");
        stringBuilder.append(", userId='" + getUserId() + "\'");
        stringBuilder.append(", emailAddress='" + getEmailAddress() + "\'");
        stringBuilder.append(", phoneNumber='" + getPhoneNumber() + "\'");
        stringBuilder.append(", department='" + getDepartment() + "\'");
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

    public String getUserId() {
        return userId.get();
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
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
        // temp fix to allow undo add skill to person
        final ArrayList<Skill> skills1 = new ArrayList<>();
        skills1.addAll(skills);
        return skills1;
//        return Collections.unmodifiableList(skills);
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
