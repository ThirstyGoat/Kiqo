package com.thirstygoat.kiqo.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

/**
 * Created by samschofield on 23/07/15.
 */
public enum Status {
    NOT_STARTED("Not Started", Color.LIGHTGRAY),
    IN_PROGRESS("In Progress", Color.LIGHTGOLDENRODYELLOW),
    PENDING("Pending", Color.LIGHTBLUE),
    BLOCKED("Blocked", Color.LIGHTSALMON),
    READY("Ready", Color.LIGHTGREEN),
    DONE("Done", Color.GREENYELLOW),
    DEFERRED("Deferred", Color.LIGHTCORAL);

    private String label;
    public Color color;

    private Status(String label, Color color) {
        this.label = label;
        this.color = color;
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