package com.thirstygoat.kiqo.util;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.beans.PropertyChangeListener;

/**
 * Created by leroy on 31/08/15.
 */
public class BoundPropertySupportTest {
    private BooleanProperty propertyChanged;
    private PropertyChangeListener listener;
    private TestObject testObject;

    private class TestObject {
        private BoundPropertySupport bps = new BoundPropertySupport(this);
        private StringProperty stringProperty = new SimpleStringProperty("");
        private ListProperty<String> listProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

        public TestObject() {
            bps.addPropertyChangeSupportFor(listProperty);
            bps.addPropertyChangeSupportFor(stringProperty);
        }

        public StringProperty stringProperty() {
            return stringProperty;
        }

        public ListProperty<String> listProperty() {
            return listProperty;
        }

        public final void addPropertyChangeListener(PropertyChangeListener listener) {
            this.bps.addChangeListener(listener);
        }

        public final void removePropertyChangeListener(PropertyChangeListener listener) {
            this.bps.removeChangeListener(listener);
        }
    }

    @Before
    public void setup() {
        propertyChanged = new SimpleBooleanProperty(false);
        listener = propertyChangeEvent -> {
            propertyChanged.set(true);
        };
        testObject = new TestObject();
        testObject.addPropertyChangeListener(listener);
    }

    @Test
    public void testStringPropertyChange() {
        Assert.assertTrue(propertyChanged.getValue().equals(false));

        testObject.stringProperty().set("a different string");
        Assert.assertTrue(propertyChanged.getValue().equals(true));
    }

    @Test
    public void testChangeListProperty() {
        testObject.listProperty().set(FXCollections.observableArrayList("a new list"));
        Assert.assertTrue(propertyChanged.getValue().equals(true));
    }

    @Test
    public void testAddItemToList() {
        testObject.listProperty().add("a new item");
        Assert.assertTrue(propertyChanged.getValue().equals(true));
    }

    @Test
    public void testRemoveItemFromList() {
        testObject.listProperty().add("a new item");
        propertyChanged.set(false);

        testObject.listProperty().remove(0);
        Assert.assertTrue(propertyChanged.getValue().equals(true));
    }
}
