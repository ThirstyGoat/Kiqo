/**
 * 
 */
package com.thirstygoat.kiqo.gui;

import java.util.function.Supplier;

import com.thirstygoat.kiqo.command.*;

/**
 * Generates ok and cancel actions which selectively execute a (lazily-created) command and then exit. These actions can be used for controls (eg. buttons).
 * @author amy
 * 26/8/15
 */
public class FormButtonHandler {
    private Supplier<? extends Command> commandSupplier;
    private Runnable exitStrategy;
    
    /**
     * 
     * @param commandSupplier generates the command to be executed
     * @param exitStrategy called as the last statement in okAction and cancelAction (eg. `stage.close()`)
     */
    public FormButtonHandler(Supplier<? extends Command> commandSupplier, Runnable exitStrategy) {
        this.commandSupplier = commandSupplier;
        this.exitStrategy = exitStrategy;
    }

    public void okAction() {
        final Command command = commandSupplier.get();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
        exitStrategy.run();
    }

    public void cancelAction() {
        exitStrategy.run();
    }
}
