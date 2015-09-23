package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.controlsfx.control.PopOver;

/**
 * Created by bradley on 9/04/15.
 */
public final class Utilities {
    public static final int SHORT_NAME_MAX_LENGTH = 20;
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    public static final Comparator<Item> LEXICAL_COMPARATOR = (item1, item2) -> {
        return item1.getShortName().compareToIgnoreCase(item2.getShortName());
    };

    public static <E extends Item> SortedList<E> createSortedList(ObservableList<E> list) {
        return list.sorted((item1, item2) -> {return item1.getShortName().compareToIgnoreCase(item2.getShortName()); });
    }

    /**
     * Given a list of Items, returns a comma separated list of those items shortName.
     * @param items A list of items.
     * @param max The maximum number of items to print.
     * @return
     */
    public static String concatenateItemsList(List<? extends Item> items, int max) {

        String list = "";

        // The true maximum can never be greater than the number of items.
        // So if max is greater than items.size() we make items.size() the new max.
        max = (max < items.size()) ? max: items.size();

        // If max is negative, then for our purposes it may as well be zero.
        // This prevents errors that occur from subtracting negative numbers.
        max = (max < 0) ? 0 : max;

        List<Item> itemsToPrint = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            itemsToPrint.add(items.get(i));
        }

        list += commaSeparatedValues(itemsToPrint);
        if (max < items.size()) {
            final int remaining = items.size() - max;
            final String others = pluralise(remaining, "other", "others");
            // Use the Oxford comma.
            list += ", and " + remaining + " " + others;
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
        else {
            concatenatedString = "-";
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
        return true;
    }

    /**
     * Attaches listeners so that the shortName mirrors the longName, in real time, up to a certain number of characters. 
     * 
     * Editing the shortName directly to differ from the longName disconnects the suggester; 
     * 	editing the shortName directly to match the longName reconnects the suggester.
     * 
     * Do *not* use this method with {@link #initShortNameSuggester(StringProperty, StringProperty)}; the length-limiting behaviour is already included here.
     * @param longName source
     * @param shortName target
     */
    public static void initShortNameSuggester(StringProperty longName, StringProperty shortName) {
        BooleanProperty isSuggesterEnabled = new SimpleBooleanProperty(true);
        shortName.addListener((observable, oldValue, newValue) -> {
            // disconnect suggester on direct edit; reconnect on edit-to-match
        	String truncated = longName.get().substring(0, Math.min(longName.get().length(), Utilities.SHORT_NAME_MAX_LENGTH-1));
    		isSuggesterEnabled.set(newValue.equals(truncated));
    		
    		// prohibit edits that would push the short name length beyond the character limit
            if (newValue.length() >= Utilities.SHORT_NAME_MAX_LENGTH) {
            	shortName.set(oldValue); // override newValue
            }
        });
        
        longName.addListener((observable, oldValue, newValue) -> {
            // Propagate truncated edition to shortName
            if (isSuggesterEnabled.get()) {
            	shortName.set(newValue.substring(0, Math.min(newValue.length(), Utilities.SHORT_NAME_MAX_LENGTH-1)));
            }
        });
    }
    
    /**
     * Sets up a listener on the name field of team to restrict it to the predefined maximum length.
     * 
     * Do *not* use this with {@link #initShortNameSuggester(StringProperty, StringProperty)}; the length-limiting behaviour is already included there.
     * @param shortName
     */
    public static void initShortNameLengthLimiter(StringProperty shortName) {
    	shortName.addListener((observable, oldValue, newValue) -> {
            // prohibit edits that would push the short name length beyond the character limit
            if (newValue.length() >= Utilities.SHORT_NAME_MAX_LENGTH) {
            	shortName.set(oldValue); // override newValue
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


    /**
     * Creates an generic predicate that takes an objectProperty and checks to see if it is a null value or not. 
     * @return predicate that checks for null values
     */
    public static <T extends Object> Predicate<T> emptinessPredicate() {
        return (T o) -> o != null && !(o.toString().isEmpty());
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static float durationToFloat(Duration duration) {
        Float hours = Float.valueOf(duration.toMinutes()) / 60;
        return round(hours, 2);
    }
}
