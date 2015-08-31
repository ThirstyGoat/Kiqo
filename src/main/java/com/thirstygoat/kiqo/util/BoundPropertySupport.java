/*
 * Copyright 2014 Benjamin Gale.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirstygoat.kiqo.util;

import javafx.beans.property.ListProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

public final class BoundPropertySupport {

    private final PropertyChangeSupport changeHandler;
    private final Map<ObservableValue<?>, String> propertyNameMap;
    private final ChangeListener<Object> changeListener;
    private final ListChangeListener<Object> listChangeListener;

    public BoundPropertySupport(Object bean) {
        this.changeHandler = new PropertyChangeSupport(bean);
        this.propertyNameMap = new HashMap<>();

        this.changeListener = (ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
            String propertyName = BoundPropertySupport.this.propertyNameMap.get(observable);
            BoundPropertySupport.this.changeHandler.firePropertyChange(propertyName, oldValue, newValue);
        };

        this.listChangeListener = change -> {
            // Getting the actual observable, oldValue and newValue is too much work, so we just fudge them
            // Note: var3 must be different to var2 otherwise the dirty hack doesn't work!
            BoundPropertySupport.this.changeHandler.firePropertyChange("fudge", "fudge", "more fudge");
        };
    }

    public void addPropertyChangeSupportFor(ListProperty listProperty) {
        listProperty.addListener(this.listChangeListener);
        listProperty.addListener(this.changeListener);
    }

    public void addPropertyChangeSupportFor(Property property) {
        if (!this.propertyNameMap.containsKey(property)) {
            this.propertyNameMap.put(property, property.getName());
            property.addListener(this.changeListener);
        }
    }

    public void addChangeListener(PropertyChangeListener listener) {
        this.changeHandler.addPropertyChangeListener(listener);
    }

    public void removeChangeListener(PropertyChangeListener listener) {
        this.changeHandler.removePropertyChangeListener(listener);
    }
}