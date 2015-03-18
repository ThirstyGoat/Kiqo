package seng302.group4;

import java.io.File;

/**
 * Created by james on 17/03/15.
 */
public class Person {
    private String shortName;
    private String longName;
    private String description;
    private String userID;
    private String emailAddress;
    private Integer phoneNumber;
    private String department;

    /* TODO
     * Save person to project
     * Check short names != null
     * Check short names == unique
     * Menu has a Save item which saves people information with project data
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
                  Integer phoneNumber, String department) {
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
     * @return
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
            } if (getPhoneNumber() != null) {
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

    public Integer getPhoneNumber() {
        return phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

}