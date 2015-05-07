package com.thirstygoat.kiqo.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.model.Project;

public class ProjectTest {

    Project project1;

    @Before
    public void setUp() throws Exception {
        project1 = new Project("p1", "project1");
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Given that the two projects have the same short name they will be equal
     *
     * @throws Exception Exception
     */
    @Test
    public void testEquals() throws Exception {
        final Project project = new Project("p1", "project1");
        Assert.assertTrue("Project should be equal", project.equals(project1));

        project.setLongName("project2");
        Assert.assertTrue("Project should still be equal", project.equals(project1));

        project.setShortName("p2");
        Assert.assertFalse("Project should not be equal", project.equals(project1));
    }

    @Test
    public void testHashCode() throws Exception {

    }
}