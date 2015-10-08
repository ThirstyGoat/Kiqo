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
    private MainController mainController;

    public TreeMouseEventDispatcher(EventDispatcher originalDispatcher, Item item, MainController mainController) {
        this.originalDispatcher = originalDispatcher;
        this.item = item;
        this.mainController = mainController;
    }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        if (event instanceof MouseEvent) {
            if (((MouseEvent) event).getButton() == MouseButton.PRIMARY
                    && ((MouseEvent) event).getClickCount() >= 2) {

                if (!event.isConsumed()) {
                    mainController.showDetailsPane(item);
                    event.consume();
                }

                return event;
            }
        }
        return originalDispatcher.dispatchEvent(event, tail);
    }
}