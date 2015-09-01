package com.thirstygoat.kiqo.util;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.util.*;

import org.controlsfx.control.textfield.*;

import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.model.Item;

import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class FxUtils {
    public static <E extends Item> void setTextFieldSuggester(TextField textField, Supplier<List<E>> listSupplier) {
        
        // use a callback to get an up-to-date list, instead of just whatever exists at initialisation.
        // use a String converter so that the short name is used.
        final AutoCompletionBinding<E> binding = TextFields.bindAutoCompletion(textField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<E>>() {
            
            @Override
            public Collection<E> call(AutoCompletionBinding.ISuggestionRequest request) {
                List<E> list = listSupplier.get();
            
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
                for (final E suggestion : listSupplier.get()) {
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

    public static <T extends Item> ListCell<T> listCellFactory() {
        return new ListCell<T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(item != null ? item.getShortName() : null);
            }
        };
    }

    private static void initGoatLabelActions(GoatLabel<?> goatLabel, Editable viewModel) {
        goatLabel.setOnAction(event -> viewModel.commitEdit());
        goatLabel.setOnCancel(event -> viewModel.cancelEdit());
    }

    public static void initGoatLabel(GoatLabelTextField goatLabel, Editable viewModel, StringProperty stringProperty,
                                     ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.displayTextProperty().bind(stringProperty);
        goatLabel.getEditField().textProperty().bindBidirectional(stringProperty);
        goatLabel.validationStatus().set(validationStatus);
    }

    public static <T> void initGoatLabel(GoatLabelTextField goatLabel, Editable viewModel, ObjectProperty<T> objectProperty,
                                     ValidationStatus validationStatus, StringConverter<T> stringConverter) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.displayTextProperty().bindBidirectional(objectProperty, stringConverter);
        goatLabel.getEditField().textProperty().bindBidirectional(objectProperty, stringConverter);
        goatLabel.validationStatus().set(validationStatus);
    }

    public static void initGoatLabel(GoatLabelTextArea goatLabel, Editable viewModel,
                                     StringProperty stringProperty, ValidationStatus validationStatus, String defaultText) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.displayTextProperty().bind(stringProperty);
        goatLabel.getEditField().textProperty().bindBidirectional(stringProperty);
        goatLabel.validationStatus().set(validationStatus);
        goatLabel.setDefaultText(defaultText);
    }

    public static void initGoatLabel(GoatLabelTextArea goatLabel, Editable viewModel,
                                     StringProperty stringProperty, ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.getEditField().setStyle("-fx-background-color: deeppink");
        goatLabel.displayTextProperty().bind(stringProperty);
        goatLabel.getEditField().textProperty().bindBidirectional(stringProperty);
        goatLabel.validationStatus().set(validationStatus);
    }

    public static void initGoatLabel(GoatLabelTextArea goatLabel, Editable viewModel,
                                     FloatProperty floatProperty, ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.displayTextProperty().bind(floatProperty.asString());
        goatLabel.getEditField().textProperty().bindBidirectional(floatProperty, new NumberStringConverter());
        goatLabel.validationStatus().set(validationStatus);
    }

    public static <T> void initGoatLabel(GoatLabelComboBox<T> goatLabel, Editable viewModel,
                                         ObjectProperty<T> objectProperty, T[] items, StringConverter<T> stringConverter) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.displayTextProperty().bindBidirectional(objectProperty, stringConverter);
        goatLabel.getEditField().setItems(FXCollections.observableArrayList(items));
        goatLabel.getEditField().valueProperty().bindBidirectional(objectProperty);
        goatLabel.getEditField().setConverter(stringConverter);
    }
    
    public static <T> void initGoatLabel(GoatLabelDatePicker goatLabel, Editable viewModel,
                                          ObjectProperty<LocalDate> objectProperty, StringProperty stringProperty, ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.displayTextProperty().bindBidirectional(stringProperty);
        goatLabel.getEditField().valueProperty().bindBidirectional(objectProperty);
        goatLabel.validationStatus().set(validationStatus);
    }
}
