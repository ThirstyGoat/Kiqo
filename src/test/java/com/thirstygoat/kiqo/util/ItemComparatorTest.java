package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import static org.junit.Assert.*;

/**
 * Created by samschofield on 20/05/15.
 */
public class ItemComparatorTest {

    @Test
    public void testCompare() throws Exception {
        Comparator<Person> comparator = new ItemComparator<>();

        Person p1 = new Person();
        p1.setShortName("aaaa");

        Person p2 = new Person();
        p2.setShortName("B");

        Person p3 = new Person();
        p3.setShortName("b");

        // aaaa before b
        Assert.assertTrue(comparator.compare(p1, p2) < 0);

        // test case insensitive
        Assert.assertTrue(comparator.compare(p3, p2) == 0);

        Assert.assertTrue(comparator.compare(p2, p1) > 0);
    }

    @Test
    public void testOnList() throws Exception {
        final ObservableList<Person> unsortedPeople = FXCollections.observableArrayList();

        Person p1 = new Person();
        Person p2 = new Person();
        Person p3 = new Person();
        Person p4 = new Person();
        Person p5 = new Person();
        Person p6 = new Person();

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
        unsortedPeople.sort(new ItemComparator<>());
        Assert.assertEquals(unsortedPeople, sortedPeople);
    }

}