package com.thirstygoat.kiqo.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by leroy on 24/07/15.
 */
public class SearchViewModel implements ViewModel {
        // Note this is an mvvmFX command. It is for executing tasks and is different from our command.
        // See: https://github.com/sialcasa/mvvmFX/wiki/Commands for more information.
        private Command searchCommand;
        // Precondition for searching.
        private BooleanProperty precondition;
        private StringProperty searchString = new SimpleStringProperty("");

        // <String> for now, but will probably be changed to something like <? implements Searchable>
        private ObservableList<String> results = FXCollections.observableArrayList();

        public SearchViewModel() {
                // Note: Search command has a .getProgress() property. Maybe this could be bound to a progress bar?
                searchCommand = new DelegateCommand(() -> new Action() {
                        @Override
                        protected void action() throws Exception {
                                search();
                        }
                }, precondition, true); // true means a new thread will be created for the action.

                // precondition.bind(...); // Some preconditions for searchCommand.isExecutable() property.
        }

        private void search() {
                // TODO Search for stuff and and update ViewModel properties.
        }

        public Command getSearchCommand() {
                return searchCommand;
        }
}
