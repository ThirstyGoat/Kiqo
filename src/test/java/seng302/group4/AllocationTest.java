package seng302.group4;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by samschofield on 21/04/15.
 */
public class AllocationTest extends TestCase {

    /**
     * Tests that an allocations startdate must be before its end date
     */
    @Test
    public void testStartBeforeEnd() {
        Team t1 = new Team("t1", "a test team", null);

        // exception should be thrown if start date is after end date
        try {
            new Allocation(t1, LocalDate.now(), LocalDate.now().minusDays(1));
        } catch (RuntimeException e) {
            assertTrue(true);
        }

        // exception should not be thrown if start date and end date are the same
        try {
            new Allocation(t1, LocalDate.now(), LocalDate.now());
        } catch (RuntimeException e) {
            fail();
        }

        // exception should not be thrown if start date is before end date
        try {
            new Allocation(t1, LocalDate.now(), LocalDate.now().plusDays(1));
        } catch (RuntimeException e) {
            fail();
        }

    }
}