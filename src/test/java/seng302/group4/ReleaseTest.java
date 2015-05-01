package seng302.group4;

import java.io.File;
import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReleaseTest {

    final LocalDate date = LocalDate.now();
    Project project1;
    Release release1;

    @Before
    public void setUp() throws Exception {
        project1 = new Project("p1", "project1");
        release1 = new Release("release1", project1, date, "Description of the release", new Organisation(new File("")));
    }

    @Test
    public void testEquals() throws Exception {
        final Release release2 = new Release("release1", project1, date, "Description of the release", new Organisation(new File("")));
        Assert.assertTrue("Releases should be equal", release2.equals(release1));
    }

    @Test
    public void testEqualsId() throws Exception {
        final Release release2 = new Release("differentId", project1, date, "Description of the release", new Organisation(new File("")));
        Assert.assertFalse("Releases should not be equal", release2.equals(release1));
    }
}
