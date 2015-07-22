package com.thirstygoat.kiqo.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.util.Utilities;

import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;

/**
 * Model ViewModel
 * @author amy
 *
 */
public class BacklogMVM {
    private Organisation organisation;
    
    private final StringProperty shortNameProperty;
    private final StringProperty longNameProperty;
    private final StringProperty descriptionProperty;
    private final ObjectProperty<Person> productOwnerProperty;
    private final ObjectProperty<Project> projectProperty;
//    private final ObservableListValue<Story> stories;

    private final FunctionBasedValidator<String> shortNameValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final FunctionBasedValidator<String> descriptionValidator;
    private final FunctionBasedValidator<Person> productOwnerValidator;
    private final FunctionBasedValidator<Project> projectValidator;
//    private final FunctionBasedValidator<ObservableListValue<Story>> storiesValidator;
    
    public BacklogMVM() {
        shortNameProperty = new SimpleStringProperty("");
        longNameProperty = new SimpleStringProperty("");
        descriptionProperty = new SimpleStringProperty("");
        productOwnerProperty = new SimpleObjectProperty<>();
        projectProperty = new SimpleObjectProperty<>();
//        stories = FXCollections.observableArrayList(Item.getWatchStrategy());
        
        shortNameValidator = new FunctionBasedValidator<>(shortNameProperty, 
                string -> {
                    if (string.length() == 0|| string.length() > 20) {
                        return false;
                    }
                    final Project project = projectProperty.get();
                    if (project == null) {
                        return true;
                    } else {
                        return Utilities.shortnameIsUnique(string, null, project.getBacklogs());
                    }
                },
                ValidationMessage.error("Short name must be unique and not empty"));
        
        longNameValidator = new FunctionBasedValidator<>(longNameProperty,
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Long name must not be empty."));
        
        descriptionValidator = new FunctionBasedValidator<>(descriptionProperty, 
                string -> { 
                    return true; 
                },
                ValidationMessage.error("Description is not valid."));
        
        productOwnerValidator = new FunctionBasedValidator<>(productOwnerProperty, 
                person -> {
                    return person != null && person.getSkills().contains(organisation.getPoSkill());
                },
                ValidationMessage.error("Product Owner must exist and possess the PO Skill."));
        
        projectValidator = new FunctionBasedValidator<>(projectProperty, 
                project -> {
                    return project != null;
                },
                ValidationMessage.error("Project must exist."));
        
//        storiesValidator = new FunctionBasedValidator<ObservableList<Story>>(stories, 
//                (Predicate<ObservableList<Story>>) Utilities.emptinessPredicate(),
//                ValidationMessage.error("Stories must not be in any other backlogs."));
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
    
    public ValidationStatus projectNameValidation() {
        return projectValidator.getValidationStatus();
    }
    
//    public ValidationStatus storiesValidation() {
//        return storiesValidator.getValidationStatus();
//    }
}
