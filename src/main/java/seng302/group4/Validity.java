package seng302.group4;

import seng302.group4.exceptions.InvalidPersonException;
import seng302.group4.exceptions.InvalidProjectException;

import java.util.ArrayList;
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
     * Check that all the people in the People list are valid
     * @param people
     * @return
     * @throws Exception
     */
    public static void checkPeople(ArrayList<Person> people) throws InvalidPersonException {
        if (people.size() > 0) {
            for (int i=0; i < people.size(); i+=1){
                if(!(Validity.checkPersonValidity(people.get(i), people.subList(i + 1, people.size())))) {
                    throw new InvalidPersonException(people.get(i));
                }
            }
        }
    }

    /**
     * Checks all the required fields in project to make sure they are non-null
     * @param project
     * @return
     */
    public static boolean checkProject(Project project) throws InvalidProjectException{
        if (project.getShortName() == null || project.getLongName() == null) {
            throw new InvalidProjectException(project);
        }

        return true;
    }
}
