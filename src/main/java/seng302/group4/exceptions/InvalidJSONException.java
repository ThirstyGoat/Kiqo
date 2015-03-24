package seng302.group4.exceptions;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by samschofield on 20/03/15.
 */
public class InvalidJSONException extends FileNotFoundException {

        public InvalidJSONException(File file) {
            super("JSON File: " + file + " is corrupt");
        }
}
