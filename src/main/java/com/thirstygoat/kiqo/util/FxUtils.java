package com.thirstygoat.kiqo.util;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.*;

import com.thirstygoat.kiqo.gui.*;
import com.thirstygoat.kiqo.gui.nodes.*;
import com.thirstygoat.kiqo.gui.nodes.bicontrol.*;
import com.thirstygoat.kiqo.model.Item;

import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.util.*;
import javafx.util.converter.NumberStringConverter;

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

    public static <E extends Item> void setTextFieldSuggester(TextField textField, Collection<E> list) {
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
     * @param property property to be displayed/edited
     */
    private static void bindProperty(GoatLabel<? extends TextInputControl> goatLabel, StringProperty property) {
        goatLabel.displayTextProperty().bind(property);
        goatLabel.getEditField().textProperty().bindBidirectional(property);
    }

    /**
     * Binds a GoatLabel to a Property using a StringConverter.
     * 
     * @param goatLabel control
     * @param property property to be displayed/edited
     * @param stringConverter
     */
    private static <T> void bindProperty(GoatLabel<? extends TextInputControl> goatLabel, Property<T> property,
            StringConverter<T> stringConverter) {
        goatLabel.displayTextProperty().bindBidirectional(property, stringConverter);
        goatLabel.getEditField().textProperty().bindBidirectional(property, stringConverter);
    }

    /**
     * Configures a GoatLabel for a StringProperty.
     * @param goatLabel control
     * @param editable viewModel
     * @param property property to be displayed/edited
     * @param validationStatus status of the Validator for this field
     */
    public static void initGoatLabel(GoatLabel<? extends TextInputControl> goatLabel, Editable editable, StringProperty property,
                                     ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, editable);
        bindProperty(goatLabel, property);
        goatLabel.validationStatus().set(validationStatus);
    }

    /**
     * Configures a GoatLabel for a Property&lt;Number&gt;.
     * @param goatLabel control
     * @param editable viewModel
     * @param property property to be displayed/edited
     * @param validationStatus status of the Validator for this field
     */
    public static void initGoatLabel(GoatLabelTextField goatLabel, Editable viewModel,
            Property<Number> property, ValidationStatus validationStatus) {
        initGoatLabelActions(goatLabel, viewModel);
        bindProperty(goatLabel, property, new NumberStringConverter());
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
        bindProperty(goatLabel, objectProperty, stringConverter);
        goatLabel.validationStatus().set(validationStatus);
    }
    
    /**
     * Configures a GoatLabelComboBox for an ObjectProperty using a StringConverter.
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
     * Configures a GoatLabelDatePicker for an ObjectProperty&lt;LocalDate&gt;.
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

    /**
     * Configures a GoatLabelFilteredListSelectionView for a ListProperty.
     * @param goatLabel control
     * @param editable viewModel
     * @param targetList list of selected elements
     * @param sourceList list of eligible (unselected) elements
     * @param validationStatus status of the Validator for this field
     */
    public static <T extends Item> void initGoatLabel(
    			GoatLabelFilteredListSelectionView<T> goatLabel,
    			Editable viewModel, ListProperty<T> targetList,
    			ListProperty<T> sourceList) {
        initGoatLabelActions(goatLabel, viewModel);
        goatLabel.getEditField().targetItemsProperty().bindBidirectional(targetList);
        goatLabel.getEditField().sourceItemsProperty().bind(sourceList);
        goatLabel.displayTextProperty().bind(Utilities.commaSeparatedValuesProperty(targetList));
    }
    
    public static <T extends Item> void initLabelFilteredListBiControl(
	    		FilteredListBiControl<Label, T> listBiControl,
	            Editable viewModel, 
	            ListProperty<T> selectedItems,
	            ListProperty<T> unselectedItems) {
    	listBiControl.selectedItems().bindBidirectional(selectedItems);
	    listBiControl.unselectedItems().bind(unselectedItems);
	    LabelFilteredListBiControlSkin<T> skin = new LabelFilteredListBiControlSkin<T>(
	    		listBiControl,
	            viewModel::commitEdit, viewModel::cancelEdit,
	            Item::shortNameProperty);
	    listBiControl.setSkin(skin);
	}

	public static <T extends Item> void initListViewFilteredListBiControl(FilteredListBiControl<ListView<T>, T> listBiControl,
                                                      Editable viewModel,
                                                      ListProperty<T> selectedItems,
                                                      ListProperty<T> unselectedItems) {
        listBiControl.selectedItems().bindBidirectional(selectedItems);
        listBiControl.unselectedItems().bind(unselectedItems);
        ListViewFilteredListBiControlSkin<T> skin = new ListViewFilteredListBiControlSkin<T>(
        		listBiControl,
                viewModel::commitEdit, viewModel::cancelEdit,
                Item::shortNameProperty);
        listBiControl.setSkin(skin);
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
	            } catch (Exception ignored) {
	            }
	        }
	    });
	}

	/**
     * Attaches the basic key shortcuts to a scene
     * Undo/Redo, and Save
     * @param scene Scene to attach key shortcuts to
     */
    public static void attachKeyShortcuts(Scene scene) {
        scene.setOnKeyPressed(event -> {
            // Check if Shortcut + Z was pressed
            if (event.isShortcutDown() && !event.isShiftDown() && event.getCode() == KeyCode.Z) {
                try {
                    MainController.menuBarView.undo();
                } catch (NoSuchElementException ignored) {}
            } else if (event.isShortcutDown() && event.isShiftDown() && event.getCode() == KeyCode.Z) {
                try {
                    MainController.menuBarView.redo();
                } catch (NoSuchElementException ignored) {}
            } else if (event.isShortcutDown() && !event.isShiftDown() && event.getCode() == KeyCode.S) {
                MainController.menuBarView.save();
            } else if (event.isShortcutDown() && event.isShiftDown() && event.getCode() == KeyCode.S) {
                MainController.menuBarView.saveAs();
            }
        });
    }

    /**
     * Method for restricting the input of a textfield to numbers between min and max values
     * e.g.
     * textfield.textProperty().addListener(FxUtils.numbericInputRestrictor(0, 99, textfield));
     *
     * @param min
     * @param max
     * @param textField
     * @return
     */
    public static ChangeListener<String> numbericInputRestrictor(int min, int max, TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
            } else {
                if (!newValue.equals("")) {
                    int num = Integer.parseInt(newValue);
                    if (num < min || num > max) {
                        textField.setText(oldValue);
                    }
                }
            }
        };
    }
}
