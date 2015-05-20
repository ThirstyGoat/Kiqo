package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.Item;

import java.util.Comparator;

/**
 * Compartor for ordering items alphabetically.
 * @param <T>
 */
public class ItemComparator<T extends Item> implements Comparator<T> {
    @Override
    public int compare(T item1, T item2) {
        return item1.getShortName().compareToIgnoreCase(item2.getShortName());
    }
}