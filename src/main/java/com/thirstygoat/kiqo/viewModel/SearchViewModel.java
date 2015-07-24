package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Search;
import com.thirstygoat.kiqo.model.Searchable;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by leroy on 24/07/15.
 */
public class SearchViewModel implements ViewModel {
    private Command searchCommand;
    private BooleanProperty precondition;
    private StringProperty query = new SimpleStringProperty("");
    private ObservableList<Searchable> results = FXCollections.observableArrayList();


    public SearchViewModel() {

        // Note: Search command has a .getProgress() property. Maybe this could be bound to a progress bar?
        searchCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                    search();
            }
        }, precondition, false); // true means a new thread will be created for the action.

    }

    public String getQuery() {
        return query.get();
    }

    public StringProperty queryProperty() {
        return query;
    }

    public ObservableList<Searchable> getResults() {
        return results;
    }

    private void search() {
        Search search = new Search(query.get());
        results.clear();
        results.addAll(search.execute());
    }

    public Command getSearchCommand() {
        return searchCommand;
    }
}
