package seng302.group4.utils;

import seng302.group4.Person;

import java.util.ArrayList;

/**
 * Created by bradley on 9/04/15.
 */
public class Utilities {

    public static String concatenatePeopleList(ArrayList<Person> people, int max) {
        String list = "";
        for (int i = 0; i < Math.min(people.size(), max)-1; i++) {
            list += people.get(i).getShortName() + ", ";
        }
        list += people.get(Math.min(people.size(), max)-1).getShortName();
        if (Math.min(people.size(), max) < people.size()) {
            int remaining = people.size() - max;
            String others = (remaining % 2 == 0) ? "others" : "other";
            list += " and " + remaining + " " + others;
        }

        return list;
    }
}
