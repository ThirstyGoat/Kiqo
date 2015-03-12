package seng302.group4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seng302.group4.Project;
import static org.junit.Assert.*;

public class ProjectTest {

    Project project1;

    @Before
    public void setUp() throws Exception {
        project1 = new Project("p1", "project1", "/home/");
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Given that the two projects have the same short name they will be equal
     *
     * @throws Exception
     */
    @Test
    public void testEquals() throws Exception {
        Project project2 = new Project("p1", "project1", "/home/");
        assertTrue("Project should be equal", project2.equals(project1));

        project2.setLongName("project2");
        assertTrue("Project should still be equal", project2.equals(project1));

        project2.setShortName("p2");
        assertFalse("Project should not be equal", project2.equals(project1));
    }

    @Test
    public void testHashCode() throws Exception {

    }
}