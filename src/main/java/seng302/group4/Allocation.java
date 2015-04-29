package seng302.group4;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Created by samschofield on 21/04/15.
 */
public class Allocation {
    private final Team team;
    private final Project project;

    private final StringProperty teamStringProperty;
    private final ObjectProperty<LocalDate> startDate;
    private final ObjectProperty<LocalDate> endDate;

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
        this.startDate = new SimpleObjectProperty<>(startDate);
        this.endDate = new SimpleObjectProperty<>(endDate);
    }

//    ######################### DO NOT DELETE, USING THESE WHEN GSON IS FIXED #########################

//    public StringProperty getProjectStringProperty() {
//        return projectStringProperty;
//    }
//
//
//    public void setProjectStringProperty(String projectStringProperty) {
//        this.projectStringProperty.set(projectStringProperty);
//    }
//
//
//    public ObjectProperty<LocalDate> getStartDateProperty() {
//        return startDateProperty;
//    }
//
//    public void setStartDateProperty(LocalDate startDateProperty) {
//        this.startDateProperty.set(startDateProperty);
//    }
//
//
//    public ObjectProperty<LocalDate> getEndDateProperty() {
//        return endDateProperty;
//    }
//
//    public void setEndDateProperty(LocalDate endDateProperty) {
//        this.endDateProperty.set(endDateProperty);
//    }


    // ######################### remove these and fix references #########################


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

    /**
     *
     * @return endDate the end date for the allocation
     */
    public LocalDate getEndDate() {
        return (endDate == null) ? null : endDate.get();
    }

    public ObjectProperty getEndDateProperty() {
        return endDate;
    }

    public StringProperty getTeamStringProperty() {
        return teamStringProperty;
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
