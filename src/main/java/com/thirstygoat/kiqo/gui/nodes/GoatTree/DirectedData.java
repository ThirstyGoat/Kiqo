package com.thirstygoat.kiqo.gui.nodes.GoatTree;

import java.util.List;

/**
 * Interface to represent data which is directed
 */
public interface DirectedData<T> {
    public T get();
    public List<DirectedData<T>> getDescendants();
}
