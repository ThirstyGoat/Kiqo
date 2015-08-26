package com.thirstygoat.kiqo.gui;

import org.junit.*;

public class FormButtonHandlerTest {
    private boolean commandSupplied;
    private boolean exited;
    private FormButtonHandler formButtonHandler = new FormButtonHandler(
            () -> {
                commandSupplied = true;
                return null;
            },
            () -> {
                exited = true;
            });

    @Test
    public final void testOkAction() {
        commandSupplied = false;
        exited = false;
        formButtonHandler.okAction();
        Assert.assertTrue("must supply command", commandSupplied);
        Assert.assertTrue("must exit", exited);
    }

    @Test
    public final void testCancelAction() {
        commandSupplied = false;
        exited = false;
        formButtonHandler.cancelAction();
        Assert.assertFalse("must not supply command", commandSupplied);
        Assert.assertTrue("must exit", exited);
    }
}
