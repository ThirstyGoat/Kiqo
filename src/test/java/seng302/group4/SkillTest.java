package seng302.group4;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SkillTest {

    Skill skill1;

    @Before
    public void setUp() throws Exception {
        skill1 = new Skill("skill1-shortname", "Description of the skill");
    }

    @Test
    public void testEquals() throws Exception {
        Skill skill2 = new Skill("skill1-shortname", "Description of the skill");
        assertTrue("Skills should be equal", skill2.equals(skill1));

        skill2.setDescription("Different description");
        assertFalse("Skills should not be equal", skill2.equals(skill1));
    }

    @Test
    public void testEqualsShortName() throws Exception {
        Skill skill2 = new Skill("different shortname", "Description of the skill");
        assertFalse("Skills should not be equal", skill2.equals(skill1));
    }
}