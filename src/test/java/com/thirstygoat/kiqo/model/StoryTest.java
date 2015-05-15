package com.thirstygoat.kiqo.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by leroy on 15/05/15.
 */
public class StoryTest {
    Story story1;
    Story story2;
    Person creator1 = new Person();
    Person creator2 = new Person();
    Project project1 = new Project();

    @Before
    public void setUp() throws Exception {
        story1 = new Story("Story 1", "First story", "The first story to test", creator1, project1, 0);
        story2 = new Story("Story 2", "Second story", "The second story to test", creator2, project1, 0);
    }

    @Test
    public void testNotEquals() throws Exception {
        Assert.assertNotEquals("Stories should not be equal", story1, story2);

    }

    @Test
    public void testEquals() throws Exception {
        story2.setShortName("Story 1");
        Assert.assertEquals("Stories should be equal", story1, story2);
    }
}
