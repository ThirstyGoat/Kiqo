package seng302.group4.undo;

import junit.framework.TestCase;
import seng302.group4.Item;

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

}