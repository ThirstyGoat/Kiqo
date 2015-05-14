package com.thirstygoat.kiqo.viewModel.formControllers;

import javafx.stage.Stage;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Organisation;

/**
 *
 * @author Amy 14/5/2015
 *
 * @param <T> Type of element handled by the form
 */
public interface IFormController<T> {
    /**
     *
     * @param stage used for stage.close() on OK and Cancel
     */
    public void setStage(Stage stage);
    /**
     *
     * @param organisation root element of the model hierarchy
     */
    public void setOrganisation(Organisation organisation);
    /**
     *
     * @param t existing element to be edited, or null to create a new element
     */
    public void populateFields(T t);
    /**
     *
     * @return all fields pass validation (false if form cancelled)
     */
    public boolean isValid();
    /**
     *
     * @return gets the command that this form represents
     */
    public Command<?> getCommand();
}
