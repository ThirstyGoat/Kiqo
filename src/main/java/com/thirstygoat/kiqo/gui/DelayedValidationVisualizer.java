package com.thirstygoat.kiqo.gui;

import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import javafx.beans.value.ObservableValue;

/**
 * ControlsFxVisualizer which binds the visualizers decorationEnabledProperty to an ObservableValue&lt;Boolean&gt;
 * which determines whether or not validation is visualized. For example, if we bind decorationEnabledProperty to
 * a ViewModel's dirtyProperty, then validation errors will only be visualized when the ViewModel is edited.
 */
public class DelayedValidationVisualizer extends ControlsFxVisualizer {

    public DelayedValidationVisualizer(ObservableValue<Boolean> decorationEnabled) {
        this.errorDecorationEnabledProperty().bind(decorationEnabled);
    }
}
