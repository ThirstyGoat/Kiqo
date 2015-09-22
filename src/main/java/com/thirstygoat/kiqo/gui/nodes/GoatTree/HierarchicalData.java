package com.thirstygoat.kiqo.gui.nodes.GoatTree;

import java.util.List;

/**
 * Interface to represent an object which has children of the same type.
 * Note, that this interface does not care about the object's parent(s), but only the children.
 *
 * @author  Bradley Kirwan
 */
public interface HierarchicalData<T> {
    public T getItem();

    /**
     * Gets the children of an item
     * @return The children of an item
     */
    public List<HierarchicalData<T>> getChildren();
}
