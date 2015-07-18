package com.thirstygoat.kiqo.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.thirstygoat.kiqo.model.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

import javafx.scene.control.TextField;

/**
 * Created by bradley on 9/04/15.
 */
public class Utilities {
    public static final Comparator<Item> LEXICAL_COMPARATOR = (item1, item2) -> {
        return item1.getShortName().compareToIgnoreCase(item2.getShortName());
    };

    public static <E extends Item> SortedList<E> createSortedList(ObservableList<E> list) {
        return list.sorted((item1, item2) -> {return item1.getShortName().compareToIgnoreCase(item2.getShortName()); });
    }

    public static String concatenateItemsList(List<? extends Item> people, int max) {

        String list = "";
        for (int i = 0; i < Math.min(people.size(), max)-1; i++) {
            list += people.get(i).getShortName() + ", ";
        }
        list += people.get(Math.min(people.size(), max)-1).getShortName();
        if (Math.min(people.size(), max) < people.size()) {
            final int remaining = people.size() - max;
            final String others = pluralise(remaining, "other", "others");
            list += " and " + remaining + " " + others;
        }
        return list;
    }

    public static String pluralise(int count, String singular, String plural) {
        return (count == 1) ? singular : plural;
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
        final StringProperty result = new SimpleStringProperty();
        result.set(Utilities.commaSeparatedValues(list));

        // Add listeners on each of the string properties within the observable list
        final ChangeListener<String> stringChangeListener =
                (observable, oldValue, newValue) -> result.set(Utilities.commaSeparatedValues(list));

        for (final Item item : list) {
            item.shortNameProperty().addListener(stringChangeListener);
        }

        list.addListener((ListChangeListener<Item>) c -> {
            c.next();
            // Remove shortNameProperty listeners on each of items removed from the list
            for (final Item item : c.getRemoved()) {
                item.shortNameProperty().removeListener(stringChangeListener);
            }

            // Add shortNameProperty listener for each of the items added to the list
            for (final Item item : c.getAddedSubList()) {
                item.shortNameProperty().addListener(stringChangeListener);
            }

            result.set(Utilities.commaSeparatedValues(list));
        });

        return result;
    }

    /**
     * Convenience method for checking short name uniqueness across multiple collections
     * @param shortName
     * @param item
     * @param collections
     * @return
     */
    public static boolean shortnameIsUniqueMultiple(String shortName, Item item, Collection<Collection<? extends Item>> collections) {
        for (Collection<? extends Item> collection : collections) {
            if (!shortnameIsUnique(shortName, item, collection)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether the given shortname is unique among the given Collection.
     * @param shortName Short Name to be checked
     * @param item Item being changed
     * @param items items among which the name must be unique
     * @return shortName is unique among items
     */
    public static boolean shortnameIsUnique(String shortName, Item item, Collection<? extends Item> items) {
        for (final Item i : items) {
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
        final int index = line.lastIndexOf('.');
        if (index > 0) {
            return line.substring(0, index);
        }
        return line;
    }

    public static boolean isPersonPoOfBacklog(Person person, Organisation organisation) {
        // Check if they are the PO of any backlogs
        for (Project project : organisation.getProjects()) {
            for (Backlog backlog : project.observableBacklogs()) {
                if (backlog.getProductOwner() == person) {
                    return true;
                }
            }
        }
        return false;
    }
}
