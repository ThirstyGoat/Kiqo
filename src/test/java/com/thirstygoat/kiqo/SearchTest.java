package com.thirstygoat.kiqo;

import com.thirstygoat.kiqo.search.AdvancedSearch;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.create.CreatePersonCommand;
import com.thirstygoat.kiqo.command.create.CreateSkillCommand;
import com.thirstygoat.kiqo.command.delete.DeleteSkillCommand;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.search.Search;
import com.thirstygoat.kiqo.search.SearchableItems;

/**
 * Created by james on 25/07/15.
 */
public class SearchTest {

    private Organisation organisation;
    private Skill skill1;

    @Before
    public void setUp() {
        SearchableItems.getInstance().clear();
        
        skill1 = new Skill("Skill1", "des");
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
    public void testMultipleReferences() {
        Person person1 = new Person();
        person1.setShortName("Skill1");
        new CreatePersonCommand(person1, organisation).execute();
        
        Search search = new Search("Skill1");
        Assert.assertEquals("Should find all occurrences in model", 2, search.execute().size());
    }
    
    @Test
    public void testPartialMatching() {
        Search search = new Search("l");
        Assert.assertEquals("Should find partial matches", 3, search.execute().size());
    }
    
    @Test
    public void testDeletedSearchablesAreNotIncluded() {
        Command command = new DeleteSkillCommand(skill1, organisation);
        
        Search search = new Search("Skill1");
        
        Assert.assertEquals("Should find not-yet-deleted item", 1, search.execute().size());
        command.execute();
        Assert.assertEquals("Should not find deleted item", 0, search.execute().size());
        command.undo();
        Assert.assertEquals("Should find un-deleted item", 1, search.execute().size());
    }

    @Test
     public void testStarRegexSearch() {
        AdvancedSearch search = new AdvancedSearch("*", SearchableItems.SCOPE.ORGANISATION);
        search.setRegexEnabled(true);
        Assert.assertEquals("no exception should be thrown", 0, search.execute().size());

        AdvancedSearch search1 = new AdvancedSearch(".*", SearchableItems.SCOPE.ORGANISATION);
        search1.setRegexEnabled(true);
        Assert.assertEquals(".* should return all fields", 3, search1.execute().size());

        AdvancedSearch search2 = new AdvancedSearch("***", SearchableItems.SCOPE.ORGANISATION);
        search2.setRegexEnabled(true);
        Assert.assertEquals("no exception should be thrown", 0, search2.execute().size());
    }

    @Test
    public void testPlusRegexSearch() {
        AdvancedSearch search = new AdvancedSearch("+", SearchableItems.SCOPE.ORGANISATION);
        search.setRegexEnabled(true);
        Assert.assertEquals("no exception should be thrown", 0, search.execute().size());

        AdvancedSearch search1 = new AdvancedSearch(".+", SearchableItems.SCOPE.ORGANISATION);
        search1.setRegexEnabled(true);
        Assert.assertEquals(".+ should return all fields", 3, search1.execute().size());

        AdvancedSearch search2 = new AdvancedSearch("+++", SearchableItems.SCOPE.ORGANISATION);
        search2.setRegexEnabled(true);
        Assert.assertEquals("no exception should be thrown", 0, search2.execute().size());

        AdvancedSearch search3 = new AdvancedSearch("+*", SearchableItems.SCOPE.ORGANISATION);
        search3.setRegexEnabled(true);
        Assert.assertEquals("no exception should be thrown", 0, search3.execute().size());

        AdvancedSearch search4 = new AdvancedSearch("*+", SearchableItems.SCOPE.ORGANISATION);
        search4.setRegexEnabled(true);
        Assert.assertEquals("no exception should be thrown", 0, search4.execute().size());
    }
}