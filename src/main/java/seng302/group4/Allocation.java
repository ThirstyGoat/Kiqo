package seng302.group4;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by samschofield on 21/04/15.
 */
public class Allocation {
    private Team team;
    private Project project;

    private StringProperty teamStringProperty;
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();

    /**
     * Creates a new allocation, checks that the start date is before the end date
     * @param team the team for the allocation
     * @param startDate the start date for the allocation
     * @param endDate the end date for the allocation
     * @param project the project for the allocation
     */
    public Allocation(Team team, LocalDate startDate, LocalDate endDate, Project project) {
        this.team = team;
        this.project = project;  //remove this

        teamStringProperty = new SimpleStringProperty(team.getShortName());
        this.startDate.set(startDate);
        this.endDate.set(endDate);
    }

    /**
     *
     * @return startDate the start date for the allocation
     */
    public LocalDate getStartDate() {
        return startDate.get();
    }

    public ObjectProperty getStartDateProperty() {
        return startDate;
    }

    public void setStartDate(LocalDate date) {
        startDate.set(date);
    }

    public void setEndDate(LocalDate date) {
        endDate.set(date);
    }

    /**
     *
     * @return endDate the end date for the allocation
     */
    public LocalDate getEndDate() {
        // If your IDE says endDate can not be null, lies.
        return (endDate == null) ? null : endDate.get();
    }

    public ObjectProperty getEndDateProperty() {
        // Since the end date can be null (ie. an allocation has no definite end date, we need to at least
        // create the property
        if (endDate == null) {
            endDate = new SimpleObjectProperty<>();
        }
        return endDate;
    }

    // ###################################################################################

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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Allocation{team=" + team.getShortName() + ", project=" + project.getShortName() + ", startDate=" +
                startDate.get() + "}";
    }
}
