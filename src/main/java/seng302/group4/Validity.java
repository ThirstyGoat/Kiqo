package seng302.group4;

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
}
