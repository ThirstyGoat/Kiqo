package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A base class for ModelViewModels.
 */
public abstract class ModelViewModel<T extends Item> implements ViewModel, Loadable<T> {
    protected static final Logger LOGGER = Logger.getLogger(ModelViewModel.class.getName());
    protected final GoatModelWrapper<T> modelWrapper = new GoatModelWrapper<>();
    private final ObjectProperty<Organisation> organisationProperty = new SimpleObjectProperty<>();

    /**
     * @return A method reference to the no-args constructor of T. For example, if T is an instance of Backlog then you
     * would typically call setModelSupplier(Backlog::new). Unfortunately it is near impossible to create a new instance
     * of a generic type T in Java due to type erasure.
     */
    protected abstract Supplier<T> modelSupplier();

    /**
     * Actions to carry out after loading. Example actions include initialising lists and possibly setup listeners on
     * property fields. For example, if you have a Backlog, you might want to add a listener to the projectProperty()
     * which updates a list of valid stories that can be added to the Backlog.
     */
    protected abstract void afterLoad();

    /**
     * Load an item into the ModelWrapper, and set the organisationProperty.
     * @param item The item to load into the viewModels wrapper. If item is null, then a new item will be created.
     * @param organisation The organisation within which the viewModel is being loaded.
     */
    public void load(T item, Organisation organisation) {
        organisationProperty().set(organisation);

        if (item != null) {
            modelWrapper.set(item);
        } else {
            modelWrapper.set(modelSupplier().get());
            modelWrapper.reset();
            modelWrapper.commit();
        }
        modelWrapper.reload();

        afterLoad();
    }

    public void reset() {
        modelWrapper.reset();
    }

    public void reload() {
        modelWrapper.reload();
    }
    
    public ObjectProperty<Organisation> organisationProperty() {
        return organisationProperty;
    }

    /**
     * @return The {@link ValidationStatus} of a {@link CompositeValidator} which combines all other validators.
     */
    public abstract ValidationStatus allValidation();


    /**
     * @return A {@link Command} to be return by the getCommand method.
     */
    protected abstract Command createCommand();

     /**
     * @return A command that can be executed by UndoManager. In the case of a {@link ModelViewModel}, this should
     * be a command that will commit the changes cached by the ViewModel's ModelWrapper to the underlying model.
     */
    public Command getCommand() {
        if (!allValidation().isValid()) {
            LOGGER.log(Level.WARNING, "Fields are invalid, no command will be returned.");
            return null;
        } else if (!modelWrapper.isDirty()) {
            LOGGER.log(Level.WARNING, "Nothing changed. No command will be returned");
            return null;
        } else {
            return createCommand();
        }
    }

    /**
     * Adds edit commands for all changed fields to the accepted list.
     */
    public Consumer<List<Command>> addEditCommands =
            commands -> {
                modelWrapper.getChangedFields().stream()
                        .filter(field -> !field.getProperty().getClass().equals(SimpleListProperty.class))
                        .forEach(field -> commands.add(new EditCommand<>(modelWrapper.get(), field.getFieldName(),
                                field.getProperty().getValue())));
            };
}
