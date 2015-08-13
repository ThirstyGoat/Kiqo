package com.thirstygoat.kiqo.gui.formControllers;

import java.util.Collection;
import java.util.stream.Collectors;

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

    public <E extends Item> void setTextFieldSuggester(TextField textField, Collection<E> list) {
        // use a callback to get an up-to-date list, instead of just whatever exists at initialisation.
        // use a String converter so that the short name is used.
        final AutoCompletionBinding<E> binding = TextFields.bindAutoCompletion(textField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<E>>() {
            @Override
            public Collection<E> call(AutoCompletionBinding.ISuggestionRequest request) {
                // filter based on input string
                if (textField.isFocused()) {
                    final Collection<E> suggestions = list.stream()
                            .filter(t -> t.getShortName().toLowerCase().contains(request.getUserText().toLowerCase()))
                            .collect(Collectors.toList());
                    return suggestions;
                } else {
                    return null;
                }

            }

        }, new StringConverter<E>() {
            @Override
            public E fromString(String string) {
                for (final E suggestion : list) {
                    if (suggestion.getShortName().equals(string)) {
                        return suggestion;
                    }
                }
                return null;
            }

            @Override
            public String toString(E suggestion) {
                return suggestion.getShortName();
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // forces suggestion list to show
                binding.setUserInput(textField.getText());
            }
        });
    }
}
