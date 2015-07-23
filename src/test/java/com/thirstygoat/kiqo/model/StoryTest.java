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
        Task t2 = new Task("b", "", (float) 2);
        Task t3 = new Task("c", "", (float) 3);
        Task t4 = new Task("e", "", (float) 4);


        s.observableTasks();
    }
}
