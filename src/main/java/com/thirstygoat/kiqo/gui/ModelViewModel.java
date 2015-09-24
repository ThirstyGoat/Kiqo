package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.BoundProperties;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * A base class for ModelViewModels.
 */
public abstract class ModelViewModel<T extends BoundProperties> implements ViewModel, Loadable<T> {
    protected static final Logger LOGGER = Logger.getLogger(ModelViewModel.class.getName());
    protected final GoatModelWrapper<T> modelWrapper = new GoatModelWrapper<>();
    private final ObjectProperty<Organisation> organisationProperty = new SimpleObjectProperty<>(null);

    /**
     * @return A method reference to the no-args constructor of T. For example, if T is an instance of Backlog then you
     * would typically call setModelSupplier(Backlog::new). Unfortunately it is near impossible to create a new instance
     * of a generic type T in Java due to type erasure.
     */
    protected abstract Supplier<T> modelSupplier();

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
        reload();
    }

    public void reset() {
        modelWrapper.reset();
    }

    public void reload() {
        modelWrapper.reload();
    }

    /**
     * Maybe don't use this. Added to fix issues #117 and #118.
     * @return
     */
    @Deprecated
    public T getWrappedObject() {
        return modelWrapper.get();
    }
    
    public ObjectProperty<Organisation> organisationProperty() {
        return organisationProperty;
    }

     /**
     * @return A command that can be executed by UndoManager. In the case of a {@link ModelViewModel}, this should
     * be a command that will commit the changes cached by the ViewModel's ModelWrapper to the underlying model.
     */
    public abstract Command getCommand();

    /**
     * Adds edit commands for all changed fields to the accepted list. Doesn't work for ListProperties.
     */
    public Consumer<List<Command>> addEditCommands = commands -> {
        modelWrapper.getChangedFields().stream()
                .filter(field -> !field.getProperty().getClass().equals(SimpleListProperty.class))
                .forEach(field -> commands.add(new EditCommand<>(modelWrapper.get(), field.getFieldName(),
                        field.getProperty().getValue())));
    };
    
    public ObservableValue<Boolean> dirtyProperty() {
		return modelWrapper.dirtyProperty();
	}
}
