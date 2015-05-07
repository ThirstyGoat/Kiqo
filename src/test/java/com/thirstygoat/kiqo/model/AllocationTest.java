package com.thirstygoat.kiqo.model;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by samschofield on 21/04/15.
 */
public class AllocationTest {

    /**
     * Tests that an allocations startdate must be before its end date
     */
    @Test
    public void testStartBeforeEnd() {
        final Project p1 = new Project("short", "long");
        final Team t1 = new Team("t1", "a test team", new ArrayList<>());

        // exception should be thrown if start date is after end date
        try {
            new Allocation(t1, LocalDate.now(), LocalDate.now().minusDays(1), p1);
        } catch (final RuntimeException e) {
            Assert.assertTrue(true);
        }

        // exception should not be thrown if start date and end date are the same
        try {
            new Allocation(t1, LocalDate.now(), LocalDate.now(), p1);
        } catch (final RuntimeException e) {
            Assert.fail();
        }

        // exception should not be thrown if start date is before end date
        try {
            new Allocation(t1, LocalDate.now(), LocalDate.now().plusDays(1), p1);
        } catch (final RuntimeException e) {
            Assert.fail();
        }

    }
}
