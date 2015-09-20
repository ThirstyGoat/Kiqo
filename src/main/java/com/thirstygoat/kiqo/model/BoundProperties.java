package com.thirstygoat.kiqo.model;

import java.beans.PropertyChangeListener;

/**
 * Created by leroy on 19/09/15.
 */
public interface BoundProperties {
    void initBoundPropertySupport();
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}
