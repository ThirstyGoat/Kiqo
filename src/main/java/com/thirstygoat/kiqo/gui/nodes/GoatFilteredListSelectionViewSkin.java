package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;


/**
 * Created by Carina Blair on 8/08/2015.
 * @param <T> type of list elements
 */
public class GoatFilteredListSelectionViewSkin<T> extends SkinBase<Control> {

    private final VBox mainView;
    private final TextField textField;
    private final ListView<T> listView;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatFilteredListSelectionViewSkin(GoatFilteredListSelectionView<T> control) {
        super(control);

        mainView = new VBox();
        textField = new TextField();
        listView = new ListView<T>();
    }

    public VBox getMainView() {
        return mainView;
    }

    public TextField getTextField() {
        return textField;
    }

    public ListView<T> getListView() {
        return listView;
    }

}
