/**
 * 
 */
package com.thirstygoat.kiqo.gui.skill;

import java.util.function.Supplier;

import com.thirstygoat.kiqo.command.*;

/**
 * @author amy
 * 26/8/15
 */
public final class FormButtonHandler {
    private Supplier<Command> commandSupplier;
    private Runnable exitStrategy;
    
    public FormButtonHandler(Supplier<Command> commandSupplier, Runnable exitStrategy) {
        this.commandSupplier = commandSupplier;
        this.exitStrategy = exitStrategy;
    }

    public void setExitStrategy(Runnable exitStrategy) {
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
