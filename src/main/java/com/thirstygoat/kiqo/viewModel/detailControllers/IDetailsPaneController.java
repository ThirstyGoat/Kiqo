package com.thirstygoat.kiqo.viewModel.detailControllers;

import com.thirstygoat.kiqo.viewModel.MainController;

public interface IDetailsPaneController<T> {
    /**
     *
     * @param t element to be displayed
     */
    public void showDetails(T t);
    /**
     * Make maincontroller available to the "allocate teams" buttons
     * @param mainController singleton instance of the supplier of all methods
     */
    public void setMainController(MainController mainController);
}
