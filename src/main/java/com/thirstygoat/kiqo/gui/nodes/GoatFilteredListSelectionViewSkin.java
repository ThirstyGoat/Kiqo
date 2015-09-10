package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


/**
 * Created by Carina Blair on 8/08/2015.
 */
public class GoatFilteredListSelectionViewSkin extends SkinBase<Control> {

    private final VBox mainView;
    private final TextField textField;
    private final ListView listView;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatFilteredListSelectionViewSkin(Control control) {
        super(control);

        mainView = new VBox();
        textField = new TextField();
        listView = new ListView();
    }

    public VBox getMainView() {
        return mainView;
    }

    public TextField getTextField() {
        return textField;
    }

    public ListView getListView() {
        return listView;
    }

}
