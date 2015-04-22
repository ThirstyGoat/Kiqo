package seng302.group4.exceptions;

import seng302.group4.Organisation;

/**
 * Created by bjk60 on 20/03/15.
 */
public class InvalidProjectException extends RuntimeException {
    public InvalidProjectException(Organisation organisation) {
        super("Project is invalid.");
    }
}
