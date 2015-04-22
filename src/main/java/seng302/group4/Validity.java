package seng302.group4;

import java.util.List;

import seng302.group4.exceptions.InvalidPersonException;
import seng302.group4.exceptions.InvalidProjectException;

/**
 * Created by blair_000 on 20/03/2015.
 */
public class Validity {
    public static boolean checkPersonValidity(Person person, List<Person> people) {
        for (final Person p : people) {
            if (p.getShortName().equals(person.getShortName())) {
               return false;
            }
        }
        return true;
    }

    /**
     * Check that all the people in the People list are valid
     *
     * @param people List of Person objects to validate
     * @throws InvalidPersonException if one or more Person objects are invalid
     */
    public static void checkPeople(List<Person> people) throws InvalidPersonException {
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
     *
     * @param organisation Project to be validated
     * @return true if all fields are valid
     * @throws InvalidProjectException if one or more fields are invalid
     */
    public static boolean checkProject(Organisation organisation) throws InvalidProjectException{
        if (organisation.getShortName() == null || organisation.getLongName() == null) {
            throw new InvalidProjectException(organisation);
        }

        return true;
    }
}
