//package seng302.group4;
//
//import java.io.File;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//public class OrganisationTest {
//
//    Organisation organisation1;
//
//    @Before
//    public void setUp() throws Exception {
//        organisation1 = new Organisation("p1", "project1", new File(""));
//    }
//
//    @After
//    public void tearDown() throws Exception {
//
//    }
//
//    /**
//     * Given that the two projects have the same short name they will be equal
//     *
//     * @throws Exception Exception
//     */
//    @Test
//    public void testEquals() throws Exception {
//        final Organisation organisation2 = new Organisation("p1", "project1", new File(""));
//        Assert.assertTrue("Project should be equal", organisation2.equals(organisation1));
//
//        organisation2.setLongName("project2");
//        Assert.assertTrue("Project should still be equal", organisation2.equals(organisation1));
//
//        organisation2.setShortName("p2");
//        Assert.assertFalse("Project should not be equal", organisation2.equals(organisation1));
//    }
//
//    @Test
//    public void testHashCode() throws Exception {
//
//    }
//}