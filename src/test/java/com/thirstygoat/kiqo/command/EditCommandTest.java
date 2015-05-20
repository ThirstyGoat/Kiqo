package com.thirstygoat.kiqo.command;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests EditCommand
 *
 * @author amy
 */
public class EditCommandTest {
    private final static int ORIGINAL_VALUE = 42;
    private final static int NEW_VALUE = 11;

    /**
     * Tests that the field value is modified during execute().
     */
    @Test
    public void testExecute() {
        final MockObject mockObject = new MockObject();
        final EditCommand<MockObject, Integer> editCommand = new EditCommand<MockObject, Integer>(mockObject, "value",
                EditCommandTest.NEW_VALUE);

        Assert.assertEquals("Field should not be modified before execution", ORIGINAL_VALUE, mockObject.getValue());
        editCommand.execute();
        Assert.assertEquals("Field should be modified after execution", EditCommandTest.NEW_VALUE, mockObject.getValue());
    }

    /**
     * Tests that attempting to change a non-existent field's value results in a
     * FieldNotFoundException in the EditCommand constructor.
     */
    @Test(expected = com.thirstygoat.kiqo.exceptions.FieldNotFoundException.class)
    public void testExecute_badFieldName() {
        final MockObject mockObject = new MockObject();
        new EditCommand<MockObject, Integer>(mockObject, "notARealField", EditCommandTest.NEW_VALUE);
    }

    /**
     * Tests that redo reverts the field to its original value
     */
    @Test
    public void testRedo() {
        final MockObject mockObject = new MockObject();
        final EditCommand<MockObject, Integer> editCommand = new EditCommand<MockObject, Integer>(mockObject, "value",
                EditCommandTest.NEW_VALUE);

        editCommand.execute();
        editCommand.undo();

        Assert.assertEquals(ORIGINAL_VALUE, mockObject.getValue());
        editCommand.redo();
        Assert.assertEquals("Field should be modified after redo", EditCommandTest.NEW_VALUE, mockObject.getValue());
    }

    /**
     * Tests that undo reverts the change
     */
    @Test
    public void testUndo() {
        final MockObject mockObject = new MockObject();
        final EditCommand<MockObject, Integer> editCommand = new EditCommand<MockObject, Integer>(mockObject, "value",
                EditCommandTest.NEW_VALUE);

        editCommand.execute();

        Assert.assertEquals(EditCommandTest.NEW_VALUE, mockObject.getValue());
        editCommand.undo();
        Assert.assertEquals("Field should be reverted after undo", ORIGINAL_VALUE, mockObject.getValue());
    }

    public static final class MockObject {
        private int value = ORIGINAL_VALUE;

        /**
         * @return the value
         */
        public int getValue() {
            return this.value;
        }

        /**
         * @param value
         *            the value to set
         */
        @SuppressWarnings("unused")
        public void setValue(final int value) {
            this.value = value;
        }
    }
}
