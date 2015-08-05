package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.command.CreateCommand;
import com.thirstygoat.kiqo.command.CreatePersonCommand;
import com.thirstygoat.kiqo.command.CreateSkillCommand;
import com.thirstygoat.kiqo.search.Search;
import com.thirstygoat.kiqo.search.SearchableItems;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by james on 25/07/15.
 */
public class SearchTest {

    private Organisation organisation;

    @Before
    public void setUp() {
        SearchableItems.getInstance().clear();
        
        Skill skill1 = new Skill("Skill1", "des");
        Skill skill2 = new Skill("Skill2", "des");
        Skill skill3 = new Skill("Skill3", "des");
        organisation = new Organisation();

        new CreateSkillCommand(skill1, organisation).execute();
        new CreateSkillCommand(skill2, organisation).execute();
        new CreateSkillCommand(skill3, organisation).execute();
    }

    @Test
    public void testExecute() throws Exception {
        Search search = new Search("Skill1");

        Assert.assertEquals("Should find a match", 1, search.execute().size());
    }

    @Test
    public void testExecuteOnTwo() {
        Person person1 = new Person();
        person1.setShortName("Skill1");
        new CreatePersonCommand(person1, organisation).execute();
        
        Search search = new Search("Skill1");
        Assert.assertEquals("Should find all occurrences in model", 2, search.execute().size());
    }
    
    @Test
    public void testPartialMatching() {
        Search search = new Search("ill");
        Assert.assertEquals("Should find partial matches", 3, search.execute().size());
    }
}