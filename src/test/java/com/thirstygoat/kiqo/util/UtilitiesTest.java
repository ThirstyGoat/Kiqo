package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Skill;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;


/**
 * Created by samschofield on 17/07/15.
 */
public class UtilitiesTest {

    @Test
    public void testCreateSortedList() throws Exception {
        ObservableList<Item> monkeySkills = FXCollections.observableArrayList(new Skill("Swinging", ""),
                new Skill("Grooming", ""), new Skill("Poo Flinging", ""));

        List<Item> sorted1 = Utilities.createSortedList(monkeySkills);
        Assert.assertTrue(sorted1.get(0).getShortName().equals("Grooming"));
        Assert.assertTrue(sorted1.get(1).getShortName().equals("Poo Flinging"));
        Assert.assertTrue(sorted1.get(2).getShortName().equals("Swinging"));

        // Sorting should be case insensitive
        monkeySkills.add(new Skill("cymbal banging", ""));
        monkeySkills.add(new Skill("wallet stealing", ""));
        List<Item> sorted2 = Utilities.createSortedList(monkeySkills);
        Assert.assertTrue(sorted2.get(0).getShortName().equals("cymbal banging"));
        Assert.assertTrue(sorted2.get(sorted2.size() - 1).getShortName().equals("wallet stealing"));

        // Sort numbers and special characters
        monkeySkills.add(new Skill("1337 HaX0ring 5ki11z", ""));
        monkeySkills.add(new Skill("__UNDERSCORE__", ""));
        monkeySkills.add(new Skill("            white           space ", ""));
        monkeySkills.add(new Skill("**********//////////", ""));
        List<Item> sorted3 = Utilities.createSortedList(monkeySkills);
        Assert.assertTrue(sorted3.get(0).getShortName().equals("            white           space "));
        Assert.assertTrue(sorted3.get(1).getShortName().equals("**********//////////"));
        Assert.assertTrue(sorted3.get(2).getShortName().equals("1337 HaX0ring 5ki11z"));
        Assert.assertTrue(sorted3.get(3).getShortName().equals("__UNDERSCORE__"));
    }

    @Test
    public void testConcatenateItemList() throws Exception {
        Skill skill1 = new Skill("skill1", "");
        Skill skill2 = new Skill("skill2", "");
        Skill skill3 = new Skill("skill3", "");

        String emptyListMessage = "Listing for an empty list should return the empty string.";
        List<Item> itemsEmpty = Arrays.asList();
        Assert.assertTrue(emptyListMessage, Utilities.concatenateItemsList(itemsEmpty, 1).equals(""));
        Assert.assertTrue(emptyListMessage, Utilities.concatenateItemsList(itemsEmpty, 0).equals(""));
        Assert.assertTrue(emptyListMessage, Utilities.concatenateItemsList(itemsEmpty, -1).equals(""));

        String singleItemMessage = "Listing for a single item should return just that item, unless max is <= 0"
                + " in which case it should return \" and 1 other\".";
        List<Item> itemsSingle = Arrays.asList(skill1);
        Assert.assertTrue(singleItemMessage, Utilities.concatenateItemsList(itemsSingle, 1).equals("skill1"));
        Assert.assertTrue(singleItemMessage, Utilities.concatenateItemsList(itemsSingle, 0).equals(", and 1 other"));
        Assert.assertTrue(singleItemMessage, Utilities.concatenateItemsList(itemsSingle, -1).equals(", and 1 other"));

        List<Item> itemsMulti = Arrays.asList(skill1, skill2, skill3);
        Assert.assertTrue(Utilities.concatenateItemsList(itemsMulti, 3).equals("skill1, skill2, skill3"));
        Assert.assertTrue(Utilities.concatenateItemsList(itemsMulti, 2).equals("skill1, skill2, and 1 other"));
        Assert.assertTrue(Utilities.concatenateItemsList(itemsMulti, 1).equals("skill1, and 2 others"));
        Assert.assertTrue(Utilities.concatenateItemsList(itemsMulti, 0).equals(", and 3 others"));
        Assert.assertTrue(Utilities.concatenateItemsList(itemsMulti, -1).equals(", and 3 others"));
    }

