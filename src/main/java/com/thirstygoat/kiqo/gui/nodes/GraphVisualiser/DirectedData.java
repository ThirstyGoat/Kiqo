package com.thirstygoat.kiqo.gui.nodes.GraphVisualiser;

import java.util.List;

/**
 * Created by bradley on 24/09/15.
 */
public interface DirectedData<T> {
    public T get();

    public List<DirectedData<T>> getDirectedChildren();
}
