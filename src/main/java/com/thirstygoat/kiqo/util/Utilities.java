package com.thirstygoat.kiqo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Person;
import javafx.scene.control.TextField;

/**
 * Created by bradley on 9/04/15.
 */
public class Utilities {

    public static String concatenatePeopleList(List<Person> people, int max) {
        String list = "";
        for (int i = 0; i < Math.min(people.size(), max)-1; i++) {
            list += people.get(i).getShortName() + ", ";
        }
        list += people.get(Math.min(people.size(), max)-1).getShortName();
        if (Math.min(people.size(), max) < people.size()) {
            final int remaining = people.size() - max;
            final String others = (remaining % 2 == 0) ? "others" : "other";
            list += " and " + remaining + " " + others;
        }

        return list;
    }

    public static String commaSeparatedValues(List<? extends Item> list) {
        String concatenatedString = "";

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                concatenatedString += list.get(i).getShortName();
                if (i != list.size() - 1) {
                    concatenatedString += ", ";
                }
            }
        }

        return concatenatedString;
    }

    public static StringProperty commaSeparatedValuesProperty(ObservableList<? extends Item> list) {
        StringProperty result = new SimpleStringProperty();
        result.set(commaSeparatedValues(list));

        // Add listeners on each of the string properties within the observable list
        final ChangeListener<String> stringChangeListener =
                (observable, oldValue, newValue) -> result.set(commaSeparatedValues(list));

        for (Item item : list) {
            item.shortNameProperty().addListener(stringChangeListener);
        }

        list.addListener((ListChangeListener<Item>) c -> {
            c.next();
            // Remove shortNameProperty listeners on each of items removed from the list
            for (Item item : c.getRemoved()) {
                item.shortNameProperty().removeListener(stringChangeListener);
            }

            // Add shortNameProperty listener for each of the items added to the list
            for (Item item : c.getAddedSubList()) {
                item.shortNameProperty().addListener(stringChangeListener);
            }

            result.set(commaSeparatedValues(list));
        });

        return result;
    }

    /**
     * Checks whether the given shortname is unique among the given Collection.
     * @param shortName Short Name to be checked
     * @param items items among which the name must be unique
     * @return shortName is unique among items
     */
    public static boolean shortnameIsUnique(String shortName, Collection<? extends Item> items) {
        // copy all the shortnames into a new list
        final Collection<String> list = new ArrayList<>();
        list.addAll(items.stream().map(Item::getShortName).collect(Collectors.toList()));

        // now for the actual check
        return !list.contains(shortName);
    }

    /**
     * Checks whether the given shortname is unique among the given Collection.
     * @param shortName Short Name to be checked
     * @param item Item being changed
     * @param items items among which the name must be unique
     * @return shortName is unique among items
     */
    public static boolean shortnameIsUnique(String shortName, Item item, Collection<? extends Item> items) {
        for (Item i : items) {
            if (item != null && i == item) {
                continue;
            }
            if (i.getShortName().equals(shortName)) {
                return false;
            }
        }
        new Object();
        return true;
    }

    /**
     * Sets up the listener for changes in the source name, that the the target name can be populated with a suggestion
     * @param source the source name
     * @param target the target name
     * @param suggestedLength the maximum length for a suggestion
     * @param targetModified whether or not the target was modified
     */
    public static void setNameSuggester(TextField source, TextField target, int suggestedLength,
                                        BooleanProperty targetModified) {
        source.textProperty().addListener((observable, oldValue, newValue) -> {
            final String suggestedName = newValue.substring(0, Math.min(newValue.length(),
                    suggestedLength));
            if (!targetModified.get()) {
                target.setText(suggestedName);
            }
        });
    }

    /**
     * Strips file extension from a file name
     * @param line
     * @return file name with extension stripped
     */
    public static String stripExtension(String line) {
        int index = line.lastIndexOf('.');
        if (index > 0) {
            return line.substring(0, index);
        }
        return line;
    }
}
