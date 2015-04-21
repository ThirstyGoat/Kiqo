package seng302.group4;

import java.time.LocalDate;

/**
 * Created by samschofield on 21/04/15.
 */
public class Allocation {
    private LocalDate startDate;
    private LocalDate endDate;
    private Team team;

    public Allocation(Team team, LocalDate startDate, LocalDate endDate) {

        if(startDate.isAfter(endDate)) {
            throw new RuntimeException("End date is before start date");
        } else {
            this.team = team;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }


}
