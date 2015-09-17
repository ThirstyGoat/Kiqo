package com.thirstygoat.kiqo.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A sort strategy which sorts elements by string such that elements with a string matching a regex from the start
 * of the string are placed before elements with a non matching string.
 *
 * For example: given the list ["Aben", "Carmen", "Benjamin"] and regex "ben", the list would be sorted as
 * ["Benjamin", "Aben", "Carom"].
 *
 * @param <E> Type of elements to be sorted.
 * @param <C> Method reference which returns a String when called on an object of type {@link E}.
 * @param <D> The type of the regex used to sort elements. Must extend string.
 */
public class AnchoredSortStrategy<E, C extends String, D extends String> extends SortStrategy<E, String, String> {

    @Override
    public List<E> sorted(List<E> toSort) {
        ArrayList<E> sorted = new ArrayList<>();
        sorted.addAll(toSort.parallelStream()
                        .filter(element -> comparableGetter.apply(element).toLowerCase().matches(data + ".*"))
                        .collect(Collectors.toList()));
        sorted.addAll(toSort.parallelStream()
                        .filter(element -> !sorted.contains(element))
                        .collect(Collectors.toList()));
        return sorted;
    }
}
