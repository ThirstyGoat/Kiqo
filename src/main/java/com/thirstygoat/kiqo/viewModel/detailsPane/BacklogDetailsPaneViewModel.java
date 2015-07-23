package com.thirstygoat.kiqo.viewModel.detailsPane;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.util.StringConverters;
import com.thirstygoat.kiqo.viewModel.model.BacklogViewModel;

public class BacklogDetailsPaneViewModel extends BacklogViewModel {
    private final StringProperty productOwnerStringProperty;
    private final StringProperty scaleStringProperty;
    

    public BacklogDetailsPaneViewModel() {
        super();
        productOwnerStringProperty = new SimpleStringProperty("");
        scaleStringProperty = new SimpleStringProperty("");
        
        // bind to parent view model
        scaleStringProperty.bindBidirectional(super.scaleProperty(), StringConverters.scaleStringConverter());
    }

    /**
     * Bind to model instead of merely copying data in. 
     * This is because the detailspane is read-only and should always exactly represent the state of the model.
     */
    @Override
    public void load(Backlog backlog, Organisation organisation) {        
        // bind to parent view model
        productOwnerStringProperty.unbindBidirectional(super.productOwnerProperty());
        productOwnerStringProperty.bindBidirectional(super.productOwnerProperty(), StringConverters.personStringConverter(organisation));
        
        if (backlog != null) {
            super.organisationProperty().set(organisation);
            super.shortNameProperty().bind(backlog.shortNameProperty());
            super.longNameProperty().bind(backlog.longNameProperty());
            super.descriptionProperty().bind(backlog.descriptionProperty());
            super.productOwnerProperty().bind(backlog.productOwnerProperty());
            super.projectProperty().bind(backlog.projectProperty());
            super.scaleProperty().bind(backlog.scaleProperty());
            super.stories().clear();
            super.stories().addAll(backlog.getStories());
        } else {
            super.shortNameProperty().unbind();
            super.longNameProperty().unbind();
            super.descriptionProperty().unbind();
            super.productOwnerProperty().unbind();
            super.projectProperty().unbind();
            super.scaleProperty().unbind();
            
//            stories.unbind();
            super.stories().clear();
        }
    }
    
    public StringProperty productOwnerStringProperty() {
        return productOwnerStringProperty;
    }
    
    public StringProperty scaleStringProperty() {
        return scaleStringProperty;
    }
}
