package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Item;

import java.util.List;

/**
 * Created by samschofield on 25/04/15.
 * Command for moving an item from one collection to another
 *
 * @param <T> Type of Item to be moved
 */
public class MoveItemCommand<T extends Item> extends Command {
    private final T item;
    private final List<? super T> position;
    private final List<? super T> destination;
    private final int posIndex;
    private final int destIndex;


    /**
     * @param item        The item to move
     * @param position    The current position of the item
     * @param destination The desired destination for the item
     */
    public MoveItemCommand(final T item, final List<? super T> position, final List<? super T> destination) {
        this.item = item;
        this.position = position;
        this.destination = destination;
        this.posIndex = -1;
        this.destIndex = -1;

        if (!position.contains(item)) {
            throw new RuntimeException("Item not found in position collection");
        }
    }

    /**
     * @param item        item to move
     * @param position    list that item is in
     * @param posIndex    index of item in the collection it is in
     * @param destination list item will be moved to
     * @param destIndex   index that the item will be positioned at in the list it is moved to
     */
    public MoveItemCommand(final T item, final List<? super T> position, final int posIndex, final List<? super T> destination, final int destIndex) {
        this.item = item;
        this.position = position;
        this.destination = destination;
        this.posIndex = posIndex;
        this.destIndex = destIndex;

        if (!position.contains(item)) {
            throw new RuntimeException("Item not found in position collection");
        }
    }

    @Override
    public void execute() {
        if (posIndex != -1 && destIndex != -1) {
            // position and destination are the same list in the same state
            if (destination.get(posIndex).equals(item)) {
                destination.remove(posIndex);
            }
            destination.add(destIndex, item);
        } else {
            position.remove(item);
            destination.add(item);
        }
    }

    @Override
    public void undo() {
        if (posIndex != -1 && destIndex != -1) {
            destination.remove(item);
            position.add(posIndex, item);
        } else {
            destination.remove(item);
            position.add(item);
        }
    }

    @Override
    public String getType() {
        return "Move Item";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MoveItemCommand\\{item='");
        builder.append(item);
        builder.append("', position='");
        builder.append(position);
        builder.append("', destination='");
        builder.append(destination);
        builder.append("', posIndex='");
        builder.append(posIndex);
        builder.append("', destIndex='");
        builder.append(destIndex);
        builder.append("\\}");
        return builder.toString();
    }

}
