package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.Item;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TreeMouseEventDispatcher implements EventDispatcher {
    private final EventDispatcher originalDispatcher;

    private Item item;

    public TreeMouseEventDispatcher(EventDispatcher originalDispatcher, Item item) {
        this.originalDispatcher = originalDispatcher;
        this.item = item;
    }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        if (event instanceof MouseEvent) {
            if (((MouseEvent) event).getButton() == MouseButton.PRIMARY
                    && ((MouseEvent) event).getClickCount() >= 2) {

                if (!event.isConsumed()) {
                    MainController.focusedItemProperty.set(item);
                }

                event.consume();
            }
        }
        return originalDispatcher.dispatchEvent(event, tail);
    }
}