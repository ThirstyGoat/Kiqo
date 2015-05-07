package com.thirstygoat.kiqo.command;

import java.util.Collection;

import com.thirstygoat.kiqo.model.Item;

/**
 * Created by samschofield on 25/04/15.
 * Command for moving an item from one collection to another
 * @param <T> Type of Item to be moved
 */
public class MoveItemCommand<T extends Item> extends Command<Void> {
    private final T item;
    private final Collection<? super T> position;
    private final Collection<? super T> destination;


    /**
     *
     * @param item The item to move
     * @param position The current position of the item
     * @param destination The desired destination for the item
     */
    public MoveItemCommand(final T item, final Collection<? super T> position, final Collection<? super T> destination) {
        this.item = item;
        this.position = position;
        this.destination = destination;

        if (!position.contains(item)) {
            throw new RuntimeException("Item not found in position collection");
        }
    }

    @Override
    public Void execute() {
        position.remove(item);
        destination.add(item);
        return null;
    }

    @Override
    public void undo() {
        destination.remove(item);
        position.add(item);
    }

    @Override
    public String getType() {
        return "Move Item";
    }

}
