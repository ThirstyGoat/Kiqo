package com.thirstygoat.kiqo.util;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by leroy on 17/04/15.
 */
public class ApplicationInfo {
    private static final Logger LOGGER = Logger.getLogger(ApplicationInfo.class.getName());
    // In order to print the name and version of the application as defined in the Maven pom file, we get the properties
    // stored in the 'project.properties' resource. Maven replaces the property references (denoted with ${})
    // with the values from the pom.xml file.
    private static String PROP_FILENAME = "project.properties";
    private static Properties properties = new Properties();

    public static String getProperty(String property) {
        try {
            ApplicationInfo.properties.load(ApplicationInfo.class.getClassLoader().getResourceAsStream(ApplicationInfo.PROP_FILENAME));
        } catch (final IOException e) {
            ApplicationInfo.LOGGER.log(Level.SEVERE, "Property file '%s' not found in the classpath.", ApplicationInfo.PROP_FILENAME);
        }

        return ApplicationInfo.properties.getProperty(property);
    }
}
