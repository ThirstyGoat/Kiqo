package com.thirstygoat.kiqo.gui.person;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.create.CreatePersonCommand;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PersonViewModel extends ModelViewModel<Person> {
    private ListProperty<Skill> availableSkills;

    private ObservableRuleBasedValidator nameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private CompositeValidator allValidator;

    public PersonViewModel() {
        availableSkills = new SimpleListProperty<>(FXCollections.observableArrayList(Item.getWatchStrategy()));
        createValidators();
    }
    
    @Override
    protected Supplier<Person> modelSupplier() {
        return Person::new;
    }

    @Override
    protected void afterLoad() {
        availableSkills.setAll(availableSkillsSupplier.get());
    }

    /**
     * Supplies skills which can be added to a persons list of skills.
     */
    public Supplier<List<Skill>> availableSkillsSupplier =
            () -> { if (organisationProperty().get() != null) {
                return organisationProperty().get().getSkills().stream()
                        .filter(skill -> !skills().contains(skill))
                        .collect(Collectors.toList());
            }
                return Collections.emptyList();
            };

    private void createValidators() {
        nameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() -> 
            { 
                if (organisationProperty().get() != null) {
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), modelWrapper.get(), organisationProperty().get().getSkills());
                } else {
                    return true; // no organisation means this isn't for real yet.
                }
            }, 
            shortNameProperty());
        nameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        nameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        nameValidator.addRule(shortNameProperty().length().lessThan(20), ValidationMessage.error("Name must be less than 20 characters"));
        nameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within organisation"));

        descriptionValidator = new ObservableRuleBasedValidator(); // always true
        
        allValidator = new CompositeValidator(nameValidator, descriptionValidator);
    }

    @Override
    public Command getCommand() {
        final ArrayList<Skill> skills = new ArrayList<>();
        skills.addAll(skills().get());

        if (modelWrapper.get()  == null) {
            final Person p = new Person(shortNameProperty().get(), longNameProperty().get(),
                    descriptionProperty().get(), userIdProperty().get(), emailProperty().get(),
                    phoneNumberProperty().get(), departmentProperty().get(), skills);
            return new CreatePersonCommand(p, organisationProperty().get());
        } else {
            final ArrayList<Command> changes = new ArrayList<>();
            super.addEditCommands.accept(changes);

            if (!(skills.containsAll(modelWrapper.get().getSkills())
                    && modelWrapper.get().getSkills().containsAll(skills))) {
                changes.add(new EditCommand<>(modelWrapper.get() , "skills", skills));
            }

            return new CompoundCommand("Edit Person" , changes);
        }
    }

    public StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Person::getShortName, Person::setShortName, "");
    }

    public StringProperty longNameProperty() {
        return modelWrapper.field("longName", Person::getLongName, Person::setLongName, "");
    }

    public StringProperty userIdProperty() {
        return modelWrapper.field("userId", Person::getUserId, Person::setUserId, "");
    }

    public StringProperty emailProperty() {
        return modelWrapper.field("emailAddress", Person::getEmailAddress, Person::setEmailAddress, "");
    }

    public StringProperty phoneNumberProperty() {
        return modelWrapper.field("phoneNumber", Person::getPhoneNumber, Person::setPhoneNumber, "");
    }

    public StringProperty departmentProperty() {
        return modelWrapper.field("department", Person::getDepartment, Person::setDepartment, "");
    }

    public ListProperty<Skill> skills() {
        return modelWrapper.field("skills", Person::getSkills, Person::setSkills, new ArrayList<>());
    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Person::getDescription, Person::setDescription, "");
    }

    protected ListProperty<Skill> availableSkills() {
        return availableSkills;
    }
    
    public ValidationStatus nameValidation() {
        return nameValidator.getValidationStatus();
    }
    
    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }

    public ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}
