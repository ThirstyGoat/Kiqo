package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.model.Item;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.time.LocalDate;
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

    private static void initGoatLabelActions(GoatLabel<?> goatLabel, Editable editable) {
        goatLabel.setOnAction(event -> editable.commitEdit());
        goatLabel.setOnCancel(event -> editable.cancelEdit());
    }

    /**
     * Binds a GoatLabel to a StringProperty.
     * @param goatLabel control
     * @param stringProperty property to be displayed/edited
     */
    private static void bindStringProperty(GoatLabel<? extends TextInputControl> goatLabel, StringProperty stringProperty) {
        goatLabel.displayTextProperty().bind(stringProperty);
        goatLabel.getEditField().textProperty().bindBidirectional(stringProperty);
    }

    /**
     * Binds a GoatLabel to an ObjectProperty using a StringConverter.
     * 
     * @param goatLabel control
     * @param objectProperty property to be displayed/edited
     * @param stringConverter
     */
    private static <T> void bindObjectProperty(GoatLabel<? extends TextInputControl> goatLabel, ObjectProperty<T> objectProperty,
            StringConverter<T> stringConverter) {
        goatLabel.displayTextProperty().bindBidirectional(objectProperty, stringConverter);
        goatLabel.getEditField().textProperty().bindBidirectional(objectProperty, stringConverter);
    }

    /**
     * Configures a GoatLabel for a StringProperty.
     * @param goatLabel control
     * @param editable viewModel
     * @param stringProperty property to be displayed/edited
     * @param validationStatus status of the Validator for this field
     */
    public static void initGoatLabel(GoatLabel<? extends TextInputControl> goatLabel, Editable editable, StringProperty stringProperty,
                                     ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, editable);
        bindStringProperty(goatLabel, stringProperty);
        goatLabel.validationStatus().set(validationStatus);
    }

    public static void initGoatLabel(GoatLabelTextField goatLabel, Editable viewModel, IntegerProperty integerProperty,
                                     ValidationStatus validationStatus, StringConverter stringConverter) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.displayTextProperty().bindBidirectional(integerProperty, stringConverter);
        goatLabel.getEditField().textProperty().bindBidirectional(integerProperty, stringConverter);
        goatLabel.validationStatus().set(validationStatus);
    }

    public static void initGoatLabel(GoatLabelTextArea goatLabel, Editable viewModel,
                                     StringProperty stringProperty, ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.displayTextProperty().bind(stringProperty);
        goatLabel.getEditField().textProperty().bindBidirectional(stringProperty);
        goatLabel.validationStatus().set(validationStatus);
    }

    public static void initGoatLabel(GoatLabelTextField goatLabel, Editable viewModel,
                                     FloatProperty floatProperty, ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.displayTextProperty().bind(floatProperty.asString());
        goatLabel.getEditField().textProperty().bindBidirectional(floatProperty, new NumberStringConverter());
        goatLabel.restrictToNumericInput(true);
        goatLabel.validationStatus().set(validationStatus);
    }
    
    /**
     * Configures a GoatLabel for an ObjectProperty using a StringConverter.
     * @param goatLabel control
     * @param editable viewModel
     * @param objectProperty property to be displayed/edited
     * @param stringConverter converter to translate the value of objectProperty between an Object and a String
     * @param validationStatus status of the Validator for this field
     */
    public static <T> void initGoatLabel(GoatLabel<? extends TextInputControl> goatLabel, Editable editable, ObjectProperty<T> objectProperty,
                                     StringConverter<T> stringConverter, ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, editable);
        bindObjectProperty(goatLabel, objectProperty, stringConverter);
        goatLabel.validationStatus().set(validationStatus);
    }

    /**
     * Configures a GoatLabel for a StringProperty, with default text.
     * @param goatLabel control
     * @param editable viewModel
     * @param stringProperty property to be displayed/edited
     * @param validationStatus status of the Validator for this field
     * @param defaultText placeholder text to display when property is null or empty
     */
    public static void initGoatLabel(GoatLabel<? extends TextInputControl> goatLabel, Editable editable,
                                     StringProperty stringProperty, ValidationStatus validationStatus, String defaultText) {
        initGoatLabel(goatLabel, editable, stringProperty, validationStatus);
        goatLabel.setDefaultText(defaultText);
    }

    /**
     * Configures a GoatLabel for an ObjectProperty using a StringConverter, with default text.
     * @param goatLabel control
     * @param editable viewModel
     * @param objectProperty property to be displayed/edited
     * @param stringConverter converter to translate the value of objectProperty between an Object and a String
     * @param validationStatus status of the Validator for this field
     * @param defaultText placeholder text to display when property is null or empty
     */
    public static <T> void initGoatLabel(GoatLabel<? extends TextInputControl> goatLabel, Editable editable,
            ObjectProperty<T> objectProperty, StringConverter<T> stringConverter, ValidationStatus validationStatus, String defaultText) {
        initGoatLabel(goatLabel, editable, objectProperty, stringConverter, validationStatus);
        goatLabel.setDefaultText(defaultText);
    }
    
    /**
     * Configures a GoatLabelComboBox for an ObjectProperty using a StringConverter, with default text.
     * @param goatLabel control
     * @param editable viewModel
     * @param items list of options in ComboBox
     * @param objectProperty property to be displayed/edited
     * @param stringConverter converter to translate the value of objectProperty between an Object and a String
     */
    public static <T> void initGoatLabel(GoatLabelComboBox<T> goatLabel, Editable editable,
                                         T[] items, ObjectProperty<T> objectProperty, StringConverter<T> stringConverter) {
        initGoatLabelActions(goatLabel, editable);
        goatLabel.displayTextProperty().bindBidirectional(objectProperty, stringConverter);
        goatLabel.getEditField().setItems(FXCollections.observableArrayList(items));
        goatLabel.getEditField().valueProperty().bindBidirectional(objectProperty);
        goatLabel.getEditField().setConverter(stringConverter);
    }
    
    /**
     * Configures a GoatLabelDatePicker for an ObjectProperty&lt;LocalDate&gt;, with default text.
     * @param goatLabel control
     * @param editable viewModel
     * @param objectProperty property to be displayed (assumes this is bound to stringProperty)
     * @param stringProperty property to be edited (assumes this is bound to objectProperty)
     * @param validationStatus status of the Validator for this field
     */
    public static <T> void initGoatLabel(GoatLabelDatePicker goatLabel, Editable editable,
                                          ObjectProperty<LocalDate> objectProperty, StringProperty stringProperty, ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, editable);
        goatLabel.displayTextProperty().bindBidirectional(stringProperty);
        goatLabel.getEditField().valueProperty().bindBidirectional(objectProperty);
        goatLabel.validationStatus().set(validationStatus);
    }

    public static void enableShiftEnter(TextArea textArea, Runnable runnable) {
        // Need to catch ENTER key presses to remove focus from textarea so that form can be submitted
        // Shift+Enter should create new line in the text area

        textArea.setOnKeyPressed(event -> {
            final KeyCombination shiftEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
            final KeyCombination enter = new KeyCodeCombination(KeyCode.ENTER);
            if (shiftEnter.match(event)) {
                // force new line
                textArea.appendText("\n");
                event.consume();
            } else if (enter.match(event)) {
                event.consume();
                try {
                    runnable.run();
                } catch (Exception ignored) {}
            }
        });
    }

    public static <T extends Item> void initGoatLabel(GoatLabelFilteredListSelectionView<T> goatLabel,
                                                      Editable viewModel, ListProperty<T> targetList,
                                                      ListProperty<T> sourceList) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.getEditField().targetItemsProperty().bindBidirectional(targetList);
        goatLabel.getEditField().sourceItemsProperty().bind(sourceList);
        goatLabel.displayTextProperty().bind(Utilities.commaSeparatedValuesProperty(targetList));
    }
}
