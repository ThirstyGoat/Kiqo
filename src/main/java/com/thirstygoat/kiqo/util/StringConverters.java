package com.thirstygoat.kiqo.util;

import javafx.beans.property.ObjectProperty;
import javafx.util.StringConverter;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Status;
import com.thirstygoat.kiqo.model.Team;

/**
 * Created by leroy on 21/07/15.
 */
public class StringConverters {
    StringConverter<Project> projectStringConverter;
    StringConverter<Person> personStringConverter;

    public static StringConverter<Project> projectStringConverter(Organisation organisation) {
        return new StringConverter<Project>() {
           @Override
            public String toString(Project project) {
                return project != null ? project.getShortName() : "";
            }

            @Override
            public Project fromString(String shortName) {
                for (final Project p : organisation.getProjects()) {
                    if (p.getShortName().equals(shortName)) {
                        return p;
                    }
                }
                return null;
            }
        };
    }

    public static StringConverter<Project> projectStringConverter(ObjectProperty<Organisation> organisationProperty) {
        return new StringConverter<Project>() {
            @Override
            public String toString(Project project) {
                return project != null ? project.getShortName() : "";
            }

            @Override
            public Project fromString(String s) {
                if (organisationProperty.get() != null) {
                    for (final Project p : organisationProperty.get().getProjects()) {
                        if (p.getShortName().equals(s)) {
                            return p;
                        }
                    }
                }
                return null;
            }
        };
    }

    public static StringConverter<Status> statusStringConverter() {
        return new StringConverter<Status>() {
            @Override
            public Status fromString(String string) {
                return Status.getEnum(string);
            }

            @Override
            public String toString(Status status) {
                return status != null ? status.toString() : "";
            }
        };
    }

    public static StringConverter<Person> personStringConverter(Organisation organisation) {
        return new StringConverter<Person>() {
            @Override
            public String toString(Person person) {
                return person != null ? person.getShortName() : "";
            }

            @Override
            public Person fromString(String shortName) {
                for (final Person person : organisation.getPeople()) {
                    if (person.getShortName().equals(shortName)) {
                        return person;
                    }
                }
                return null;
            }
        };
    }

    public static StringConverter<Person> personStringConverter(ObjectProperty<Organisation> organisationProperty) {
        return new StringConverter<Person>() {
            @Override
            public String toString(Person person) {
                return person != null ? person.getShortName() : "";
            }

            @Override
            public Person fromString(String shortName) {
                if (organisationProperty.get() != null) {
                    for (final Person person : organisationProperty.get().getPeople()) {
                        if (person.getShortName().equals(shortName)) {
                            return person;
                        }
                    }
                }
                return null;
            }
        };
    }

    public static StringConverter<Scale> scaleStringConverter() {
        return new StringConverter<Scale>() {
            @Override
            public Scale fromString(String string) {
                return Scale.getEnum(string);
            }
    
            @Override
            public String toString(Scale scale) {
                return scale != null ? scale.toString() : "";
            }
        };
    }

    public static StringConverter<Team> teamStringConverter(Organisation organisation) {
        return new StringConverter<Team>() {
            @Override
            public String toString(Team team) {
                return team != null ? team.getShortName() : "";
            }

            @Override
            public Team fromString(String shortName) {
                for (final Team team : organisation.getTeams()) {
                    if (team.getShortName().equals(shortName)) {
                        return team;
                    }
                }
                return null;
            }
        };
    }

    public static StringConverter<Team> teamStringConverter(ObjectProperty<Organisation> organisationProperty) {
        return new StringConverter<Team>() {
            @Override
            public String toString(Team team) {
                return team != null ? team.getShortName() : "";
            }

            @Override
            public Team fromString(String shortName) {
                if (organisationProperty.get() != null) {
                    for (final Team team : organisationProperty.get().getTeams()){
                        if (team.getShortName().equals(shortName)) {
                            return team;
                        }
                    }
                }
                return null;
            }
        };
    }

    public static StringConverter<Backlog> backlogStringConverter(Organisation organisation) {
        return new StringConverter<Backlog>() {
            @Override
            public String toString(Backlog backlog) {
                return backlog != null ? backlog.getShortName() : "";
            }

            @Override
            public Backlog fromString(String shortName) {
                for (final Project project : organisation.getProjects()) {
                    for (final Backlog backlog : project.getBacklogs()) {
                        if (backlog.getShortName().equals(shortName)) {
                            return backlog;
                        }
                    }
                }
                return null;
            }
        };
    }

    public static StringConverter<Backlog> backlogStringConverter(ObjectProperty<Organisation> organisationProperty) {
        return new StringConverter<Backlog>() {
            @Override
            public String toString(Backlog backlog) {
                return backlog != null ? backlog.getShortName() : "";
            }

            @Override
            public Backlog fromString(String shortName) {
                for (final Project project : organisationProperty.get().getProjects()) {
                    for (final Backlog backlog : project.getBacklogs()) {
                        if (backlog.getShortName().equals(shortName)) {
                            return backlog;
                        }
                    }
                }
                return null;
            }
        };
    }

    public static StringConverter<Release> releaseStringConverter(Organisation organisation) {
        return new StringConverter<Release>() {
            @Override
            public String toString(Release release) {
                return release != null ? release.getShortName() : "";
            }

            @Override
            public Release fromString(String shortName) {
                for (final Project project : organisation.getProjects()) {
                    for (final Release release : project.getReleases()) {
                        if (release.getShortName().equals(shortName)) {
                            return release;
                        }
                    }
                }
                return null;
            }
        };
    }

    public static StringConverter<Release> releaseStringConverter(ObjectProperty<Organisation> organisationProperty) {
        return new StringConverter<Release>() {
            @Override
            public String toString(Release release) {
                return release != null ? release.getShortName() : "";
            }

            @Override
            public Release fromString(String shortName) {
                for (final Project project : organisationProperty.get().getProjects()) {
                    for (final Release release : project.getReleases()) {
                        if (release.getShortName().equals(shortName)) {
                            return release;
                        }
                    }
                }
                return null;
            }
        };
    }
}
