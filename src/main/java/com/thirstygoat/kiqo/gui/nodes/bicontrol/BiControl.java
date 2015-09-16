package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.scene.control.Control;


/**
 * A base class for BiControls. BiControls are controls which have two states: an editable state and a
 * display state.
 *
 * @param <D> The control to use for displaying data. For example {@link javafx.scene.control.Label} or
 *           {@link javafx.scene.control.ListView}.
 *
 * @param <E> The control to use for editing data. For example {@link com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView}
 *           or {@link javafx.scene.control.TextField}.
 */
public abstract class BiControl<D extends Control, E extends Control, T> extends Control {
    public abstract T getData();
    public abstract void setData(T data);
}
