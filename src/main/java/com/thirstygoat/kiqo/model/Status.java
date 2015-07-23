package com.thirstygoat.kiqo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samschofield on 23/07/15.
 */
public enum Status {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    PENDING("Pending"),
    BLOCKED("Blocked"),
    READY("Ready"),
    DONE("Done"),
    DEFERRED("Deferred");

    private String label;

    private Status(String label) {
        this.label = label;
    }

    /**
     * Return the status with the given label
     * @param label label of a Status
     * @return status with the given label
     * @throws RuntimeException if label does not belong to any Status
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

    @Override
    public String toString() {
        return label;
    }
}
