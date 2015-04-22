package seng302.group4;

import java.time.LocalDate;


/**
 * Created by leroy on 10/04/15.
 */
public class Release extends Item{
    private Organisation organisation;
    private String shortName;
    private String description;
    private LocalDate date;  //change back to date

    public Release(String shortName, LocalDate date, String description, Organisation organisation) {
        this.shortName = shortName;
        this.description = description;
        this.date = date;
        this.organisation = organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Release release = (Release) o;

        return shortName.equals(release.shortName);
    }

    @Override
    public int hashCode() {
        return shortName.hashCode();
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String toString() {
        return shortName;
    }
}
