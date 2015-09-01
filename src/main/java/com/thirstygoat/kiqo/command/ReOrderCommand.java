package com.thirstygoat.kiqo.command;

import java.util.List;

/**
 * Command to move an object to a new index within a list
 * @param <T> Type of object to be moved
 */
public class ReOrderCommand<T> extends Command {
    private List<T> list;
    private T item;
    private int newIndex;
    private int oldIndex;

    /**
     * Command to move an item to a new index within a list
     * @param list List in which item belongs to
     * @param item Item to be moved in the list
     * @param newIndex The index to move the item to (within the list)
     */
    public ReOrderCommand(List<T> list, T item, int newIndex) {
        this.list = list;
        this.item = item;
        this.newIndex = newIndex;
    }

    @Override
    public void execute() {
        System.out.println(list);

        oldIndex = list.indexOf(item);
        System.out.println("old index: " + oldIndex);
        System.out.println("new index: " + newIndex);
        list.remove(item);

        // Since we are removing the item first, the new index position needs to be adjusted
        // appropriately, since if the item is to be moved to a position down the list, its insertion index
        // will be off by one (too large).
        int i = (newIndex > oldIndex) ? newIndex - 1 : newIndex;
        list.add(i, item);
    }

    @Override
    public String toString() {
        return "Moving: " + item + ", from position: " + oldIndex + " to new position: " + newIndex;
    }

    @Override
    public void undo() {
        list.remove(item);

        int i = (oldIndex > newIndex) ? oldIndex - 1 : oldIndex;
        list.add(i, item);
    }

    @Override
    public String getType() {
        return "Re-order";
    }
}