package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.Item;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;

import java.beans.PropertyChangeListener;

/**
 * Created by leroy on 18/08/15.
 */
public class GoatModelWrapper<M> extends ModelWrapper<M> {

    PropertyChangeListener listener = propertyChangeEvent -> {
        reload(); // Reload fields from model when model changes
    };

    @Override
    public void set(M model) {
        // Remove old property change listener if item already set
        if (get() != null) {
            Item oldItem = (Item) get();
            oldItem.removePropertyChangeListener(listener);
        }
        super.set(model);
        // Set new property change listener
        Item newItem = (Item) model;
        newItem.initBoundPropertySupport();
        newItem.addPropertyChangeListener(listener);
    }
}
