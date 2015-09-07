package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.Item;

/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelFilteredListSelectionView<T extends Item> extends GoatLabel<GoatFilteredListSelectionView<T>> {

    @Override
    protected void populateEditField() {
//        editField.setText(displayLabel.getText());
    }

    @Override
    protected GoatLabelSkin<GoatFilteredListSelectionView<T>> initSkin() {
        return new GoatLabelFilteredListSelectionViewSkin<>(this);
    }

    @Override
    public GoatFilteredListSelectionView<T> getEditField() {
        return editField;
    }
}