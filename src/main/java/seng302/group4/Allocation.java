package seng302.group4;

import java.time.LocalDate;

/**
 * Created by samschofield on 21/04/15.
 */
public class Allocation {
    private LocalDate startDate;
    private LocalDate endDate;
    private Team team;
    private Project project;

    /**
     * Creates a new allocation, checks that the start date is before the end date
     * @throws RuntimeException if start date is after end date
     * @param team the team for the allocation
     * @param startDate the start date for the allocation
     * @param endDate the end date for the allocation
     */
    public Allocation(Team team, LocalDate startDate, LocalDate endDate, Project project) {

        if(startDate.isAfter(endDate)) {
            throw new RuntimeException("End date is before start date");
        } else {
            this.team = team;
            this.startDate = startDate;
            this.endDate = endDate;
            this.project = project;
        }
    }

    /**
     *
     * @return startDate the start date for the allocation
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     *
     * @return endDate the end date for the allocation
     */
    public LocalDate getEndDate() {
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
}
