package com.thirstygoat.kiqo.util;

import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.junit.Assert;
import org.junit.Test;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Person;

/**
 * Created by samschofield on 20/05/15.
 */
public class ItemComparatorTest {

    static final Comparator<Item> comparator = Utilities.LEXICAL_COMPARATOR;

    @Test
    public void testCompare() throws Exception {

        final Person p1 = new Person();
        p1.setShortName("aaaa");

        final Person p2 = new Person();
        p2.setShortName("B");

        final Person p3 = new Person();
        p3.setShortName("b");

        // aaaa before b
        Assert.assertTrue(ItemComparatorTest.comparator.compare(p1, p2) < 0);

        // test case insensitive
        Assert.assertTrue(ItemComparatorTest.comparator.compare(p3, p2) == 0);

        Assert.assertTrue(ItemComparatorTest.comparator.compare(p2, p1) > 0);
    }

    @Test
    public void testOnList() throws Exception {
        final ObservableList<Person> unsortedPeople = FXCollections.observableArrayList();

        final Person p1 = new Person();
        final Person p2 = new Person();
        final Person p3 = new Person();
        final Person p4 = new Person();
        final Person p5 = new Person();
        final Person p6 = new Person();

        p1.setShortName("aaaa");
        p2.setShortName("b");
        p3.setShortName("c");
        p4.setShortName("d");
        p5.setShortName("ee");
        p6.setShortName("f");

        unsortedPeople.addAll(p6, p2, p3, p1, p4, p5);

        final ObservableList<Person> sortedPeople = FXCollections.observableArrayList();
        sortedPeople.addAll(p1, p2, p3, p4, p5, p6);

        // check that that the unsorted and sorted lists are not equal
        Assert.assertNotEquals(unsortedPeople, sortedPeople);

        // check that after sorting the sorted and unsorted list are equal
        unsortedPeople.sort(ItemComparatorTest.comparator);
        Assert.assertEquals(unsortedPeople, sortedPeople);
    }

}