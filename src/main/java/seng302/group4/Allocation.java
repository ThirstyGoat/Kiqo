package seng302.group4;

import java.time.LocalDate;

/**
 * Created by samschofield on 21/04/15.
 */
public class Allocation {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Team team;
    private final Project project;

    /**
     * Creates a new allocation, checks that the start date is before the end date
     * @throws RuntimeException if start date is after end date
     * @param team the team for the allocaiton
     * @param startDate the start date for the allocation
     * @param endDate the end date for the allocation
     */
    public Allocation(Team team, LocalDate startDate, LocalDate endDate, Project project) {
        this.team = team;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
        team.getAllocations().add(this); // move to command
        project.getAllocations().add(this); // move to command
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
