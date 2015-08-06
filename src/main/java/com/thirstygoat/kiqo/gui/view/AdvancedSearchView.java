package com.thirstygoat.kiqo.gui.view;

import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.search.AdvancedSearchListCell;
import com.thirstygoat.kiqo.search.SearchResult;
import com.thirstygoat.kiqo.search.SearchableItems;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 31/07/15.
 */
public class AdvancedSearchView implements FxmlView<AdvancedSearchViewModel>, Initializable {

    @FXML
    private TextField searchTextField;
    @FXML
    private CheckBox regexCheckBox;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<SearchableItems.SCOPE> limitSearchComboBox;
    @FXML
    private ListView<SearchResult> resultsListView;
    @InjectViewModel
    private AdvancedSearchViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.searchScopeProperty().bindBidirectional(limitSearchComboBox.valueProperty());
        limitSearchComboBox.setItems(FXCollections.observableArrayList(SearchableItems.SCOPE.values()));
        limitSearchComboBox.setValue(SearchableItems.SCOPE.ORGANISATION);

        // Bind view elements to view model
        regexCheckBox.selectedProperty().bindBidirectional(viewModel.regexEnabledProperty());
        searchTextField.textProperty().bindBidirectional(viewModel.searchQueryProperty());

        // Disable search button appropriately depending on whether search is RegEx or not
        searchButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> {
                    if (searchTextField.getText().length() == 0)
                        return true; // We do not allow an empty search (Whether RegEx or not)
                    if (regexCheckBox.selectedProperty().get())
                        return false; // We allow leading and trailing whitespace for RegEx search
                    if (searchTextField.getText().trim().length() == 0)
                        return true; // We do not allow empty string for non regex search
                    return false;
                }, searchTextField.textProperty(), regexCheckBox.selectedProperty()));

        resultsListView.setItems(viewModel.getSearchResults());

        resultsListView.setCellFactory(param -> new AdvancedSearchListCell(viewModel));

        resultsListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                viewModel.action(resultsListView.getSelectionModel().getSelectedItem());
            }
        });

        // Set button action
        searchButton.setOnAction(event -> viewModel.search());
    }
}
