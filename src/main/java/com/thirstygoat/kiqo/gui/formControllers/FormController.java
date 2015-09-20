package com.thirstygoat.kiqo.gui.formControllers;

import java.util.Collection;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Organisation;

/**
 *
 * @author Amy 14/5/2015
 *
 * @param <T> Type of element handled by the form
 */
public abstract class FormController<T> implements Initializable {
    /**
     *
     * @param stage used for stage.close() on OK and Cancel`
     */
    public abstract void setStage(Stage stage);
    /**
     *
     * @param organisation root element of the model hierarchy
     */
    public abstract void setOrganisation(Organisation organisation);
    /**
     *
     * @param t existing element to be edited, or null to create a new element
     */
    public abstract void populateFields(T t);

    /**
     *
     * @return all fields pass validation (false if form cancelled)
     */
    public abstract boolean isValid();
    /**
     *
     * @return gets the command that this form represents
     */
    public abstract Command getCommand();

    /**
     * Returns a heading property. Transitional as we move to new design of form dialogs
     * @return
     */
    public StringProperty headingProperty() {
        return new SimpleStringProperty();
    }
}
