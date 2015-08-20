package com.thirstygoat.kiqo.util;

import com.thirstygoat.kiqo.model.Item;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;

import java.beans.PropertyChangeListener;

/**
 * Created by leroy on 18/08/15.
 */
public class GoatModelWrapper<M extends Item> extends ModelWrapper<M> {

    PropertyChangeListener listener = propertyChangeEvent -> {
        reload(); // Reload fields from model when model changes
    };

    @Override
    public void set(M model) {
        // Remove old property change listener if item already set
        if (get() != null) {
            M oldItem = get();
            oldItem.removePropertyChangeListener(listener);
        }
        super.set(model);
        // Set new property change listener
        M newItem = model;
        newItem.initBoundPropertySupport();
        newItem.addPropertyChangeListener(listener);
    }
}
