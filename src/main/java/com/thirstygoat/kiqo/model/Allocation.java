package com.thirstygoat.kiqo.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Created by samschofield on 21/04/15.
 */
public class Allocation {
    private final Team team;
    private final Project project;

    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

    /**
     * Creates a new allocation, checks that the start date is before the end date.
     *
     * @param team the team for the allocation
     * @param startDate the start date for the allocation
     * @param endDate the end date for the allocation
     * @param project the project for the allocation
     */
    public Allocation(Team team, LocalDate startDate, LocalDate endDate, Project project) {
        this.team = team;
        this.project = project;

        this.startDate.set(startDate);
        setEndDate(endDate);
    }

    /**
     * Calculates whether this allocation is currently in effect, according to the system time.
     * @return true if allocation is current
     */
    public boolean isCurrent() {
        final LocalDate now = LocalDate.now();
        return getStartDate().isBefore(now.plusDays(1)) && getEndDate().isAfter(now);
    }

    /**
     * Calculates whether this allocation will start in the future, according to the system time.
     * @return true if allocation is in the future
     */
    public boolean isFuture() {
        final LocalDate now = LocalDate.now();
        return getStartDate().isAfter(now);
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate date) {
        startDate.set(date);
    }

    public ObjectProperty<LocalDate> getStartDateProperty() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public void setEndDate(LocalDate date) {
        if (date == null) {
            endDate.set(LocalDate.MAX);
        } else {
            endDate.set(date);
        }
    }

    public ObjectProperty<LocalDate> getEndDateProperty() {
        return endDate;
    }

    /**
     *
     * @return the project the allocation belongs to
     */
    public Project getProject() {
        return project;
    }

    /**
     *
     * @return team the team for the allocation
     */
    public Team getTeam() {
        return team;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Allocation)) {
            return false;
        }
        final Allocation other = (Allocation) obj;
        if (!endDate.get().equals(other.endDate.get())) {
            return false;
        }
        if (project == null) {
            if (other.project != null) {
                return false;
            }
        } else if (!project.equals(other.project)) {
            return false;
        }
        if (startDate == null) {
            if (other.startDate != null) {
                return false;
            }
        } else if (!startDate.equals(other.startDate)) {
            return false;
        }
        if (team == null) {
            if (other.team != null) {
                return false;
            }
        } else if (!team.equals(other.team)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((team == null) ? 0 : team.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Allocation{team=" + team.getShortName() + ", project=" + project.getShortName() + ", startDate=" + startDate.get()
                + ", endDate=" + endDate.get() + "}";
    }
}
