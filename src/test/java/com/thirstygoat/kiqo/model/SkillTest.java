package com.thirstygoat.kiqo.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.model.Skill;

public class SkillTest {

    Skill skill1;

    @Before
    public void setUp() throws Exception {
        skill1 = new Skill("skill1-shortname", "Description of the skill");
    }

    @Test
    public void testEquals() throws Exception {
        final Skill skill2 = new Skill("skill1-shortname", "Description of the skill");
        Assert.assertEquals("Skills should be equal", skill1, skill2);

        skill2.setDescription("Different description");
        Assert.assertEquals("Skills should be equal", skill1, skill2);
    }

    @Test
    public void testEqualsShortName() throws Exception {
        final Skill skill2 = new Skill("different shortname", "Description of the skill");
        Assert.assertNotEquals("Skills should not be equal", skill1, skill2);
    }
}
