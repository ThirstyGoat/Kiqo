package com.thirstygoat.kiqo.model;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by samschofield on 21/04/15.
 */
public class StoryTest {

    @Test
    public void testTasksTotalHoursUpdated() {
        Story s = new Story();
        Task t1 = new Task("a", "", (float) 1);
        Task t2 = new Task("b", "", (float) 1);
        Task t3 = new Task("c", "", (float) 1);
        Task t4 = new Task("e", "", (float) 1);

        // Check initial value
        s.observableTasks().addAll(t1, t2, t3, t4);
        Assert.assertTrue(s.taskHoursProperty().get() == 4);

        // Check if we add a task
        Task t5 = new Task("e", "", (float) 4);
        s.observableTasks().addAll(t5);
        Assert.assertTrue(s.taskHoursProperty().get() == 8);

        // Check if we remove a task
        s.observableTasks().remove(0);
        Assert.assertTrue(s.taskHoursProperty().get() == 7);

        // Check if we edit a task
        s.observableTasks().get(0).estimateProperty().setValue(4);
        Assert.assertTrue(s.taskHoursProperty().get() == 10);
    }
}
