package com.thirstygoat.kiqo.gui.view;

import com.thirstygoat.kiqo.gui.model.AdvancedSearchViewModel;
import com.thirstygoat.kiqo.search.SearchResult;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 31/07/15.
 */
public class AdvancedSearchView implements FxmlView<AdvancedSearchViewModel>, Initializable {

    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private ListView<SearchResult> resultsListView;
    @InjectViewModel
    private AdvancedSearchViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultsListView.setItems(viewModel.getSearchResults());

        resultsListView.setCellFactory(param -> new ListCell<SearchResult>() {
            @Override
            protected void updateItem(SearchResult item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    setText(item.getResultText());
                } else {
                    setText("");
                }
            }
        });

        // Set button action
        searchButton.setOnAction(event -> viewModel.search(searchTextField.getText()));
    }
}
