package com.thirstygoat.kiqo.model;

import java.util.*;

/**
 * Created by samschofield on 23/07/15.
 */
public enum Status {
    NOT_STARTED("Not Started", "not-started"),
    IN_PROGRESS("In Progress", "in-progress"),
    VERIFY("Verify", "verify"),
    DONE("Done", "done");

    private final String cssClass;
    private final String label;
    
    private Status(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    /**
     * Return the status with the given label
     * @param label label of a Status
     * @return status with the given label
     * @throws RuntimeException if label does not belong to any Status
     */
    public static Status getEnum(String label) {
        for (Status status: Status.values()) {
            if (status.label.equals(label)) {
                return status;
            }
        }
        throw new RuntimeException("Status " + label + " does not exist.");
    }

    /**
     * @return list of the labels for each enum
     */
    public static List<String> getStrings() {
        List<String> strs = new ArrayList<>();
        for (Status status : Status.values()) {
            strs.add(status.toString());
        }
        return strs;
    }

    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return label;
    }
}