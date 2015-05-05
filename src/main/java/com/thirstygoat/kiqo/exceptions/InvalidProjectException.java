package com.thirstygoat.kiqo.exceptions;

import com.thirstygoat.kiqo.Project;

/**
 * Created by bjk60 on 20/03/15.
 */
public class InvalidProjectException extends RuntimeException {
    public InvalidProjectException(Project project) {
        super("Project is invalid.");
    }
}
