package com.thirstygoat.kiqo.undo;

import javafx.beans.property.StringProperty;
import junit.framework.TestCase;

import java.util.ArrayList;

import com.thirstygoat.kiqo.Item;
import com.thirstygoat.kiqo.undo.MoveItemCommand;

/**
 * Created by samschofield on 25/04/15.
 */
public class MoveItemCommandTest extends TestCase {

    private static final class MockObject extends Item {

        @Override
        public String getShortName() {
            return null;
        }

        @Override
        public StringProperty shortNameProperty() {
            return null;
        }

    }

    public void test() {
        final MockObject mockObject = new MockObject();
        ArrayList<Item> position  = new ArrayList<>();
        ArrayList<Item> destination = new ArrayList<>();

        position.add(mockObject);
        final MoveItemCommand moveCommand = new MoveItemCommand(mockObject, position, destination);

        // test execute
        moveCommand.execute();
        // check that the original position no longer contains the item
        assertFalse(position.contains(mockObject));

        // check that the destination now contains the item
        assertTrue(destination.contains(mockObject));

        // test undo
        moveCommand.undo();
        // original position should now contain item
        assertTrue(position.contains(mockObject));

        // destination should no longer contain item
        assertFalse(destination.contains(mockObject));

        // test redo
        moveCommand.redo();
        // check that the original position no longer contains the item
        assertFalse(position.contains(mockObject));

        // check that the destination now contains the item
        assertTrue(destination.contains(mockObject));
    }

    public void testItemNotInList() {
        final MockObject mockObject = new MockObject();
        ArrayList<Item> position  = new ArrayList<>();
        ArrayList<Item> destination = new ArrayList<>();

        try {
            final MoveItemCommand moveCommand = new MoveItemCommand(mockObject, position, destination);
        } catch (RuntimeException e) {
            assertTrue(true);
        }

    }

}