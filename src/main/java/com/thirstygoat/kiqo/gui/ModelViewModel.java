package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.Supplier;

/**
 * A base class for ModelViewModels.
 */
public abstract class ModelViewModel<T extends Item> implements ViewModel, Loadable<T> {
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
     * @return A command that can be executed by UndoManager. In the case of a {@link ModelViewModel}, this should
     * be a command that will commit the changes cached by the ViewModel's ModelWrapper to the underlying model.
     */
    public abstract Command getCommand();
}
