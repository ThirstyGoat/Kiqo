package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.viewModel.SearchViewModel;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import javax.naming.directory.SearchResult;

/**
 * Created by leroy on 24/07/15.
 */
public class SearchView implements FxmlView<SearchViewModel> {
    //@FXML
    Button searchButton;

    //@FXML

    //@FXML
    ListView<SearchResult> searchResults;

    @InjectViewModel
    private SearchViewModel viewModel;

    public void initialize() {
        searchButton.disableProperty().bind(viewModel.getSearchCommand().isExecutableProperty().not());
    }

    //@FXML //Method that is called if the button is clicked
    public void searchAction() {
        viewModel.getSearchCommand().execute();
    }
}
