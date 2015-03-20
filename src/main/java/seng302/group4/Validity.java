package seng302.group4;

import seng302.group4.exceptions.InvalidProjectException;

import java.util.List;

/**
 * Created by blair_000 on 20/03/2015.
 */
public class Validity {
    public static boolean checkPersonValidity(Person person, List<Person> people) {
        for (Person p : people) {
            if (p.getShortName().equals(person.getShortName())) {
               return false;
            }
        }
        return true;
    }

    /**
     * Checks all the required fields in project to make sure they are non-null
     * @param project
     * @return
     */
    public static boolean checkProject(Project project) throws InvalidProjectException{
        if (project.getShortName() == null || project.getLongName() == null || project.getSaveLocation() == null) {
            throw new InvalidProjectException(project);
        }

        return true;
    }
}