    @Test
    public void testPluralise() throws Exception {
        // Test with zero monkeys.
        String string0 = Utilities.pluralise(0, "Monkey", "Monkeys");
        Assert.assertTrue("Should be plural.", string0.equals("Monkeys"));

        // Test with one monkey.
        String string1 = Utilities.pluralise(1, "Monkey", "Monkeys");
        Assert.assertTrue("Should be singular.", string1.equals("Monkey"));

        // Test with more than one monkey.
        String string2 = Utilities.pluralise(2, "Monkey", "Monkeys");
        Assert.assertTrue("Should be plural.", string2.equals("Monkeys"));

        // Test with an large prime number of monkeys. Also covers the case of an odd
        // number of Monkeys, and ensures no sneaky modulo stuff is going on.
        String stringPrime = Utilities.pluralise(10037, "Monkey", "Monkeys");
        Assert.assertTrue("Should be plural.", stringPrime.equals("Monkeys"));

        // Test with negative monkeys.
        String stringNeg2 = Utilities.pluralise(-2, "Monkey", "Monkeys");
        Assert.assertTrue("Should be plural.", stringNeg2.equals("Monkeys"));
        String stringNegPrime = Utilities.pluralise(-10037, "Monkey", "Monkeys");
        Assert.assertTrue("Should be plural.", stringNegPrime.equals("Monkeys"));
    }

    @Test
    public void testCommaSeparatedValues() throws Exception {
        List threeWiseMonkeys = Arrays.asList(new Skill("see no evil", ""), new Skill("hear no evil", ""), new Skill("speak no evil", ""));
        String proverb = Utilities.commaSeparatedValues(threeWiseMonkeys);
        Assert.assertTrue(proverb.equals("see no evil, hear no evil, speak no evil"));

        List oneValue = Arrays.asList(new Skill("Banana", ""));
        String string = Utilities.commaSeparatedValues(oneValue);
        Assert.assertTrue("Should return a single value with no commas because there are no other values seperate.",
                string.equals("Banana"));

        List emptyList = Arrays.asList();
        String emptyString = Utilities.commaSeparatedValues(emptyList);
        Assert.assertTrue("Should return the empty string.", emptyString.equals(""));
    }

    @Test
    public void testCommaSeparatedValuesProperty() throws Exception {

    }

    @Test
    public void testShortnameIsUniqueMultiple() throws Exception {

    }

    @Test
    public void testShortNameIsUnique() throws Exception {
        // Person should really be a subclass of Primate. Oh well.
        class Primate extends Person {
            Primate (String shortName) {
                this.setShortName(shortName);
            }
        }

        List<Primate> fictionalPrimates = new LinkedList<>(Arrays.asList(new Primate("Bingo"), new Primate("Bubbles"),
                new Primate("Koko"), new Primate("Rafiki"), new Primate("Mighty Joe Young")));
        Primate uniquelyNamed = new Primate("Professor Bobo");
        Primate nonUniquelyNamed = new Primate("Rafiki");

        Assert.assertTrue(Utilities.shortnameIsUnique(uniquelyNamed.getShortName(), uniquelyNamed, fictionalPrimates));
        Assert.assertFalse(Utilities.shortnameIsUnique(nonUniquelyNamed.getShortName(), nonUniquelyNamed,fictionalPrimates));

        // Now try checking uniqueness when item is already in the Collection.
        fictionalPrimates.add(uniquelyNamed);
        Assert.assertTrue("The unique name of an object should still be unique even if it is the same as itself.",
                Utilities.shortnameIsUnique(uniquelyNamed.getShortName(), uniquelyNamed, fictionalPrimates));
    }

    @Test
    public void testSetNameSuggester() throws Exception {

    }

    @Test
    public void testStripExtension() throws Exception {
        String filename = "banana_recipes.tar.gz";

        String strip1 = Utilities.stripExtension(filename);
        Assert.assertTrue(strip1.equals("banana_recipes.tar"));

        String strip2 = Utilities.stripExtension(strip1);
        Assert.assertTrue(strip2.equals("banana_recipes"));

        String strip3 = Utilities.stripExtension(strip2);
        Assert.assertTrue(strip3.equals("banana_recipes"));
    }

    @Test
    public void testIsPersonPoOfBacklog() throws Exception {

    }

    @Test
    public void testEmptyValidation() {
        Predicate<String> predicate = Utilities.emptinessPredicate();

        Assert.assertFalse("Initially should be null.", predicate.test(null));
        Assert.assertTrue("Valid predicate, this shouldn't fail", predicate.test("has value"));
        Assert.assertFalse("Should be null.", predicate.test(null));

    }
}
