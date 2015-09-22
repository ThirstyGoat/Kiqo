package com.thirstygoat.kiqo.gui.team;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TeamViewModel extends ModelViewModel<Team> {

    private ObservableRuleBasedValidator shortNameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private ObservableRuleBasedValidator productOwnerValidator;
    private ObservableRuleBasedValidator scrumMasterValidator;
    private ObservableRuleBasedValidator teamMembersValidator;
    private ObservableRuleBasedValidator devTeamValidator;
    private CompositeValidator allValidator;

    public TeamViewModel() {
        createValidators();
    }

    @Override
    protected Supplier<Team> modelSupplier() {
        return Team::new;
    }

    private void createValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        BooleanBinding uniqueName = Bindings.createBooleanBinding(() -> 
            { 
                if (organisationProperty().get() != null) {
                    return Utilities.shortnameIsUnique(shortNameProperty().get(), modelWrapper.get(),
                                    organisationProperty().get().getTeams());
                } else {
                    return true; // no project means this isn't for real yet.
                }
            }, 
            shortNameProperty(), organisationProperty());
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(20), ValidationMessage.error("Name must be less than 20 characters"));
        shortNameValidator.addRule(uniqueName, ValidationMessage.error("Name must be unique within organisation"));

        descriptionValidator = new ObservableRuleBasedValidator(); // always true
       
        // the other validators are input-constrained so need not be validated
        productOwnerValidator = new ObservableRuleBasedValidator(); // always true
        scrumMasterValidator = new ObservableRuleBasedValidator(); // always true
        teamMembersValidator = new ObservableRuleBasedValidator(); // always true
        devTeamValidator = new ObservableRuleBasedValidator(); // always true

        allValidator = new CompositeValidator(shortNameValidator, descriptionValidator, productOwnerValidator, scrumMasterValidator, teamMembersValidator, devTeamValidator);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Command getCommand() {
        // edit command
        final ArrayList<Command> changes = new ArrayList<>();
        super.addEditCommands.accept(changes);

        if (!(teamMembersProperty().get().containsAll(modelWrapper.get().getTeamMembers())
                        && modelWrapper.get().getTeamMembers().containsAll(teamMembersProperty().get()))) {
            changes.add(new EditCommand<>(modelWrapper.get(), "teamMembers", teamMembersProperty().get()));
        }

        if (!(devTeamProperty().get().containsAll(modelWrapper.get().getDevTeam())
                        && modelWrapper.get().getDevTeam().containsAll(devTeamProperty().get()))) {
            changes.add(new EditCommand<>(modelWrapper.get(), "devTeam", devTeamProperty().get()));
        }

        final ArrayList<Person> newMembers = new ArrayList<>(teamMembersProperty().get());
        newMembers.removeAll(modelWrapper.get().getTeamMembers());
        final ArrayList<Person> oldMembers = new ArrayList<>(modelWrapper.get().getTeamMembers());
        oldMembers.removeAll(teamMembersProperty().get());

        // Loop through all the new members and add a command to set their team
        // Set the person's team field to this team
        changes.addAll(newMembers.stream().map(person -> new EditCommand<>(person, "team", modelWrapper.get()))
                .collect(Collectors.toList()));

        // Loop through all the old members and add a command to remove their team
        // Set the person's team field to null, since they're no longer in the team
        changes.addAll(oldMembers.stream().map(person -> new EditCommand<>(person, "team", null))
                .collect(Collectors.toList()));

        return new CompoundCommand("Edit Team", changes);
    }

    protected Supplier<List<Person>> productOwnerSupplier() {
        return () -> {
            Skill poSkill = organisationProperty().get().getPoSkill();
            Person currentScrumMaster = scrumMasterProperty().get();
            List<Person> currentDevTeam = devTeamProperty().get();
            // person has po skill and does not currently have any other role in the team
            List<Person> eligiblePeople = organisationProperty().get().getPeople().stream()
                    .filter(person -> {
                        return person.getSkills().contains(poSkill) 
                                && (currentScrumMaster == null || !person.equals(currentScrumMaster))
                                && !currentDevTeam.contains(person);
                    }).collect(Collectors.toList());
            return eligiblePeople;
        };
    }
    
    protected Supplier<List<Person>> scrumMasterSupplier() {
        return () -> {
            Skill smSkill = organisationProperty().get().getSmSkill();
            Person currentProductOwner = productOwnerProperty().get();
            List<Person> currentDevTeam = devTeamProperty().get();
            // person has sm skill and does not currently have any other role in the team
            return organisationProperty().get().getPeople().stream()
                    .filter(person -> person.getSkills().contains(smSkill) 
                            && !person.equals(currentProductOwner)
                            && !currentDevTeam.contains(person))
                    .collect(Collectors.toList());
        };
    }
    
    protected StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Team::getShortName, Team::setShortName, "");
    }

    protected StringProperty descriptionProperty() {
        return modelWrapper.field("description", Team::getDescription, Team::setDescription, "");
    }
    
    protected ObjectProperty<Person> productOwnerProperty() {
        return modelWrapper.field("productOwner", Team::getProductOwner, Team::setProductOwner, null);
    }
    
    protected ObjectProperty<Person> scrumMasterProperty() {
        return modelWrapper.field("scrumMaster", Team::getScrumMaster, Team::setScrumMaster, null);
    }
    
    protected ListProperty<Person> teamMembersProperty() {
        return modelWrapper.field("teamMembers", Team::getTeamMembers, Team::setTeamMembers, new ArrayList<Person>());
    }
    
    protected ListProperty<Person> devTeamProperty() {
        return modelWrapper.field("devTeam", Team::getDevTeam, Team::setDevTeam, new ArrayList<Person>());
    }

    protected ListProperty<Allocation> allocations() {
        return modelWrapper.field("allocations", Team::getAllocations, Team::setAllocations, new ArrayList<Allocation>());
    }
    
    protected ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }
    
    protected ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }
    
    protected ValidationStatus productOwnerValidation() {
        return productOwnerValidator.getValidationStatus();
    }
    
    protected ValidationStatus scrumMasterValidation() {
        return scrumMasterValidator.getValidationStatus();
    }
    
    protected ValidationStatus teamMembersValidation() {
        return teamMembersValidator.getValidationStatus();
    }
    
    protected ValidationStatus devTeamValidation() {
        return devTeamValidator.getValidationStatus();
    }
    
    protected ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}
