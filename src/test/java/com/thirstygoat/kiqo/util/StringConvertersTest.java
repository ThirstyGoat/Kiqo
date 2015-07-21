package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Skill;
import javafx.util.StringConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by leroy on 21/07/15.
 */
public class StringConvertersTest {

    Organisation organisation;

    @Before
    public void setUp() {
        organisation = new Organisation();
    }

    @Test
    public void testPersonStringConverter() {
        Person personInOrganisation = new Person("inny", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        organisation.getPeople().add(personInOrganisation);
        Person personNotInOrganisation = new Person("outy", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        StringConverter personStringConverter = StringConverters.personStringConverter(organisation);

        // Test from string method
        Assert.assertEquals("Empty string should return null", null,
                personStringConverter.fromString(""));
        Assert.assertEquals("String of non-existing person should return null", null,
                personStringConverter.fromString("non-exsting person shortName"));
        Assert.assertEquals("String of person not in organisation should return null", null,
                personStringConverter.fromString("outy"));
        Assert.assertEquals("String of person in organisation sohuld return that person", personInOrganisation,
                personStringConverter.fromString("inny"));

        // Test to string method
        Assert.assertEquals("Person in organisation should return the short name of that person", "inny",
                personStringConverter.toString(personInOrganisation));
        Assert.assertEquals("Person not in organisation should return the short name of that person", "outy",
                personStringConverter.toString(personNotInOrganisation));
        Assert.assertEquals("Null person should return empty string", "",
                personStringConverter.toString(null));
    }

    @Test
    public void testProjectStringConverter() {
        Project projectInOrganisation = new Project("inny", "longName");
        organisation.getProjects().add(projectInOrganisation);
        Project projectNotInOrganisation = new Project("outy", "longName");
        StringConverter projectStringConverter = StringConverters.projectStringConverter(organisation);

        // Test from string method
        Assert.assertEquals("Empty string should return null", null,
                projectStringConverter.fromString(""));
        Assert.assertEquals("String of non-existing project should return null", null,
                projectStringConverter.fromString("non-exsting project shortName"));
        Assert.assertEquals("String of project not in organisation should return null", null,
                projectStringConverter.fromString("outy"));
        Assert.assertEquals("Sring of project in organisation sohuld return that project", projectInOrganisation,
                projectStringConverter.fromString("inny"));

        // Test to string method
        Assert.assertEquals("Project in organisation should return the short name of that project", "inny",
                projectStringConverter.toString(projectInOrganisation));
        Assert.assertEquals("Project not in organisation should return the short name of that project", "outy",
                projectStringConverter.toString(projectNotInOrganisation));
        Assert.assertEquals("Null project should return empty string", "",
                projectStringConverter.toString(null));

    }
}
