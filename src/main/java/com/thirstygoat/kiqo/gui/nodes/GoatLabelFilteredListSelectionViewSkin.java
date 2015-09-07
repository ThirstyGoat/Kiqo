package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.Item;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Created by samschofield on 7/08/15.
 */
public class GoatLabelFilteredListSelectionViewSkin<T extends Item> extends GoatLabelSkin<GoatFilteredListSelectionView<T>> {

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatLabelFilteredListSelectionViewSkin(@SuppressWarnings("rawtypes") GoatLabel control) {
        super(control);
    }

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */

    @Override
    protected void setSizing() {

        displayView.setMaxWidth(Control.USE_PREF_SIZE);
        displayView.setMinWidth(Control.USE_PREF_SIZE);
        stackPane.setAlignment(Pos.TOP_LEFT);


    }

    @Override
    protected GoatFilteredListSelectionView<T> createEditField() {
        return new GoatFilteredListSelectionView<>();
    }


    @Override
    protected void showEditField() {
        editField.setMinHeight(Control.USE_COMPUTED_SIZE);
        editField.setMaxHeight(Control.USE_COMPUTED_SIZE);
        editView.setMinHeight(Control.USE_COMPUTED_SIZE);
        editView.setMaxHeight(Control.USE_COMPUTED_SIZE);
    }

}