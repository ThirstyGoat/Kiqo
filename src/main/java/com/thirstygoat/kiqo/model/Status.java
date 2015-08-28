package com.thirstygoat.kiqo.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

/**
 * Created by samschofield on 23/07/15.
 */
public enum Status {
    NOT_STARTED("Not Started", Color.LIGHTGRAY, "not-started"),
    IN_PROGRESS("In Progress", Color.LIGHTGOLDENRODYELLOW, "in-progress"),
    PENDING("Pending", Color.LIGHTBLUE, "pending"),
    BLOCKED("Blocked", Color.LIGHTSALMON, "blocked"),
    VERIFY("Verify", Color.LIGHTGREEN, "verify"),
    DONE("Done", Color.GREENYELLOW, "done"),
    DEFERRED("Deferred", Color.LIGHTCORAL, "deferred");

    public Color color;
    private String cssClass;
    private String label;

    private Status(String label, Color color, String cssClass) {
        this.label = label;
        this.color = color;
        this.cssClass = cssClass;
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

    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return label;
    }
}