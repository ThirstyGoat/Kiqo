package com.thirstygoat.kiqo.gui.nodes.BiControl;

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


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.control.Control;

/**
 *
 * @param <D>
 */
public abstract class BiControl<D extends Control, E extends Control, P extends Property<?>> extends Control {
   public abstract P dataProperty();
}
