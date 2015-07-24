package com.thirstygoat.kiqo.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by james on 25/07/15.
 */
public class SearchTest {

    @Before
    public void setUp() {
        SearchableItems.clear();
        Skill skill1 = new Skill("Skill1", "des");
        Skill skill12 = new Skill("Skill2", "des");
        Skill skill3 = new Skill("Skill3", "des");
    }



    @Test
    public void testExecute() throws Exception {
        Search search = new Search("Skill1");

        Assert.assertTrue("should find 1 match", search.execute().size() == 1);
    }

    @Test
    public void testExecuteOnTwo() {
        Person person1 = new Person();
        person1.setShortName("Skill1");
        Search search = new Search("Skill1");
        Assert.assertTrue("Should find 2", search.execute().size() == 2);
    }
}