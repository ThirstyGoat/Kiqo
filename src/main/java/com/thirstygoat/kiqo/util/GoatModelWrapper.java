package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.BoundProperties;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.beans.PropertyChangeListener;


/**
 * Created by leroy on 18/08/15.
 */
public class GoatModelWrapper<M extends BoundProperties> extends ModelWrapper<M> {
    private ObjectProperty<EventHandler<ActionEvent>> modelChangeAction =
                    new SimpleObjectProperty<>(event -> {});

    PropertyChangeListener listener = propertyChangeEvent -> {
        reload(); // Reload fields from model when model changes
        modelChangeAction.get().handle(new ActionEvent());
    };

    @Override
    public void set(M model) {
        // Remove old property change listener if item already set
        if (get() != null) {
            M oldItem = get();
            oldItem.removePropertyChangeListener(listener);
        }
        super.set(model);
        super.reload();
        // Set new property change listener
        M newItem = model;
        newItem.initBoundPropertySupport();
        newItem.addPropertyChangeListener(listener);
    }

    public void onModelChange(EventHandler<ActionEvent> action) {
        modelChangeAction.set(action);
    }
}
