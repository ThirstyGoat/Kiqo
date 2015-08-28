package com.thirstygoat.kiqo.gui;

/**
 * Created by leroy on 26/08/15.
 */
public interface Editable {

    /**
     * What the viewModel should do when an edit is made.
     * For example, commit the changes to the model object.
     */
    void commitEdit();

    /**
     * What the ViewModel should do if an edit is cancelled.
     * For example, reload it's fields to the original values.
     */
    void cancelEdit();
}
