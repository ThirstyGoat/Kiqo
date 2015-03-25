package seng302.group4.viewModel;

import javafx.fxml.Initializable;

public interface DetailsController<T> extends Initializable {
    public void showDetails(T item);
}
