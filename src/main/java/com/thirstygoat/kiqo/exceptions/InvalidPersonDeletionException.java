package com.thirstygoat.kiqo.exceptions;

import com.thirstygoat.kiqo.model.Person;

/**
 * Created by Bradley on 29/05/15.
 */
public class InvalidPersonDeletionException extends Exception {

        public InvalidPersonDeletionException(Person person) {
            super(person + " is currently PO of one or more backlogs.");
        }
}