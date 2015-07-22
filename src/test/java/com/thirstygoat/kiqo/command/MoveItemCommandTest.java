package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Item;
import javafx.beans.property.StringProperty;
import junit.framework.TestCase;

import java.util.ArrayList;

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
        final ArrayList<Item> position  = new ArrayList<>();
        final ArrayList<Item> destination = new ArrayList<>();

        position.add(mockObject);
        final MoveItemCommand<MockObject> moveCommand = new MoveItemCommand<>(mockObject, position, destination);

        // test execute
        moveCommand.execute();
        // check that the original position no longer contains the item
        TestCase.assertFalse(position.contains(mockObject));

        // check that the destination now contains the item
        TestCase.assertTrue(destination.contains(mockObject));

        // test undo
        moveCommand.undo();
        // original position should now contain item
        TestCase.assertTrue(position.contains(mockObject));

        // destination should no longer contain item
        TestCase.assertFalse(destination.contains(mockObject));

        // test redo
        moveCommand.redo();
        // check that the original position no longer contains the item
        TestCase.assertFalse(position.contains(mockObject));

        // check that the destination now contains the item
        TestCase.assertTrue(destination.contains(mockObject));
    }

    public void testItemNotInList() {
        final MockObject mockObject = new MockObject();
        final ArrayList<Item> position  = new ArrayList<>();
        final ArrayList<Item> destination = new ArrayList<>();

        try {
            final MoveItemCommand<MockObject> moveCommand = new MoveItemCommand<>(mockObject, position, destination);
        } catch (final RuntimeException e) {
            TestCase.assertTrue(true);
        }

        // TODO implement me
    }

}