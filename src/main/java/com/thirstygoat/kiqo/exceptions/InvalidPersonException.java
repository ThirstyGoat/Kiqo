package com.thirstygoat.kiqo.exceptions;

import com.thirstygoat.kiqo.Person;

/**
 * Created by samschofield on 20/03/15.
 */
public class InvalidPersonException extends RuntimeException {

    public InvalidPersonException(Person person) {
        super("Person: " + person + " is invalid");
    }
}
