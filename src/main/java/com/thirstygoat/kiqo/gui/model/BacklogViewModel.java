package com.thirstygoat.kiqo.gui.model;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model ViewModel
 * @author amy
 *
 */
public class BacklogViewModel implements ViewModel {
    private final ObjectProperty<Organisation> organisationProperty;

    private final StringProperty shortNameProperty;
    private final StringProperty longNameProperty;
    private final StringProperty descriptionProperty;
    private final ObjectProperty<Person> productOwnerProperty;
    private final ObjectProperty<Project> projectProperty;
    private final ObjectProperty<Scale> scaleProperty;
    //    private final FunctionBasedValidator<ObservableListValue<Story>> storiesValidator;
    
    private final ObservableList<Story> stories;

    private final FunctionBasedValidator<String> shortNameValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final FunctionBasedValidator<String> descriptionValidator;
    private final FunctionBasedValidator<Person> productOwnerValidator;
    private final FunctionBasedValidator<Project> projectValidator;
    private final FunctionBasedValidator<Scale> scaleValidator;
//    private final FunctionBasedValidator<ObservableListValue<Story>> storiesValidator;

    public BacklogViewModel() {
        organisationProperty = new SimpleObjectProperty<>();
        
        shortNameProperty = new SimpleStringProperty("");
        longNameProperty = new SimpleStringProperty("");
        descriptionProperty = new SimpleStringProperty("");
        productOwnerProperty = new SimpleObjectProperty<>();
        projectProperty = new SimpleObjectProperty<>();
        scaleProperty = new SimpleObjectProperty<>();
        stories = FXCollections.observableArrayList(Item.getWatchStrategy());
        
        shortNameValidator = new FunctionBasedValidator<>(shortNameProperty, 
                string -> {
                    if (string.length() == 0|| string.length() > 20) {
                        System.out.println("A");
                        return false;
                    }
                    final Project project = projectProperty.get();
                    if (project == null) {
                        System.out.println("B");
                        return true;
                    } else {
                        System.out.println("C");
                        return Utilities.shortnameIsUnique(string, null, project.getBacklogs());
                    }
                },
                ValidationMessage.error("Short name must be unique and not empty"));
        
        longNameValidator = new FunctionBasedValidator<>(longNameProperty,
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Name must not be empty."));
        
        descriptionValidator = new FunctionBasedValidator<>(descriptionProperty, 
                string -> { 
                    return true; 
                },
                ValidationMessage.error("Description is not valid."));
        
        productOwnerValidator = new FunctionBasedValidator<>(productOwnerProperty, 
                person -> {
                    return person != null && person.getSkills().contains(organisationProperty.get().getPoSkill());
                },
                ValidationMessage.error("Product Owner must exist and possess the PO Skill."));
        
        projectValidator = new FunctionBasedValidator<>(projectProperty, 
                project -> {
                    return project != null;
                },
                ValidationMessage.error("Project must exist."));
        
        scaleValidator = new FunctionBasedValidator<>(scaleProperty, 
                scale -> {
                    return false;
                },
                ValidationMessage.error("Scale is not valid.")); // TODO improve message
        
//        storiesValidator = new FunctionBasedValidator<ObservableList<Story>>(stories, 
//                (Predicate<ObservableList<Story>>) Utilities.emptinessPredicate(),
//                ValidationMessage.error("Stories must not be in any other backlogs."));
    }
    
    public void load(Backlog backlog, Organisation organisation) {
        this.organisationProperty.set(organisation);
        if (backlog != null) {
            shortNameProperty.set(backlog.shortNameProperty().get());
            longNameProperty.set(backlog.longNameProperty().get());
            descriptionProperty.set(backlog.descriptionProperty().get());
            productOwnerProperty.set(backlog.productOwnerProperty().get());
            projectProperty.set(backlog.projectProperty().get());
            scaleProperty.set(backlog.scaleProperty().get());
            stories.clear();
            stories.addAll(backlog.getStories());
        } else {
            shortNameProperty.set("");
            longNameProperty.set("");
            descriptionProperty.set("");
            productOwnerProperty.set(null);
            projectProperty.set(null);
            scaleProperty.set(null);
            stories.clear();
        }
    }
    
    public StringProperty shortNameProperty() {
        return shortNameProperty;
    }

    public StringProperty longNameProperty() {
        return longNameProperty;
    }

    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public ObjectProperty<Person> productOwnerProperty() {
        return productOwnerProperty;
    }

    public ObjectProperty<Project> projectProperty() {
        return projectProperty;
    }
    
    public ObjectProperty<Scale> scaleProperty() {
        return scaleProperty;
    }
    
    public ObservableList<Story> stories() {
        return stories;
    }
    
    public ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }
    
    public ValidationStatus longNameValidation() {
        return longNameValidator.getValidationStatus();
    }
    
    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }
    
    public ValidationStatus productOwnerValidation() {
        return productOwnerValidator.getValidationStatus();
    }
    
    public ValidationStatus projectValidation() {
        return projectValidator.getValidationStatus();
    }
    
    public ValidationStatus scaleValidation() {
        return scaleValidator.getValidationStatus();
    }

    public ObjectProperty<Organisation> organisationProperty() {
        return organisationProperty;
    }
    
//    public ValidationStatus storiesValidation() {
//        return storiesValidator.getValidationStatus();
//    }
}
