package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Status;
import com.thirstygoat.kiqo.model.Scale;

import javafx.util.StringConverter;

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
}
