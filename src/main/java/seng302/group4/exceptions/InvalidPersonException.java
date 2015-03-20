package seng302.group4.exceptions;

import seng302.group4.Person;

/**
 * Created by samschofield on 20/03/15.
 */
public class InvalidPersonException extends RuntimeException {

    public InvalidPersonException(Person person) {
        super("Person: " + person + " is invalid");
    }
}
