package seng302.group4.utils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by leroy on 17/04/15.
 */
public class ApplicationInfo {
    // In order to print the name and version of the application as defined in the Maven pom file, we get the properties
    // stored in the 'project.properties' resource. Maven replaces the property references (denoted with ${})
    // with the values from the pom.xml file.
    private static String PROP_FILENAME = "project.properties";
    private static Properties properties = new Properties();

    public static String getProperty(String property) {
        try {
            properties.load(ApplicationInfo.class.getClassLoader().getResourceAsStream(PROP_FILENAME));
        } catch (IOException e) {
            System.out.println("Property file '" + PROP_FILENAME + "' not found in the classpath.");
            e.printStackTrace();
        }

        return properties.getProperty(property);
    }
}
