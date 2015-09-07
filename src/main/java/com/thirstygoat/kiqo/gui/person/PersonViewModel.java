package com.thirstygoat.kiqo.gui.person;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.create.CreatePersonCommand;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Supplier;

public class PersonViewModel extends ModelViewModel<Person> {
    private ModelWrapper<Person> modelWrapper;
    private ObjectProperty<Person> person;
    private ObjectProperty<Organisation> organisation;

    private ObservableRuleBasedValidator nameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private CompositeValidator allValidator;

    public PersonViewModel() {
        modelWrapper = new GoatModelWrapper<>();
        person = new SimpleObjectProperty<>(null);
        organisation = new SimpleObjectProperty<>(null);
        createValidators();
    }
    
    private void createValidators() {
        nameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() -> 
            { 
                if (organisationProperty().get() != null) {
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), personProperty().get(), organisationProperty().get().getSkills());
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
    protected Supplier<Person> modelSupplier() {
        return Person::new;
    }

    @Override
    protected void afterLoad() {
//        eligableStories.setAll(storySupplier().get());
//        projectProperty().addListener(change -> {
//            eligableStories.setAll(storySupplier().get());
//        });
    }

    /**
     * @param person model object to be displayed
     * @param organisation (ignored)
     */
    @Override
    public void load(Person person, Organisation organisation) {
        personProperty().set(person);
        organisationProperty().set(organisation);
        if (person != null) {
            modelWrapper.set(person);
        } else {
            modelWrapper.set(new Person());
        }
        modelWrapper.reload();
    }

    public void reload() {
        modelWrapper.reload();
    }

    protected Command createCommand() {
        final Command command;
        if (person.get() != null) { // edit
            final ArrayList<Command> changes = new ArrayList<>();

            if (!shortNameProperty().get().equals(person.get().getShortName())) {
                changes.add(new EditCommand<>(person.get(), "shortName", shortNameProperty().get()));
            }
            if (!longNameProperty().get().equals(person.get().getLongName())) {
                changes.add(new EditCommand<>(person.get(), "longName", longNameProperty().get()));
            }
            if (!descriptionProperty().get().equals(person.get().getDescription())) {
                changes.add(new EditCommand<>(person.get(), "description", descriptionProperty().get()));
            }
            if (!userIdProperty().get().equals(person.get().getUserId())) {
                changes.add(new EditCommand<>(person.get(), "userId", userIdProperty().get()));
            }
            if (!emailProperty().get().equals(person.get().getEmailAddress())) {
                changes.add(new EditCommand<>(person.get(), "emailAddress", emailProperty().get()));
            }
            if (!phoneNumberProperty().get().equals(person.get().getPhoneNumber())) {
                changes.add(new EditCommand<>(person.get(), "phoneNumber", phoneNumberProperty().get()));
            }
            if (!departmentProperty().get().equals(person.get().getDepartment())) {
                changes.add(new EditCommand<>(person.get(), "department", departmentProperty().get()));
            }


//            if (!(.containsAll(person.getSkills())
//                    && person.getSkills().containsAll(skills))) {
//                changes.add(new EditCommand<>(person, "skills", skills));
//            }

            if (changes.size() > 0) {
                command = new CompoundCommand("Edit Skill", changes);
            } else {
                command = null;
            }
        } else { // new
            final Person p = new Person(shortNameProperty().get(), longNameProperty().get(),
                    descriptionProperty().get(), userIdProperty().get(), emailProperty().get(),
                    phoneNumberProperty().get(), descriptionProperty().get(), new ArrayList<>());
            command = new CreatePersonCommand(p, organisationProperty().get());
        }
        return command;
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
        return modelWrapper.field("skills", Person::getSkills, Person::setSkills, Collections.emptyList());
    }

//    public ListProperty<Skill> allSkills() {
//        return modelWrapper.field("skills", Organisation::getSkills, Organisation::setSkills, Collections.emptyList());
//    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Person::getDescription, Person::setDescription, "");
    }

    protected ObjectProperty<Person> personProperty() {
        return person;
    }
    
    public ObjectProperty<Organisation> organisationProperty() {
        return organisation;
    }

    @Override
    public Command getCommand() {
        return null;
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
