package com.thirstygoat.kiqo.sort;

import java.util.List;
import java.util.function.Function;

/**
 * A sort strategy takes a list with elements of type {@link E} and returns a list with the elements sorted according
 * to some strategy.
 *
 * @param <E> The type of elements in the list to be sorted.
 */
public abstract class SortStrategy<E, C, D> {
    protected Function<E, C> comparableGetter;
    protected D data;

    /**
     * Provide a method reference which returns the comparable by which elements should be sorted.
     * For example, if the elements are of type {@link com.thirstygoat.kiqo.model.Item} and we wanted to sort by
     * shortName, we would set the comparableGetter to Item::getShortName.
     */
    public void setComparableGetter(Function<E, C> comparableGetter) {
        this.comparableGetter = comparableGetter;
    }

    public Function<E, C> getComparableGetter() {
        return comparableGetter;
    }

    public void setData(D data) {
        this.data = data;
    }

    /**
     * Given a list, sort it according to some strategy.
     * @return A sorted list.
     */
    public abstract List<E> sorted(List<E> toSort);
}
