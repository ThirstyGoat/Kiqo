package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.exceptions.FieldNotFoundException;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.viewModel.MainController;

/**
 * Reverts to the last saved location.
 * 15/5/2015
 * @author Amy
 *
 */
public class RevertCommand extends EditCommand<MainController, Organisation> {
    private final Runnable postExecutionHandler;

    public RevertCommand(MainController mainController, String fieldName, Organisation organisation, Runnable postExecutionHandler) throws FieldNotFoundException {
        super(mainController, fieldName, organisation);
        this.postExecutionHandler = postExecutionHandler;
    }

    @Override
    public Void execute() {
        super.execute();
        postExecutionHandler.run();
        return null;
    }

    @Override
    public String getType() {
        return "Revert";
    }
}
