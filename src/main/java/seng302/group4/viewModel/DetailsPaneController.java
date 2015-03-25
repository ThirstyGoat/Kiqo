package seng302.group4.viewModel;


public class DetailsPaneController<T> {
    private DetailsController<T> subform;

    void showDetails(T p) {
        subform.showDetails(p);
    }
}

